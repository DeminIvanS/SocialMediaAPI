package example.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserRequest {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("birth_date")
    private String birthDate;
    private String phone;

    @JsonProperty("photo_id")
    private String photoId;
    private String about;
    private String city;
    private String country;

    @JsonProperty("message_permission")
    private String messagePermission;
}
