package example.service;

import example.handler.exception.InvalidRequestException;
import example.model.dto.response.ComplexResponse;
import example.model.dto.response.ListResponse;
import example.model.dto.response.PersonSettingsResponse;
import example.model.dto.request.PersonSettingsRequest;
import example.model.dto.response.ResponseResponse;
import example.model.enums.NotificationType;
import example.repository.PersonSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static example.model.enums.NotificationType.*;

@Service
@RequiredArgsConstructor
public class PersonSettingsService {
    private final PersonSettingsRepository personSettingsRepository;
    private final PersonService personService;

    public ListResponse<PersonSettingsResponse> getPersonSettings() {
        var personId = personService.getAuthorizedPerson().getId();
        var ps = personSettingsRepository.findByPersonId(personId);
        List<PersonSettingsResponse> data = new ArrayList<>();
        data.add(convertToResponse(POST, ps.getPostNotification()));
        data.add(convertToResponse(POST_COMMENT, ps.getPostCommentNotification()));
        data.add(convertToResponse(COMMENT_COMMENT, ps.getCommentCommentNotification()));
        data.add(convertToResponse(FRIEND_REQUEST, ps.getFriendRequestNotification()));
        data.add(convertToResponse(MESSAGE, ps.getMessageNotification()));
        data.add(convertToResponse(FRIEND_BIRTHDAY, ps.getFriendBirthdayNotification()));
        data.add(convertToResponse(POST_LIKE, ps.getLikeNotification()));

        return new ListResponse<>("", 0, 20, data);
    }

    public ResponseResponse<ComplexResponse> editPersonSettings(PersonSettingsRequest rq) {
        var personId = personService.getAuthorizedPerson().getId();
        var ps = personSettingsRepository.findByPersonId(personId);
        switch (rq.getType()) {
            case "POST":
                ps.setPostNotification(rq.getEnable());
                break;
            case "POST_COMMENT":
                ps.setPostCommentNotification(rq.getEnable());
                break;
            case "COMMENT_COMMENT":
                ps.setCommentCommentNotification(rq.getEnable());
                break;
            case "FRIEND_REQUEST":
                ps.setFriendRequestNotification(rq.getEnable());
                break;
            case "MESSAGE":
                ps.setMessageNotification(rq.getEnable());
                break;
            case "FRIEND_BIRTHDAY":
                ps.setFriendBirthdayNotification(rq.getEnable());
                break;
            case "POST_LIKE":
                ps.setLikeNotification(rq.getEnable());
                break;
            default:
                throw new InvalidRequestException("Request with - " + rq.getType() + " not found");
        }
        personSettingsRepository.update(ps);
        var data = ComplexResponse.builder().message("ok").build();
        return new ResponseResponse<>("", data, null);
    }

    private PersonSettingsResponse convertToResponse(NotificationType notificationType, boolean enable) {
        return PersonSettingsResponse.builder().type(notificationType).enable(enable).build();
    }
}
