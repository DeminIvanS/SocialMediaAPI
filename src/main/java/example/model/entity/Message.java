package example.model.entity;

import example.model.enums.ReadStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Message implements Comparable<Message>{

    private Integer id;
    private LocalDateTime time;
    private Integer authorId;
    private Integer recipientId;
    private String messageText;
    private ReadStatus readStatus;
    private Integer dialogId;
    private boolean isDelete;

    @Override
    public int compareTo(Message m){
        return time.compareTo(m.time);
    }

}
