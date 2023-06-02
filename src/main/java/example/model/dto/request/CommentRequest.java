package example.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
@Builder
public class CommentRequest {
    @JsonProperty("parent_id")
    Integer parentId;
    @JsonProperty("comment_message")
    String commentMessage;
    @JsonProperty("get_delete")
    Boolean getDelete;
}
