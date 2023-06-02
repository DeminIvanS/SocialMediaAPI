package example.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRequest {

    String title;
    @JsonProperty("post_text")
    String postText;
    List<String> tags;
    @JsonProperty("get_delete")
    Boolean getDelete;
}
