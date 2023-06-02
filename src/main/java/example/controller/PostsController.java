package example.controller;

import example.model.dto.request.PostRequest;
import example.model.dto.response.ListResponse;
import example.model.dto.response.PostResponse;
import example.model.dto.response.ResponseResponse;
import example.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/post")
@Tag(name = "posts", description = "Interaction with posts")
public class PostsController {
    private final PostService postService;

    @DeleteMapping("/{id}")
    public ResponseResponse<PostResponse> deletePost(@PathVariable(value = "id") int postId){
        return postService.softDeletePost(postId);
    }
    @PutMapping("/{id}")
    public ResponseResponse<PostResponse> updatePost(@PathVariable(value = "id") int postId,
                                         @RequestBody PostRequest postRq){
        return postService.updatePost(postId, postRq.getTitle(),
                postRq.getPostText(), postRq.getTags());
    }

    @GetMapping("/{id}")
    public ResponseResponse<PostResponse> getPost(@PathVariable(value = "id") int postId){
        return postService.getPost(postId);
    }

    @GetMapping
    public ListResponse<PostResponse> findPost(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "date_from", required = false) String dateFrom,
            @RequestParam(value = "date_to", required = false) String dateTo,
            @RequestParam(value = "author", required = false) String authorName,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "perPage", required = false, defaultValue = "20") int itemPerPage) {

        return postService.findPost(text, dateFrom, dateTo, authorName, tags, offset, itemPerPage);
    }

    @PutMapping("/{id}/recover")
    public ResponseResponse<PostResponse> recoverPost(@PathVariable(value = "id") int postId) {
        return postService.recoverPost(postId);
    }
}
