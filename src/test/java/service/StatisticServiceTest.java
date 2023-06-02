package service;

import example.model.entity.Person;
import example.repository.*;
import example.service.PersonService;
import example.service.StatisticService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class StatisticServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private PersonService personService;

    private StatisticService statisticService;

    @Before
    public void setUp() {
        statisticService = new StatisticService(likeRepository, postRepository, commentRepository, personRepository,
                messageRepository, personService);
    }

    @Test
    public void getStatisticsGettingAllStatisticsAllDataIsOk() {

        Person person = new Person();
        person.setId(1);

        when(likeRepository.getCount()).thenReturn(20);
        when(postRepository.getCount()).thenReturn(30);
        when(commentRepository.getCount()).thenReturn(40);
        when(personRepository.getCount()).thenReturn(50);
        when(personService.getAuthorizedPerson()).thenReturn(person);
        when(likeRepository.getPersonalCount(anyInt())).thenReturn(10);
        when(postRepository.getPersonalCount(anyInt())).thenReturn(15);
        when(commentRepository.getPersonalCount(anyInt())).thenReturn(20);
        when(messageRepository.getPersonalCount(anyInt())).thenReturn(60);

        var response = statisticService.getStatistics();

        assertEquals(20, response.getOverallStatisticResponse().getLikesCount());
        assertEquals(30, response.getOverallStatisticResponse().getPostsCount());
        assertEquals(40, response.getOverallStatisticResponse().getCommentsCount());
        assertEquals(50, response.getOverallStatisticResponse().getUsersCount());
        assertEquals(10, response.getPersonalStatisticResponse().getLikesCount());
        assertEquals(15, response.getPersonalStatisticResponse().getPostsCount());
        assertEquals(20, response.getPersonalStatisticResponse().getCommentsCount());
        assertEquals(60, response.getPersonalStatisticResponse().getMessagesCount());
    }
}
