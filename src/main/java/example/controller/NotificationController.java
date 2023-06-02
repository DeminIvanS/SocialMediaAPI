package example.controller;

import example.aop.InfoLogger;
import example.model.dto.response.ListResponse;
import example.model.dto.response.NotificationBaseResponse;
import example.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@InfoLogger
@Tag(name = "notifications", description = "Interaction with notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ListResponse<NotificationBaseResponse> getNotifications(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "10") int itemPerPage) {

        return notificationService.getNotifications(token, offset, itemPerPage);
    }

    @PutMapping
    public ListResponse<NotificationBaseResponse> markAsReadNotification(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "id", defaultValue = "0") int id,
            @RequestParam(value = "all", defaultValue = "false") boolean all) {

        return notificationService.markAsReadNotification(token, id, all);
    }
}
