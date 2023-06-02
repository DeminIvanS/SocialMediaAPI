package example.mapper;

import example.model.entity.Dialog;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DialogMapper implements RowMapper<Dialog> {
    @Override
    public Dialog mapRow(ResultSet resultSet, int i) throws SQLException {

        return Dialog.builder()
                .id(resultSet.getInt("id"))
                .firstPersonId(resultSet.getInt("first_person_id"))
                .secondPersonId(resultSet.getInt("second_person_id"))
                .lastMessageId(resultSet.getInt("last_message_id"))
                .lastActiveTime(resultSet.getTimestamp("last_active_time").toLocalDateTime())
                .build();
    }
}
