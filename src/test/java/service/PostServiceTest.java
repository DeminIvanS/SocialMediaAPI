package service;

import example.model.dto.request.PostRequest;
import example.model.dto.response.ListResponse;
import example.model.dto.response.PostResponse;
import example.model.dto.response.ResponseResponse;
import example.repository.PostRepository;
import example.repository.TagRepository;
import example.service.NotificationService;
import example.service.PostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class PostServiceTest {
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private TagRepository tagRepository;
    @MockBean
    private NotificationService notificationService;

    @Autowired
    PostService postService;

    private void assertResponseRs(ResponseResponse<PostResponse> response){
        assertNotNull(response);
        assertEquals("", response.getError());
        assertNotNull(response.getTimestamp());
    }

    private void assertListResponseRs(ListResponse<PostResponse> response, Integer offset, Integer itemPerPage){
        assertNotNull(response);
        assertEquals(offset, response.getOffset());
        assertEquals(itemPerPage, response.getPerPage());
        assertEquals("", response.getError());
        assertNotNull(response.getTimestamp());
    }

    @Test
    public void publishPost() {
        PostRequest testPost = PostRequest.builder().postText("Test post").tags(List.of("test_post")).title("Test post").build();
        ResponseResponse<PostResponse> response = postService.publishPost(System.currentTimeMillis(), testPost, 1);

        assertResponseRs(response);
    }


    @Test
    public void findAllPosts() {
        int offset = 0;
        int itemPerPage = 20;
        ListResponse<PostResponse> response = postService.findAllPosts(offset, itemPerPage);

        assertListResponseRs(response, offset, itemPerPage);
    }

    @Test
    public void findAllUserPosts() {
        int offset = 0;
        int itemPerPage = 20;
        int authorId = 1;
        ListResponse<PostResponse> response = postService.findAllUserPosts(authorId, offset, itemPerPage);

        assertListResponseRs(response, offset, itemPerPage);
    }

    @Test
    public void deletePost() {
        int postId = 1;
        ResponseResponse<PostResponse> response = postService.softDeletePost(postId);

        assertResponseRs(response);
    }

    @Test
    public void updatePost() {
        int postId = 0;
        String title = "post title";
        String postText = "post text";
        List<String> tags = new ArrayList<>();
        ResponseResponse<PostResponse> response = postService.updatePost(postId, title, postText, tags);

        assertResponseRs(response);
    }

    @Test
    public void findPost() {
        String text = "post text";
        Long dateFrom = System.currentTimeMillis() - 100_000;
        Long dateTo = System.currentTimeMillis();
        String authorName = "author name";
        List<String> tags = new ArrayList<>();
        int offset = 0;
        int itemPerPage = 20;
        ListResponse<PostResponse> response = postService.findPost(text, dateFrom.toString(), dateTo.toString(),
                authorName, tags, offset, itemPerPage);

        assertListResponseRs(response, offset, itemPerPage);
    }

    @Test
    public void getPost() {
        int postId = 0;
        ResponseResponse<PostResponse> response = postService.getPost(postId);

        assertResponseRs(response);
    }
}
