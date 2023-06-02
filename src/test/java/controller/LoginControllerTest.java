package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.handler.exception.EntityNotFoundException;
import example.handler.exception.InvalidRequestException;
import example.model.dto.request.LoginRequest;
import example.repository.PersonRepository;
import example.service.KafkaProducerService;
import example.util.PhotoCloudinary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql("classpath:sql/person/insert-person.sql")
@Transactional
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PersonRepository personRepository;
    @MockBean
    private PhotoCloudinary photoCloudinary;
    @MockBean
    private KafkaProducerService kafkaProducerService;

    private final String loginUrl = "/api/v1/auth/login";
    private final String logoutUrl = "/api/v1/auth/logout";
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void loginCorrectRqIsOkResponseWithJsonContent() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.ru");
        request.setPass("test1234");
        when(photoCloudinary.getUrl(anyInt())).thenReturn("test");
        this.mockMvc.perform(post(loginUrl).content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void loginBadEmailRqEntityNotFoundThrown() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("bad@mail.ru");
        request.setPass("test1234");
        this.mockMvc.perform(post(loginUrl).content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getClass().equals(EntityNotFoundException.class));
    }

    @Test
    public void loginBadPasswordRqInvalidRequestThrown() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.ru");
        request.setPass("bad");
        this.mockMvc.perform(post(loginUrl).content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getClass().equals(InvalidRequestException.class));
    }

    @Test
    public void loginEmptyRequest400Response() throws Exception {
        this.mockMvc.perform(post(loginUrl))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void logoutAuthorizedRqIsOkResponse() throws Exception {
        this.mockMvc.perform(post(logoutUrl))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    public void logoutUnAuthorizedRqAccessDeniedResponse() throws Exception {
        this.mockMvc.perform(post(logoutUrl))
                .andDo(print())
                .andExpect(unauthenticated())
                .andExpect(status().is4xxClientError());
    }
}
