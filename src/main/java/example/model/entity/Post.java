package example.model.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Post {
    private Integer id;
    private Long time;
    private Integer authorId;
    private String title;
    private String postText;
    private Boolean isBlocked;
    private Boolean isDelete;
}
