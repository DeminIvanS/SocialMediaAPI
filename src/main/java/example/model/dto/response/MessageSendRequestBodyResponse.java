package example.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageSendRequestBodyResponse {

    @JsonProperty("message_text")
    private String messageText;
}
