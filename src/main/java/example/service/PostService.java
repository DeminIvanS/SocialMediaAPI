package example.service;

import example.model.dto.request.PostRequest;
import example.model.dto.response.CommentResponse;
import example.model.dto.response.ListResponse;
import example.model.dto.response.PostResponse;
import example.model.dto.response.ResponseResponse;
import example.model.entity.Post;
import example.model.enums.NotificationType;
import example.repository.PostRepository;
import example.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CommentService commentService;
    private final LikeService likeService;
    private final NotificationService notificationService;
    private final PersonService personService;
    private static final String POST_MARKER = "Post";

    private PostResponse convertToPostRs(Post post){

        if (post == null) return null;
        long timestamp = post.getTime();
        String type;
        if (Boolean.TRUE.equals(post.getIsDelete())) {
            type = "DELETED";
        } else {
            type = timestamp > System.currentTimeMillis() ? "QUEUED" : "POSTED";
        }
        List<CommentResponse> comments = commentService.getAllUserCommentsToPost(post.getId(), null, null);
        return PostResponse.builder()
                .id(post.getId())
                .time(timestamp)
                .author(personService.initialize(post.getAuthorId()))
                .title(post.getTitle())
                .likes(likeService.countLikes(post.getId(), POST_MARKER))
                .tags(tagRepository.findTagsByPostId(post.getId()))
                .commentRs(comments)
                .type(type)
                .postText(post.getPostText())
                .isBlocked(post.getIsBlocked())
                .myLike(likeService.isLikedByUser(personService.getAuthorizedPerson().getId(), post.getId(), POST_MARKER))
                .build();
    }

    public ListResponse<PostResponse> findAllPosts(int offset, int itemPerPage) {

        List<Post> posts = postRepository.findAllPublishedPosts(offset, itemPerPage);
        List<PostResponse> data = (posts != null) ? posts.stream().map(this::convertToPostRs).
                collect(Collectors.toList()) : null;
        return new ListResponse<>("", offset, itemPerPage, data);
    }

    public ListResponse<PostResponse> findAllUserPosts(int authorId, int offset, int itemPerPage) {

        List<Post> posts = postRepository.findAllUserPosts(authorId, offset, itemPerPage);
        List<PostResponse> data = (posts != null) ? posts.stream().map(this::convertToPostRs).
                collect(Collectors.toList()) : null;
        return new ListResponse<>("", offset, itemPerPage, data);
    }

    public ResponseResponse<PostResponse> softDeletePost(int postId) {

        postRepository.softDeletePostById(postId);
        return new ResponseResponse<>("", PostResponse.builder().id(postId).build(),null);
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void finalDeletePost() {

        List<Integer> postIdListForDelete = postRepository.getDeletedPostIdsOlderThan("30 days");
        postIdListForDelete.forEach(postId -> {
            tagRepository.deleteTagsByPostId(postId);
            List<Integer> commentIds = commentService.getAllCommentsToPost(postId);
            commentIds.forEach(commentId -> likeService.deleteAllLikesByLikedObjectId(commentId,
                    CommentService.COMMENT_MARKER));
            likeService.deleteAllLikesByLikedObjectId(postId, POST_MARKER);
            commentService.deleteAllCommentsToPost(postId);
            postRepository.finalDeletePostById(postId);
            notificationService.deleteNotification(NotificationType.POST, null, postId);
        });
    }

    public ResponseResponse<PostResponse> updatePost(int postId, String title, String postText, List<String> tags) {

        tagRepository.updateTagsPostId(postId, tags);
        postRepository.updatePostById(postId, title, postText);
        return new ResponseResponse<>("", convertToPostRs(postRepository.findPostById(postId)),null);
    }

    public ResponseResponse<PostResponse> publishPost(Long publishDate, PostRequest postRequest, int authorId) {

        long publishDateTime = (publishDate == null) ? System.currentTimeMillis() : publishDate;
        int postId = postRepository.addPost(publishDateTime, authorId, postRequest.getTitle(), postRequest.getPostText());
        postRequest.getTags().forEach(tag -> tagRepository.addTag(tag, postId));
        notificationService.createPostNotification(authorId, publishDateTime, postId);
        return (new ResponseResponse<>("", convertToPostRs(postRepository.findPostById(postId)),null));
    }

    public ListResponse<PostResponse> findPost (String text, String dateFrom, String dateTo, String authorName,
                                            List<String> tags,
                                            int offset, int itemPerPage) {

        List<Post> postsFound = postRepository.findPost(text, dateFrom, dateTo, authorName, tags);
        List<PostResponse> data = (postsFound != null) ? postsFound.stream().map(this::convertToPostRs).
                collect(Collectors.toList()) : null;

        return new ListResponse<>("", offset, itemPerPage, data);
    }

    public ResponseResponse<PostResponse> getPost(int postId) {

        Post post = postRepository.findPostById(postId);
        PostResponse data = (post != null) ? convertToPostRs(post) : null;
        return new ResponseResponse<>("", data, null);
    }

    @Transactional
    public ResponseResponse<PostResponse> recoverPost(int postId) {

        postRepository.recoverPostById(postId);
        Post post = postRepository.findPostById(postId);
        PostResponse data = (post != null) ? convertToPostRs(post) : null;
        return new ResponseResponse<>("", data, null);
    }
}
