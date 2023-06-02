package example.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterRequest {

    private String email;

    @JsonProperty("pass1")
    private String pass1;

    @JsonProperty("pass2")
    private String pass2;
    private String firstName;
    private String lastName;
    private String code;
    private String codeSecret;
}
