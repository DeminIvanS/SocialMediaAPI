package example.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import example.model.enums.FriendshipStatusCode;
import example.model.enums.MessagePermission;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonResponse {
    private Integer id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("reg_date")
    private Long regDate;
    @JsonProperty("birth_date")
    private Long birthDate;
    private String email;
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
    @JsonProperty("message_permission")
    private MessagePermission messagePermission;
    private Long lastOnlineTime;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
    private String token;
    @JsonProperty("friendship_status")
    private FriendshipStatusCode friendshipStatusCode;
    private boolean online;
    @JsonProperty("user_deleted")
    private boolean isDeleted;
}
