package example.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OverallStatisticResponse {
    @JsonProperty("users_count")
    private Integer usersCount;
    @JsonProperty("posts_count")
    private Integer postsCount;
    @JsonProperty("comments_count")
    private Integer commentsCount;
    @JsonProperty("likes_count")
    private Integer likesCount;

}
