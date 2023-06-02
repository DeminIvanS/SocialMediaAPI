package example.model.entity;

import example.model.enums.FriendshipStatusCode;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendshipStatus {

    private int id;
    private LocalDateTime time;
    private String name;
    private FriendshipStatusCode code;
}
