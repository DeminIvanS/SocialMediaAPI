package example.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {
    private Integer id;
    private Long time;
    @JsonProperty("post_id")
    private Integer postId;
    @JsonProperty("parent_id")
    private Integer parentId;
    @JsonProperty("author")
    private PersonResponse author;
    @JsonProperty("comment_text")
    private String commentText;
    @JsonProperty("is_blocked")
    private Boolean isBlocked;
    @JsonProperty("is_delete")
    private Boolean isDelete;
    @JsonProperty("sub_comment")
    List<CommentResponse> subComment;
    @JsonProperty("likes")
    private Integer subLike;
    @JsonProperty("my_like")
    private Boolean myLike;

}
