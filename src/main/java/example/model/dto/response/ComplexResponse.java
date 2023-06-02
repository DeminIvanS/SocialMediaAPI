package example.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComplexResponse {

    private Integer id;
    private Integer count;
    @JsonProperty("message_id")
    private Integer messageId;
    private String message;
}
