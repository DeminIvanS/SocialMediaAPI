package example.model.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonSetting {
    private Integer id;
    private Integer personId;
    private Boolean postCommentNotification;
    private Boolean commentCommentNotification;
    private Boolean friendRequestNotification;
    private Boolean messageNotification;
    private Boolean friendBirthdayNotification;
    private Boolean likeNotification;
    private Boolean postNotification;
}
