package example.controller;

import example.aop.InfoLogger;
import example.model.dto.request.MessageRequest;
import example.model.dto.response.*;
import example.service.DialogsService;
import example.service.PersonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dialogs")
@RequiredArgsConstructor
@InfoLogger
@Tag(name = "dialogs", description = "Interaction with dialogs and messages")
public class DialogsController {

    private final DialogsService dialogsService;
    private final PersonService personService;

    @PostMapping
    public ResponseResponse<ComplexResponse> createDialogs(
            @RequestHeader("Authorization") String token,
            @RequestBody DialogUserShortListDto userIds) {

        return dialogsService.createDialog(token, userIds.getUserIds());
    }

    @GetMapping
    public ListResponse<DialogResponse> getDialogs(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "perPage", defaultValue = "10") Integer itemPerPage) {

        return dialogsService.getDialogs(token, offset, itemPerPage);
    }

    @GetMapping("/unreaded")
    public ResponseResponse<ComplexResponse> getUnread(@RequestHeader("Authorization") String token) {

        return dialogsService.getUnread(token);
    }

    @DeleteMapping("/{id}")
    public ResponseResponse<ComplexResponse> deleteDialog(@PathVariable Integer id) {

        return dialogsService.deleteDialog(id);
    }

    @PostMapping("/{id}/messages")
    public ResponseResponse<MessageResponse> sendMessage(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @RequestBody MessageRequest text) {

        return dialogsService.sendMessage(token, id, text);
    }

    @GetMapping("/{id}/messages")
    public ListResponse<MessageResponse> getMessagesByDialog(
            @PathVariable Integer id,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "perPage", defaultValue = "10") Integer itemPerPage) {

        return dialogsService.getMessagesByDialog(id, offset, itemPerPage, personService.getAuthorizedPerson().getId());
    }

    @PutMapping("/{dialog_id}/messages/{message_id}")
    public ResponseResponse<MessageResponse> editMessage(
            @PathVariable("message_id") Integer messageId,
            @RequestBody MessageRequest text) {

        return dialogsService.editMessage(messageId, text);
    }

    @PutMapping("/{dialog_id}/messages/{message_id}/read")
    public ResponseResponse<ComplexResponse> markAsReadMessage(@PathVariable("message_id") Integer messageId) {

        return dialogsService.markAsReadMessage(messageId);
    }

    @PutMapping("/{dialog_id}/read")
    public ResponseResponse<ComplexResponse> markDialogAsReadMessage(@PathVariable("dialog_id") Integer dialogId) {

        return dialogsService.markDialogAsReadMessage(dialogId, null);
    }

    @DeleteMapping("/{dialog_id}/messages/{message_id}")
    public ResponseResponse<ComplexResponse> deleteMessage(
            @PathVariable("dialog_id") Integer dialogId,
            @PathVariable("message_id") Integer messageId) {

        return dialogsService.deleteMessage(dialogId, messageId);
    }

    @PutMapping("/{dialog_id}/messages/{message_id}/recover")
    public ResponseResponse<MessageResponse> recoverMessage(
            @PathVariable("dialog_id") Integer dialogId,
            @PathVariable("message_id") Integer messageId) {

        return dialogsService.recoverMessage(dialogId, messageId);
    }
}
