package example.mapper;

import example.model.entity.Message;
import example.model.enums.ReadStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageMapper implements RowMapper<Message> {
    @Override
    public Message mapRow(ResultSet resultSet, int i) throws SQLException {
        return Message.builder()
                .id(resultSet.getInt("id"))
                .time(resultSet.getTimestamp("time").toLocalDateTime())
                .authorId(resultSet.getInt("author_id"))
                .recipientId(resultSet.getInt("recipient_id"))
                .messageText(resultSet.getString("message_text"))
                .readStatus(ReadStatus.valueOf(resultSet.getString("read_status")))
                .dialogId(resultSet.getInt("dialog_id"))
                .isDelete(resultSet.getBoolean("is_delete"))
                .build();

    }
}
