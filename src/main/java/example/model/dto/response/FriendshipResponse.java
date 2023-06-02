package example.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class FriendshipResponse {
    private String error;
    private LocalDateTime timestamp;
    private ComplexResponse data;
}
