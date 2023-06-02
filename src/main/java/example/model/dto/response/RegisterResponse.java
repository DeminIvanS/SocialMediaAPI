package example.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterResponse {

    private String error;
    private String email;
    private long timestamp;
    private ComplexResponse data;
    @JsonProperty("error_description")
    private String errorDescription;
}
