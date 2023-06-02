package example.model.entity;

import example.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private Integer id;
    private NotificationType notificationType;
    private Long sentTime;
    private Integer personId;
    private Integer entityId;
    private String contact;
    private boolean isRead;

}
