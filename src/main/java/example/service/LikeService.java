package example.service;

import example.model.dto.response.LikeResponse;
import example.model.dto.response.ResponseResponse;
import example.model.enums.NotificationType;
import example.repository.CommentRepository;
import example.repository.LikeRepository;
import example.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PersonService personService;
    private final NotificationService notificationService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public ResponseResponse<LikeResponse> addLike(String type, Integer objectLikedId){

        long time = System.currentTimeMillis();
        Integer personId = personService.getAuthorizedPerson().getId();
        int likeId = likeRepository.addLike(time, personId, objectLikedId, type);
        notificationService.createPostLikeNotification(likeId, time, objectLikedId, type);
        LikeResponse data = LikeResponse.builder().likes(1).users(List.of(personId)).build();
        return new ResponseResponse<>("", data, null);
    }

    public ResponseResponse<LikeResponse> deleteLike(String type, Integer objectLikedId) {

        Integer personId = personService.getAuthorizedPerson().getId();
        Integer entityId = likeRepository.findByPostAndPersonAndType(objectLikedId, personId, type).getId();
        if (Objects.equals(type, "Post")) {
            var authorId = postRepository.findPostById(objectLikedId).getAuthorId();
            notificationService.deleteNotification(NotificationType.POST_LIKE, authorId, entityId);
        }
        if (Objects.equals(type, "Comment")) {
            var authorId = commentRepository.getCommentById(objectLikedId).getAuthorId();
            notificationService.deleteNotification(NotificationType.COMMENT_LIKE, authorId, entityId);
        }
        likeRepository.deleteLike(type, objectLikedId, personId);
        LikeResponse data = LikeResponse.builder().likes(1).build();
        return new ResponseResponse<>("", data, null);
    }

    public ResponseResponse<LikeResponse> getLikeList(String type, Integer objectLikedId) {

        List<Integer> likes = likeRepository.getLikedUserList(objectLikedId, type);
        LikeResponse data = LikeResponse.builder().likes(likes.size()).users(likes).build();
        return new ResponseResponse<>("", data, null);
    }

    public Boolean isLikedByUser(Integer userId, Integer objectLikedId, String type){

        List<Integer> likes = likeRepository.isLikedByUser(userId, objectLikedId, type);
        return !likes.isEmpty();
    }

    public Integer countLikes(Integer objectLikedId, String type) {

        return likeRepository.getLikedUserList(objectLikedId, type).size();
    }

    public void deleteAllLikesByLikedObjectId(Integer objectLikedId, String type) {

        likeRepository.deleteLike(type, objectLikedId, null);
    }
}
