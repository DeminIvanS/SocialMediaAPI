package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.handler.exception.EntityNotFoundException;
import example.model.dto.request.MessageRequest;
import example.model.dto.response.DialogUserShortListDto;
import example.security.jwt.JwtTokenProvider;
import example.security.jwt.JwtUser;
import example.service.KafkaProducerService;
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

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql("classpath:sql/person/insert-person.sql")
@Sql({"classpath:sql/dialog/insert-dialog.sql"})
@Transactional
public class DialogsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private KafkaProducerService kafkaProducerService;


    private final String url = "/api/v1/dialogs";
    private final ObjectMapper objectMapper = new ObjectMapper();


    private String getTokenAuthorization() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        return jwtTokenProvider.createToken(jwtUser.getUsername());
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void createDialogsWithCorrectRqIsOkResponseWithJsonData() throws Exception {
        DialogUserShortListDto rq = new DialogUserShortListDto();
        rq.setUserIds(List.of(3));

        this.mockMvc.perform(post(url).content(objectMapper.writeValueAsString(rq))
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", getTokenAuthorization()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void createDialogsWithExistsData() throws Exception {
        DialogUserShortListDto rq = new DialogUserShortListDto();
        rq.setUserIds(List.of(2));

        this.mockMvc.perform(post(url).content(objectMapper.writeValueAsString(rq))
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", getTokenAuthorization()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithUserDetails("test2@mail.ru")
    public void getUnreadWithCorrectRqIsOkResponse() throws Exception {
        this.mockMvc.perform(get(url + "/unreaded").header("Authorization", getTokenAuthorization()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void deleteDialogWithCorrectRqIsOkResponse() throws Exception {
        this.mockMvc.perform(delete(url + "/1"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void deleteDialogWithBadIdEntityNotFoundException() throws Exception {
        this.mockMvc.perform(delete(url + "/6"))
                .andDo(print()).andExpect(status().is4xxClientError())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getClass().equals(EntityNotFoundException.class));
    }



    @Test
    @WithUserDetails("test@mail.ru")
    public void editMessageWithCorrectDataIsOkResponse() throws Exception {
        var rq = new MessageRequest();
        rq.setMessageText("Text");

        this.mockMvc.perform(put(url + "/1/messages/1").content(objectMapper.writeValueAsString(rq))
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", getTokenAuthorization()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void markAsReadMessageWithCorrectDataIsOkResponse() throws Exception {
        this.mockMvc.perform(put(url + "/1/messages/1/read").header("Authorization", getTokenAuthorization()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void deleteMessageWithCorrectDataIsOkResponse() throws Exception {
        this.mockMvc.perform(delete(url + "/1/messages/1").header("Authorization", getTokenAuthorization()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void recoverMessageWithCorrectDataIsOkResponse() throws Exception {
        this.mockMvc.perform(put(url + "/1/messages/1/recover").header("Authorization", getTokenAuthorization()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
