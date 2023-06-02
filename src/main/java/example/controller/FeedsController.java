package example.controller;

import example.model.dto.response.ListResponse;
import example.model.dto.response.PostResponse;
import example.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
@Tag(name = "feeds", description = "Newsfeed Interaction")
public class FeedsController {
    private final PostService postService;

    @GetMapping
    public ListResponse<PostResponse> getAllPost(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage) {

        return postService.findAllPosts(offset, perPage);
    }
}
