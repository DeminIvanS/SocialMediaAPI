package example.controller;

import example.model.dto.request.PostRequest;
import example.model.dto.response.ListResponse;
import example.model.dto.response.PostResponse;
import example.model.dto.response.ResponseResponse;
import example.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/{id}/wall")
public class WallController {
    private final PostService postService;
    @PostMapping
    public ResponseResponse<PostResponse> publishPost(
            @RequestParam(required = false) Long publish_date,
            @RequestBody PostRequest postRequest,
            @PathVariable(value = "id") int authorId){

        return postService.publishPost(publish_date, postRequest, authorId);
    }

    @GetMapping
    public ListResponse<PostResponse> getUserPosts(
            @PathVariable(value = "id") int authorId,
            @RequestParam (defaultValue = "0") int offset,
            @RequestParam (defaultValue = "20") int itemPerPage) {

        return postService.findAllUserPosts(authorId, offset, itemPerPage);
    }
}
