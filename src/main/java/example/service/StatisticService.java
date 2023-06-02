package example.service;

import example.aop.DebugLogger;
import example.model.dto.response.OverallStatisticResponse;
import example.model.dto.response.PersonalStatisticResponse;
import example.model.dto.response.StatisticResponse;
import example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@DebugLogger
@RequiredArgsConstructor
public class StatisticService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PersonRepository personRepository;
    private final MessageRepository messageRepository;
    private final PersonService personService;


    public StatisticResponse getStatistics() {
        return StatisticResponse.builder()
                .overallStatisticResponse(getOverallStatistics())
                .personalStatisticResponse(getPersonalStatistics())
                .build();
    }

    private OverallStatisticResponse getOverallStatistics() {
        return OverallStatisticResponse.builder()
                .likesCount(likeRepository.getCount())
                .postsCount(postRepository.getCount())
                .commentsCount(commentRepository.getCount())
                .usersCount(personRepository.getCount())
                .build();
    }

    private PersonalStatisticResponse getPersonalStatistics() {
        var personId = personService.getAuthorizedPerson().getId();
        return PersonalStatisticResponse.builder()
                .likesCount(likeRepository.getPersonalCount(personId))
                .postsCount(postRepository.getPersonalCount(personId))
                .commentsCount(commentRepository.getPersonalCount(personId))
                .messagesCount(messageRepository.getPersonalCount(personId))
                .build();
    }

}
