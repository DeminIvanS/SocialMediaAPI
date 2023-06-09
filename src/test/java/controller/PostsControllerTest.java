package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.model.dto.request.PostRequest;
import example.service.KafkaProducerService;
import example.util.PhotoCloudinary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"classpath:sql/person/insert-person.sql", "classpath:sql/post/insert-post.sql",
        "classpath:sql/currency/insert-currency.sql"})
@Transactional
@WithUserDetails("test@mail.ru")
public class PostsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PhotoCloudinary photoCloudinary;
    @MockBean
    private KafkaProducerService kafkaProducerService;

    private final String postUrl = "/api/v1/post";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getPost() throws Exception {

        when(photoCloudinary.getUrl(anyInt())).thenReturn("test");
        this.mockMvc.perform(get(postUrl + "/1"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void deletePost() throws Exception {

        this.mockMvc.perform(delete(postUrl + "/1"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updatePost() throws Exception {

        when(photoCloudinary.getUrl(anyInt())).thenReturn("test");
        PostRequest request = PostRequest.builder().postText("new test text").title("new test title").tags(List.of())
                .getDelete(false).build();
        this.mockMvc.perform(put(postUrl + "/1").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void recoverPost() throws Exception {

        when(photoCloudinary.getUrl(anyInt())).thenReturn("test");
        this.mockMvc.perform(put(postUrl + "/2/recover")).andDo(print()).andExpect(status().isOk());
    }
}
