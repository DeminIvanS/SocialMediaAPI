package example.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonPropertyOrder({"id", "time", "author","title", "likes", "tags", "comment",
        "type", "post_text","is_blocked", "my_like"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {
    private Integer id;
    private Long time;
    private PersonResponse author;
    private String title;
    private Integer likes;
    private List<String> tags;
    @JsonProperty("comments")
    private List<CommentResponse> commentRs;
    private String type;
    @JsonProperty("post_text")
    private String postText;
    @JsonProperty("is_blocked")
    private Boolean isBlocked;
    @JsonProperty("my_like")
    private Boolean myLike;
}
