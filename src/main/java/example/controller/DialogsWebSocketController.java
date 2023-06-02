package example.controller;

import example.model.dto.request.MessageRequest;
import example.model.dto.request.WebSocketMessageRequest;
import example.service.DialogsService;
import example.service.PersonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Tag(name = "web-socket-dialogs", description = "Interaction with dialogs and messages by web sockets")
public class DialogsWebSocketController {
    private final DialogsService dialogsService;

    private final PersonService personService;

    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/dialogs/start_typing")
    public void startTyping(@Header("dialog_id") Integer dialogId, @Payload WebSocketMessageRequest body) {

        Map<String, Object> header = Collections.singletonMap("type", "start_typing");
        messagingTemplate.convertAndSendToUser(dialogId.toString(), "/queue/messages", body, header);
    }

    @MessageMapping("/dialogs/stop_typing")
    public void stopTyping(@Header("dialog_id") Integer dialogId, @Payload WebSocketMessageRequest body) {

        Map<String, Object> header = Collections.singletonMap("type", "stop_typing");
        messagingTemplate.convertAndSendToUser(dialogId.toString(), "/queue/messages", body, header);
    }

    @MessageMapping("/dialogs/send_message")
    public void sendMessages(@Header("token") String token,
                             @Header("dialog_id") Integer dialogId,
                             @Payload WebSocketMessageRequest request) {

        String authorId = personService.getPersonByToken(token).getId().toString();
        Map<String, Object> header = new HashMap<>();
        header.put("type", "send_messages");
        header.put("author_id", authorId);
        request.setAuthorId(authorId);
        request.setDialogId(dialogId);
        request.setToken(token);
        MessageRequest response = new MessageRequest();
        response.setMessageText(request.getMessageText());
        messagingTemplate.convertAndSendToUser(dialogId.toString(), "/queue/messages", request, header);
        dialogsService.sendMessage(token, dialogId, response);
    }

    @MessageMapping("/dialogs/mark_readed")
    public void markReaded(@Header("token") String token,
                           @Header(value = "id_message", defaultValue = "-1") Integer messageId,
                           @Header(value = "dialog_id") Integer dialogId) {

        Map<String, Object> header = new HashMap<>();
        header.put("type", "mark_readed");
        Integer authorId = personService.getPersonByToken(token).getId();
        if (messageId >= 0){
            messagingTemplate.convertAndSendToUser(dialogId.toString(), "/queue/messages",
                    dialogsService.markAsReadMessage(messageId), header);

        } else {
            messagingTemplate.convertAndSendToUser(dialogId.toString(),
                    "/queue/messages", dialogsService.markDialogAsReadMessage(dialogId, authorId), header);
        }
    }
}
