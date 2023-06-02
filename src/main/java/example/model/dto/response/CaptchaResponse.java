package example.model.dto.response;

import lombok.Data;

@Data
public class CaptchaResponse {

    private String image;
    private String code;
}
