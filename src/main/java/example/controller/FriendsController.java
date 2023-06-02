package example.controller;

import example.aop.InfoLogger;
import example.model.dto.response.FriendshipResponse;
import example.model.dto.response.ListResponse;
import example.model.dto.response.PersonResponse;
import example.service.FriendsService;
import example.service.FriendshipService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
@InfoLogger
@Tag(name = "friends", description = "Interaction with friends")
public class FriendsController {
    private final FriendsService friendsService;
    private final FriendshipService friendshipService;

    @GetMapping("/recommendations")
    public ListResponse<PersonResponse> getRecommendations(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "perPage", defaultValue = "10") int itemPerPage) {

        return friendsService.getRecommendations(token, offset, itemPerPage);
    }

    @GetMapping
    public ListResponse<PersonResponse> getListFriends(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "perPage", required = false, defaultValue = "20") int itemPerPage) {

        return friendsService.getListFriends(offset, itemPerPage);
    }

    @PostMapping("/{id}")
    public FriendshipResponse addFriends(@PathVariable int id) {

        return friendshipService.addFriendShip(id);
    }

    @DeleteMapping("/{id}")
    public FriendshipResponse deleteFriends(@PathVariable int id) {

        return friendshipService.deleteFriend(id);
    }

    @GetMapping("/request")
    public ListResponse<PersonResponse> getListApplicationsFriends(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "perPage", required = false, defaultValue = "20") int itemPerPage) {

        return friendsService.getListApplicationsFriends(offset, itemPerPage);
    }

    @PostMapping("/request/{id}")
    public FriendshipResponse addApplicationsFriends(@PathVariable int id) {

        return friendshipService.addApplicationsFriends(id);
    }

    @DeleteMapping("/request/{id}")
    public FriendshipResponse deleteApplicationsFriends(@PathVariable int id) {

        return friendshipService.deleteApplicationsFriends(id);
    }
}
