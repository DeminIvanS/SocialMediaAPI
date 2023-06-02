package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.controller.AccountController;
import example.model.dto.request.RegisterRequest;
import example.model.dto.response.CaptchaResponse;
import example.model.entity.Captcha;
import example.repository.CaptchaRepository;
import example.repository.PersonRepository;
import example.repository.PersonSettingsRepository;
import example.service.CaptchaService;
import example.service.KafkaProducerService;
import example.util.PhotoCloudinary;
import org.junit.jupiter.api.AfterEach;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AccountController.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql("classpath:sql/person/insert-person.sql")
@Transactional
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockBean
    private CaptchaService captchaService;
    @MockBean
    private CaptchaRepository captchaRepository;
    @MockBean
    private PersonSettingsRepository personSettingsRepository;
    @MockBean
    private PhotoCloudinary photoCloudinary;
    @MockBean
    private KafkaProducerService kafkaProducerService;
    @Autowired
    private ObjectMapper objectMapper;


    private final static String registerUrl = "/api/v1/account/register";
    private final static String testEmail = "testtest@test.ru";

    @AfterEach
    public void deleteAll() {
        personRepository.deletePerson(personRepository.findByEmail(testEmail).getId());
    }

    @Test
    public void profileRegister() throws Exception {
        CaptchaResponse captchaRs = new CaptchaResponse();
        captchaRs.setCode("1234");
        Captcha captcha = Captcha.builder()
                .id(1)
                .time(LocalDateTime.now())
                .code("1234")
                .secretCode("12345")
                .build();

        when(captchaService.getCaptcha()).thenReturn(captchaRs);
        when(captchaRepository.findByCode(anyString())).thenReturn(captcha);

        String password = "12345678";

        RegisterRequest registerRq = new RegisterRequest();
        registerRq.setFirstName("Тест");
        registerRq.setLastName("Test");
        registerRq.setEmail(testEmail);
        registerRq.setPass1(password);
        registerRq.setPass2(password);
        registerRq.setCode(captchaService.getCaptcha().getCode());
        registerRq.setCodeSecret(captchaRepository.findByCode(registerRq.getCode()).getSecretCode());

        this.mockMvc.perform(post(registerUrl).content(objectMapper.writeValueAsString(registerRq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
