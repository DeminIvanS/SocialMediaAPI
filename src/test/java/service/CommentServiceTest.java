package service;

import example.model.dto.response.CommentResponse;
import example.model.dto.response.ListResponse;
import example.model.dto.response.ResponseResponse;
import example.model.entity.Comment;
import example.model.entity.Person;
import example.repository.CommentRepository;
import example.service.CommentService;
import example.service.NotificationService;
import example.service.PersonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class CommentServiceTest {
    @MockBean
    PersonService personService;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    NotificationService notificationsService;

    @Autowired
    CommentService commentService;

    private void assertResponseRs(ResponseResponse<CommentResponse> response){

        assertNotNull(response);
        assertEquals("", response.getError());
        assertNotNull(response.getTimestamp());
    }

    private void assertListResponse(ListResponse<CommentResponse> response, Integer offset, Integer itemPerPage){

        assertNotNull(response);
        assertEquals(offset, response.getOffset());
        assertEquals(itemPerPage, response.getPerPage());
        assertEquals("", response.getError());
        assertNotNull(response.getTimestamp());
    }

    @Test
    public void addComment() {

        int postId = 0;
        String commentText = "comment text";
        Integer parentId = 0;
        Person person = new Person();
        person.setId(1);
        person.setFirstName("FirstName");
        person.setLastName("LastName");
        person.setPhoto("");
        when(personService.getAuthorizedPerson()).thenReturn(person);
        ResponseResponse<CommentResponse> response = commentService.addComment(postId, commentText, parentId);

        assertResponseRs(response);
    }

    @Test
    public void editComment() {

        int postId = 0;
        int commentId = 0;
        String commentText = "comment text";
        Integer parentId = 0;
        Person person = new Person();
        person.setId(1);
        person.setFirstName("FirstName");
        person.setLastName("LastName");
        person.setPhoto("");
        when(personService.getAuthorizedPerson()).thenReturn(person);
        ResponseResponse<CommentResponse> response = commentService.editComment(postId, commentId, commentText, parentId);

        assertResponseRs(response);
    }

    @Test
    public void getCommentsByPostIdInResponse() {

        int postId = 0;
        int offset = 0;
        int itemPerPage = 20;
        ListResponse<CommentResponse> response = commentService.getCommentsByPostIdInResponse(postId, offset, itemPerPage);

        assertListResponse(response, offset, itemPerPage);
    }

    @Test
    public void deleteAllCommentsToPost() {

        int postId = 0;
        commentService.deleteAllCommentsToPost(postId);

        verify(commentRepository, times(1)).getAllCommentsByPostId(postId);
    }

    @Test
    public void deleteComment() {

        int postId = 0;
        int commentId = 0;

        Comment comment = Comment.builder().parentId(1).build();

        when(commentRepository.getCommentById(anyInt())).thenReturn(comment);

        ResponseResponse<CommentResponse> response = commentService.deleteComment(postId, commentId);

        assertResponseRs(response);
    }

    @Test
    public void initializeCommentsToPost() {

        Integer postId = 0;
        Integer offset = 0;
        Integer limit = 20;
        List<CommentResponse> commentRsList = commentService.getAllUserCommentsToPost(postId, offset, limit);

        assertNotNull(commentRsList);
        verify(commentRepository, times(1)).getAllCommentsByPostIdAndParentId(postId,
                null, offset, limit);
    }
}
