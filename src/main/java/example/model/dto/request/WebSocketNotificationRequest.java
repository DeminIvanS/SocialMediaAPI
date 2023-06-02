package example.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketNotificationRequest {

    @JsonProperty("websocket_user_id")
    private String websocketUserId;
    @JsonProperty("person_id")
    private Integer personId;
}
