package example.controller;

import example.aop.InfoLogger;
import example.model.dto.request.PostRequest;
import example.model.dto.request.UserRequest;
import example.model.dto.response.*;
import example.service.LoginService;
import example.service.PersonService;
import example.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@InfoLogger
@Tag(name = "users", description = "User interaction")
public class UserController {
    private final PersonService personService;
    private final LoginService loginService;
    private final PostService postService;

    @GetMapping("/search")
    public ListResponse<PersonResponse> searchPeople(
            @RequestParam(value = "first_name", required = false) String firstName,
            @RequestParam(value = "last_name", required = false) String lastName,
            @RequestParam(value = "age_from", required = false) Integer ageFrom,
            @RequestParam(value = "age_to", required = false) Integer ageTo,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "perPage", required = false, defaultValue = "20") int itemPerPage) {

        return personService.findPerson(firstName, lastName, ageFrom, ageTo, city, country,
                offset, itemPerPage);
    }

    @GetMapping("me")
    public ResponseResponse<PersonResponse> profileResponse(@RequestHeader("Authorization") String token) {
        return loginService.profileResponse(token);
    }

    @PutMapping("/me")
    public UserResponse editUser(@RequestBody UserRequest request, @RequestHeader("Authorization") String token) {
        return personService.editUser(request, token);
    }

    @PostMapping("/{id}/wall")
    public ResponseResponse<PostResponse> publishPost(@RequestParam(required = false, name = "publish_date") Long publishDate,
                                                      @RequestBody PostRequest postRq, @PathVariable(value = "id") int authorId) {

        return postService.publishPost(publishDate, postRq, authorId);
    }

    @GetMapping("/{id}/wall")
    public ListResponse<PostResponse> getUserPosts(@PathVariable(value = "id") int authorId,
                                               @RequestParam (defaultValue = "0") int offset,
                                               @RequestParam (defaultValue = "20") int itemPerPage) {

        return postService.findAllUserPosts(authorId, offset, itemPerPage);
    }

    @GetMapping("/{id}")
    public ResponseResponse<PersonResponse> getUserInfo(@PathVariable(value = "id") int userId) {

        return personService.getUserInfo(userId);
    }

    @DeleteMapping("/me")
    public ResponseResponse<ComplexResponse> deleteUser(@RequestHeader("Authorization") String token) {
        return personService.deleteUser(token);
    }

    @PostMapping("/me/recover")
    public ResponseResponse<ComplexResponse> publishPost(@RequestHeader("Authorization") String token) {

        return personService.recoverUser(token);
    }
}
