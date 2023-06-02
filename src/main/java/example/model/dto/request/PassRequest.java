package example.model.dto.request;

import lombok.Data;

@Data
public class PassRequest {
    private String token;
    private String pass;
}
