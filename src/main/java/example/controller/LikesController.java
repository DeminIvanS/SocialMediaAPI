package example.controller;

import example.model.dto.request.LikeRequest;
import example.model.dto.response.LikeResponse;
import example.model.dto.response.ResponseResponse;
import example.service.LikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
@Tag(name = "likes", description = "Interaction with likes")
public class LikesController {
    private final LikeService likeService;

    @PutMapping
    public ResponseResponse<LikeResponse> putLike(@RequestBody LikeRequest likeRq) {

        return likeService.addLike(likeRq.getType(), likeRq.getItemId());
    }

    @DeleteMapping
    public ResponseResponse<LikeResponse> deleteLike(@RequestParam("item_id") Integer itemId,
                                         @RequestParam String type) {

        return likeService.deleteLike(type, itemId);
    }

    @GetMapping
    public ResponseResponse<LikeResponse> getLikeList(@RequestParam("item_id") Integer itemId,
                                          @RequestParam String type) {

        return likeService.getLikeList(type, itemId);
    }
}
