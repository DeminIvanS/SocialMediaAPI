package controller;

import example.security.jwt.JwtTokenProvider;
import example.security.jwt.JwtUser;
import example.service.KafkaProducerService;
import example.service.LoginService;
import example.util.PhotoCloudinary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql("classpath:sql/person/insert-person.sql")
@Sql({"classpath:sql/notification/insert-notification.sql"})
@Transactional
public class NotificationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginService loginService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private PhotoCloudinary photoCloudinary;
    @MockBean
    private KafkaProducerService kafkaProducerService;

    private final String notificationUrl = "/api/v1/notifications";


    @Test
    @WithUserDetails("test@mail.ru")
    public void getNotificationsAuthorizedPersonIsOkResponseWithJsonContent() throws Exception {

        when(photoCloudinary.getUrl(anyInt())).thenReturn("test");

        this.mockMvc.perform(get(notificationUrl).header("Authorization", getTokenAuthorization()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void markAsReadNotificationsAuthorizedPersonWithAllTrueIsOkResponseWithJsonContent() throws Exception {

        when(photoCloudinary.getUrl(anyInt())).thenReturn("test");

        this.mockMvc.perform(put(notificationUrl).header("Authorization", getTokenAuthorization())
                        .param("all", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private String getTokenAuthorization() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        return jwtTokenProvider.createToken(jwtUser.getUsername());
    }
}
