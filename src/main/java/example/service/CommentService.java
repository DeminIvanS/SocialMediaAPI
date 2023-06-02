package example.service;

import example.model.dto.response.CommentResponse;
import example.model.dto.response.ListResponse;
import example.model.dto.response.PersonResponse;
import example.model.dto.response.ResponseResponse;
import example.model.entity.Comment;
import example.model.entity.Person;
import example.model.enums.NotificationType;
import example.repository.CommentRepository;
import example.repository.PostRepository;
import example.util.PhotoCloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PersonService personService;
    private final CommentRepository commentRepository;
    private final LikeService likeService;
    private final NotificationService notificationService;
    public static final String COMMENT_MARKER = "Comment";
    private final PhotoCloudinary photoCloudinary;
    private final PostRepository postRepository;

    private CommentResponse convertToCommentRs(Comment comment) {

        Person person = personService.findById(comment.getAuthorId());
        PersonResponse author = PersonResponse.builder().id(person.getId()).firstName(person.getFirstName()).
                lastName(person.getLastName()).photo(photoCloudinary.getUrl(person.getId())).build();
        return CommentResponse.builder().isDelete(false).parentId(comment.getParentId()).
                commentText(comment.getCommentText()).id(comment.getId()).postId(comment.getPostId()).
                time(comment.getTime()).author(author).isBlocked(person.getIsBlocked()).subComment(new ArrayList<>())
                .subLike(likeService.countLikes(comment.getId(), COMMENT_MARKER))
                .myLike(likeService.isLikedByUser(personService.getAuthorizedPerson().getId(), comment.getId(), COMMENT_MARKER)).
                build();
    }

    public ResponseResponse<CommentResponse> addComment(int postId, String commentText, Integer parentId) {

        Person person = personService.getAuthorizedPerson();
        PersonResponse author = PersonResponse.builder().id(person.getId()).firstName(person.getFirstName()).
                lastName(person.getLastName()).photo(person.getPhoto()).build();
        Long time = System.currentTimeMillis();
        Integer commentId = commentRepository.addComment(postId, commentText, parentId, author.getId(), time);
        notificationService.createCommentNotification(postId, time, commentId, parentId);
        if (parentId != null) {
            notificationService.createSubCommentNotification(parentId, time, commentId);
        }
        CommentResponse data = CommentResponse.builder().isDelete(false).parentId(parentId).commentText(commentText).id(commentId).
                postId(postId).time(time).author(author).isBlocked(false).subComment(new ArrayList<>()).build();
        return new ResponseResponse<>("", data, null);

    }

    public ResponseResponse<CommentResponse> editComment(int postId, int commentId, String commentText, Integer parentId){

        Person person = personService.getAuthorizedPerson();
        PersonResponse author = PersonResponse.builder().id(person.getId()).firstName(person.getFirstName()).
                lastName(person.getLastName()).photo(person.getPhoto()).build();
        Long time = System.currentTimeMillis();
        commentRepository.editComment(postId, commentId, commentText, time);
        CommentResponse data = CommentResponse.builder().isDelete(false).parentId(parentId).commentText(commentText).id(commentId).
                postId(postId).time(time).author(author).isBlocked(false).subComment(new ArrayList<>()).build();
        return new ResponseResponse<>("", data, null);
    }

    public ListResponse<CommentResponse> getCommentsByPostIdInResponse(int postId, int offset, int itemPerPage) {

        return new ListResponse<>("", offset, itemPerPage, getAllUserCommentsToPost(postId, null, null));
    }

    public void deleteAllCommentsToPost(int postId) {

        List<Comment> comments = commentRepository.getAllCommentsByPostId(postId);
        if (!comments.isEmpty()) {
            comments.forEach(comment -> deleteComment(postId, comment.getId()));
        }
    }

    public ResponseResponse<CommentResponse> deleteComment(int postId, int commentId) {
        var comment = commentRepository.getCommentById(commentId);
        if (comment.getParentId() != 0) {
            var authorId = commentRepository.getCommentById(comment.getParentId()).getAuthorId();
            notificationService.deleteNotification(NotificationType.COMMENT_COMMENT, authorId, commentId);
        } else {
            var authorId = postRepository.findPostById(postId).getAuthorId();
            notificationService.deleteNotification(NotificationType.POST_COMMENT, authorId, commentId);
        }
        List<Comment> subComments = commentRepository.getAllCommentsByPostIdAndParentId(postId, commentId, null, null);
        subComments.forEach(subComment -> {
            likeService.deleteAllLikesByLikedObjectId(subComment.getId(), "Comment");
            deleteComment(subComment.getPostId(), subComment.getId());
        });
        commentRepository.deleteComment(postId, commentId);
        return new ResponseResponse<>("", CommentResponse.builder().id(commentId).build(), null);
    }

    public List<CommentResponse> getAllUserCommentsToPost(Integer postId, Integer offset, Integer limit) {

        List<Comment> commentList = commentRepository.getAllCommentsByPostIdAndParentId(postId, null, offset, limit);
        List<CommentResponse> commentRsList = commentList.stream().map(this::convertToCommentRs).collect(Collectors.toList());
        commentRsList.forEach(commentResponse -> setSubCommentsToComments(commentResponse, commentResponse.getId()));
        return commentRsList;
    }

    private void setSubCommentsToComments (CommentResponse commentRs, Integer parentId) {

        List<Comment> comments = commentRepository.getAllCommentsByPostIdAndParentId(commentRs.getPostId(),
                parentId, null, null);
        List<CommentResponse> subComments = comments.stream().map(this::convertToCommentRs).collect(Collectors.toList());
        commentRs.setSubComment(subComments);
        commentRs.getSubComment().forEach(commentRs1 -> setSubCommentsToComments(commentRs1, commentRs1.getId()));
    }

    public List<Integer> getAllCommentsToPost(Integer postId) {
        return commentRepository.getAllCommentsByPostId(postId).stream().map(Comment::getId)
                .collect(Collectors.toList());
    }
}
