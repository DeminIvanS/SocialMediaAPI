package example.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import example.model.dto.response.PersonResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {
    private Integer id;
    private Long time;
    @JsonProperty("post_id")
    private Integer postId;

    private Integer parentId;

    private PersonResponse author;
}
