package example.model.entity;

import lombok.Builder;
import lombok.Data;

import java.security.PrivateKey;

@Data
@Builder
public class PostLike {
    private Integer id;
    private Long time;
    private Integer personId;
    private Integer postId;
    private String type;
}
