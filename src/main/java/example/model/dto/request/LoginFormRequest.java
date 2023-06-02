package example.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginFormRequest {

    private String email;
    private String pass;

    public LoginFormRequest(@JsonProperty("email") String email, @JsonProperty("pass") String pass){
        this.email = email;
        this.pass = pass;
    }
}
