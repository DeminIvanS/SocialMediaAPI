package service;

import example.model.dto.response.LikeResponse;
import example.model.dto.response.ResponseResponse;
import example.model.entity.Person;
import example.model.entity.PostLike;
import example.repository.LikeRepository;
import example.service.LikeService;
import example.service.NotificationService;
import example.service.PersonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LikeServiceTest {
    @MockBean
    PersonService personService;

    @MockBean
    LikeRepository likeRepository;

    @MockBean
    NotificationService notificationService;

    @Autowired
    private LikeService likeService;

    private void assertResponseRs(ResponseResponse<LikeResponse> response){

        assertNotNull(response);
        assertEquals("", response.getError());
        assertNotNull(response.getTimestamp());
    }

    @Test
    public void addLike() {

        String type = "POST";
        Integer objectLikedId = 1;
        Person person = new Person();
        person.setId(1);
        when(personService.getAuthorizedPerson()).thenReturn(person);
        ResponseResponse<LikeResponse> response = likeService.addLike(type, objectLikedId);

        assertResponseRs(response);
    }

    @Test
    public void deleteLike() {

        String type = "POST";
        Integer objectLikedId = 1;
        Person person = new Person();
        person.setId(1);
        PostLike postLike = PostLike.builder().id(1).build();

        when(personService.getAuthorizedPerson()).thenReturn(person);
        when(likeRepository.findByPostAndPersonAndType(anyInt(), anyInt(), anyString())).thenReturn(postLike);
        ResponseResponse<LikeResponse> response = likeService.deleteLike(type, objectLikedId);

        assertResponseRs(response);
    }

    @Test
    public void getLikeList() {

        String type = "POST";
        Integer objectLikedId = 1;
        ResponseResponse<LikeResponse> response = likeService.getLikeList(type, objectLikedId);

        assertResponseRs(response);
    }

    @Test
    public void isLikedByUser() {

        Integer userId = 1;
        Integer objectLikedId = 1;
        String type = "POST";
        likeService.isLikedByUser(userId, objectLikedId, type);
        verify(likeRepository, times(1)).isLikedByUser(userId, objectLikedId, type);
    }

    @Test
    public void countLikes() {

        Integer objectLikedId = 1;
        String type = "POST";
        likeService.countLikes(objectLikedId, type);
        verify(likeRepository, times(1)).getLikedUserList(objectLikedId, type);
    }

    @Test
    public void deleteAllLikesByLikedObjectId() {

        Integer objectLikedId = 1;
        String type = "POST";
        likeService.deleteAllLikesByLikedObjectId(objectLikedId, type);
        verify(likeRepository, times(1)).deleteLike(type, objectLikedId, null);
    }
}
