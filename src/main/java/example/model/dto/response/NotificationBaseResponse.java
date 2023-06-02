package example.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import example.model.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationBaseResponse {
    private Integer id;
    @JsonProperty("notification_type")
    private NotificationType notificationType;
    @JsonProperty("sent_time")
    private Long sentTime;
    private String info;
    @JsonProperty("entity_author")
    private PersonResponse entityAuthor;
    @JsonProperty("entity_id")
    private Integer entityId;
}
