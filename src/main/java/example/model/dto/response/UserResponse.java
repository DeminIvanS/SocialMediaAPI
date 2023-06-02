package example.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private String error;
    private long timestamp;
    private UserDto data;
    @JsonProperty("error_description")
    private String errorDescription;
}
