package example.service;

import example.model.dto.request.MessageRequest;
import example.model.dto.response.*;
import example.model.entity.Dialog;
import example.model.entity.Message;
import example.model.entity.Person;
import example.model.enums.ReadStatus;
import example.repository.DialogRepository;
import example.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DialogsService {
    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final PersonService personService;
    private final NotificationService notificationService;

    public ResponseResponse<ComplexResponse> createDialog(String token, List<Integer> userIds) {

        Integer firstPersonId = personService.getPersonByToken(token).getId();
        Integer secondPersonId = userIds.get(0);
        Dialog dialog = dialogRepository.findByPersonIds(firstPersonId, secondPersonId);
        Integer dialogId;
        if (dialog == null) {
            Dialog newDialog = Dialog.builder()
                    .firstPersonId(firstPersonId)
                    .secondPersonId(secondPersonId)
                    .lastActiveTime(LocalDateTime.now())
                    .build();

            dialogId = dialogRepository.save(newDialog);
        } else {
            dialogId = dialog.getId();
        }
        ComplexResponse data = ComplexResponse.builder().id(dialogId).build();
        return new ResponseResponse<>("", data, null);
    }

    public ListResponse<DialogResponse> getDialogs(String token, Integer offset, Integer itemPerPage) {

        Person person = personService.getPersonByToken(token);
        Integer personId = person.getId();
        List<Dialog> dialogList = dialogRepository.findByPersonId(personId, offset, itemPerPage);

        List<DialogResponse> result = new ArrayList<>();
        if (!dialogList.isEmpty()) {
            for (Dialog dialog : dialogList) {
                Integer unreadCount = messageRepository.countUnreadByDialogIdAndRecipientId(dialog.getId(), personId);
                Integer recipientId = dialog.getFirstPersonId().equals(personId) ?
                        dialog.getSecondPersonId() :
                        dialog.getFirstPersonId();
                if (dialog.getLastMessageId() != 0) {
                    var lastMessage = messageRepository.findById(dialog.getLastMessageId());
                    boolean isSentByMe = (lastMessage.getAuthorId().equals(person.getId()));
                    var recipientPerson = personService.findById(recipientId);

                    result.add(DialogResponse.builder()
                            .id(dialog.getId())
                            .unreadCount(unreadCount)
                            .lastMessage(buildLastMessageRs(lastMessage, isSentByMe, personService.getPersonRs(recipientPerson)))
                            .authorId(personId)
                            .recipientId(recipientId)
                            .readStatus(lastMessage.getReadStatus())
                            .build());
                } else {
                    var recipientPerson = personService.findById(recipientId);
                    result.add(DialogResponse.builder()
                            .id(dialog.getId())
                            .unreadCount(unreadCount)
                            .lastMessage(MessageResponse.builder().recipient(personService.getPersonRs(recipientPerson)).build())
                            .authorId(personId)
                            .recipientId(recipientId)
                            .build());
                }
            }
        }
        return new ListResponse<>("", offset, itemPerPage, result);
    }

    public ResponseResponse<ComplexResponse> getUnread(String token) {

        Integer personId = personService.getPersonByToken(token).getId();
        Integer unreadCount = messageRepository.countUnreadByRecipientId(personId);
        ComplexResponse data = ComplexResponse.builder().count(unreadCount).build();

        return new ResponseResponse<>(null, data, null);
    }

    public ResponseResponse<ComplexResponse> deleteDialog(Integer dialogId) {

        Dialog dialog = dialogRepository.findById(dialogId);
        dialog.setLastMessageId(null);
        dialogRepository.update(dialog);

        messageRepository.deleteByDialogId(dialogId);
        dialogRepository.deleteById(dialogId);

        ComplexResponse data = ComplexResponse.builder().id(dialogId).build();

        return new ResponseResponse<>("", data, null);
    }

    public ResponseResponse<MessageResponse> sendMessage(String token, Integer dialogId, MessageRequest text) {

        Integer authorId = personService.getPersonByToken(token).getId();
        Dialog dialog = dialogRepository.findById(dialogId);
        Integer recipientId = dialog.getFirstPersonId().equals(authorId) ?
                dialog.getSecondPersonId() :
                dialog.getFirstPersonId();

        Message message = Message.builder()
                .time(LocalDateTime.now())
                .authorId(authorId)
                .recipientId(recipientId)
                .messageText(text.getMessageText())
                .readStatus(ReadStatus.SENT)
                .dialogId(dialogId)
                .build();

        Integer savedId = messageRepository.save(message);
        message.setId(savedId);

        dialog.setLastMessageId(savedId);
        dialog.setLastActiveTime(LocalDateTime.now());
        dialogRepository.update(dialog);

        MessageResponse data = getMessageResponse(message);
        data.setIsSentByMe(true);
        notificationService.createMessageNotification(message.getId(), System.currentTimeMillis(), recipientId, token);

        return new ResponseResponse<>("", data, null);
    }

    public ListResponse<MessageResponse> getMessagesByDialog(Integer id, Integer offset,
                                                         Integer itemPerPage, Integer personId) {

        Integer messagesCount = messageRepository.countByDialogId(id);
        List<MessageResponse> data = Collections.emptyList();

        if (messagesCount > 0) {

            data = messageRepository.findByDialogId(id, offset, itemPerPage).stream()
                    .map(this::getMessageResponse)
                    .collect(Collectors.toList());
            data.forEach(messageRs -> messageRs.setIsSentByMe(messageRs.getAuthorId().equals(personId)));
        }

        return ListResponse.<MessageResponse>builder()
                .error("")
                .timestamp(System.currentTimeMillis())
                .total(messagesCount)
                .offset(offset)
                .perPage(itemPerPage)
                .data(data)
                .build();
    }

    public ResponseResponse<MessageResponse> editMessage(Integer messageId, MessageRequest text) {

        Message message = messageRepository.findById(messageId);

        message.setMessageText(text.getMessageText());
        message.setReadStatus(ReadStatus.SENT);

        messageRepository.update(message);

        MessageResponse data = getMessageResponse(message);
        data.setIsSentByMe(true);

        return new ResponseResponse<>("", data, null);
    }

    public ResponseResponse<ComplexResponse> markAsReadMessage(Integer messageId) {

        Message message = messageRepository.findById(messageId);
        message.setReadStatus(ReadStatus.READ);
        messageRepository.update(message);

        ComplexResponse data = ComplexResponse.builder().message("ok").build();
        return new ResponseResponse<>("", data, null);
    }

    public ResponseResponse<ComplexResponse> deleteMessage(Integer dialogId, Integer messageId) {

        Message message = messageRepository.findById(messageId);
        message.setDelete(true);
        messageRepository.update(message);

        Dialog dialog = dialogRepository.findById(dialogId);
        List<Message> lastMessage = messageRepository.getLastUndeletedByDialogId(dialogId);
        if (!lastMessage.isEmpty()) {
            dialog.setLastMessageId(lastMessage.get(0).getId());
        } else dialog.setLastMessageId(null);
        dialogRepository.update(dialog);


        ComplexResponse data = ComplexResponse.builder().messageId(messageId).build();

        return new ResponseResponse<>("", data, null);
    }

    public ResponseResponse<MessageResponse> recoverMessage(Integer dialogId, Integer messageId) {
        var message = messageRepository.findById(messageId);
        message.setDelete(false);
        messageRepository.update(message);

        var dialog = dialogRepository.findById(dialogId);
        dialog.setLastMessageId(message.getId());
        dialogRepository.update(dialog);

        MessageResponse data = getMessageResponse(message);

        return new ResponseResponse<>("", data, null);
    }

    private MessageResponse buildLastMessageRs(Message lastMessage, boolean isSentByMe, PersonResponse recipientPersonRs) {
        return MessageResponse.builder()
                .id(lastMessage.getId())
                .time(Timestamp.valueOf(lastMessage.getTime()).getTime())
                .isSentByMe(isSentByMe)
                .recipient(recipientPersonRs)
                .messageText(lastMessage.getMessageText())
                .build();
    }

    private MessageResponse getMessageResponse(Message message) {
        return MessageResponse.builder().id(message.getId()).messageText(message.getMessageText())
                .recipientId(message.getRecipientId()).time(Timestamp.valueOf(message.getTime()).getTime())
                .authorId(message.getAuthorId()).readStatus(message.getReadStatus())
                .build();
    }

    public ResponseResponse<ComplexResponse> markDialogAsReadMessage(Integer dialogId, Integer recipientId) {

        recipientId = (recipientId == null) ?  personService.getAuthorizedPerson().getId() : recipientId;

        List<Message> messages = messageRepository.findByDialogIdAndRecipientId(dialogId, recipientId);
        messages.forEach(message -> markAsReadMessage(message.getId()));
        ComplexResponse data = ComplexResponse.builder().message("ok").
                count(messageRepository.countUnreadByRecipientId(recipientId)).build();
        return new ResponseResponse<>("", data, null);
    }
}
