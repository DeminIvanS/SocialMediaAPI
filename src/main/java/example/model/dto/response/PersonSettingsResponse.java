package example.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import example.model.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonSettingsResponse {

    @JsonProperty("type")
    private NotificationType type;
    @JsonProperty("enable")
    private Boolean enable;
}
