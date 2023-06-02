package service;

import example.handler.exception.InvalidRequestException;
import example.model.dto.request.LoginRequest;
import example.model.dto.response.ComplexResponse;
import example.model.dto.response.PersonResponse;
import example.model.dto.response.ResponseResponse;
import example.model.entity.Person;
import example.repository.PersonRepository;
import example.security.jwt.JwtTokenProvider;
import example.service.LoginService;
import example.util.PhotoCloudinary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LoginServiceTest {
    @Mock
    private PersonRepository personRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Mock
    private PhotoCloudinary photoCloudinary;


    private LoginService loginService;

    @Before
    public void setUp() {
        loginService = new LoginService(jwtTokenProvider, personRepository, passwordEncoder,
                photoCloudinary);
    }

    @Test
    public void profileResponseAuthorizedRqAllDataIsOk() {
        String token = "token";

        Person person = new Person();
        person.setEmail("email");
        person.setPass("pass");
        person.setIsBlocked(false);
        person.setIsDelete(false);


        Integer expectedOffset = 0;
        Integer expectedPerPage = 20;

        when(jwtTokenProvider.getUsername(token)).thenReturn("email");
        when(personRepository.findByEmail("email")).thenReturn(person);


        var response = loginService.profileResponse(token);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals("string", response.getError());
        assertEquals(expectedOffset, response.getOffset());
        assertEquals(expectedPerPage, response.getPerPage());
    }

    @Test
    public void loginCorrectRqAllDataIsOk() {
        var password = passwordEncoder.encode("test1234");
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@mail.ru");
        loginRequest.setPass("test1234");

        Person person = new Person();
        person.setEmail("test@mail.ru");
        person.setPass(password);
        person.setIsBlocked(false);

        when(personRepository.findByEmail(loginRequest.getEmail())).thenReturn(person);
        when(jwtTokenProvider.createToken(anyString())).thenReturn("");

        ResponseResponse<PersonResponse> response = loginService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getData().getToken());
        assertNotNull(response.getData());
        assertEquals("", response.getError());
        assertTrue(response.getTimestamp() instanceof Long);

        verify(jwtTokenProvider, times(1)).createToken(loginRequest.getEmail());
    }

    @Test
    public void loginBadPasswordRqIncorrectPasswordThrown() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@mail.ru");
        loginRequest.setPass("test");

        Person person = new Person();
        person.setEmail("test@mail.ru");
        person.setPass("wrong");
        person.setIsBlocked(false);

        when(personRepository.findByEmail(any())).thenReturn(person);
        when(jwtTokenProvider.createToken(anyString())).thenReturn("");

        InvalidRequestException thrown = assertThrows(InvalidRequestException.class,
                () -> loginService.login(loginRequest));

        assertEquals("Неверный пароль.", thrown.getMessage());

        verify(jwtTokenProvider, times(0)).createToken(loginRequest.getEmail());
    }

    @Test
    public void logoutAuthorizedRqAllDataIsOk() {
        ResponseResponse<ComplexResponse> response = loginService.logout();

        var expectedData = ComplexResponse.builder().message("ok").build();

        assertNotNull(response);
        assertEquals("", response.getError());
        assertTrue(response.getTimestamp() instanceof Long);
        assertNotNull(response.getData());
        assertEquals(expectedData, response.getData());
    }
}
