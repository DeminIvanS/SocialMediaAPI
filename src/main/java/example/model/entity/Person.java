package example.model.entity;

import example.model.enums.MessagePermission;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Person {
    private Integer id;
    private String firstName;
    private String lastName;
    private Long regDate;
    private Long birthDate;
    private String email;
    private String phone;
    private String pass;
    private String photo;
    private String about;
    private String city;
    private String country;
    private Integer confirmCode;
    private Boolean isApproved;
    private MessagePermission messagePermission;
    private Long lastOnlineTime;
    private Boolean isBlocked;
    private String token;
    private String changePassToken;
    private String notificationSessionId;
    private String onlineStatus;
    private Boolean isDelete;
    private Long deletedTime;
}
