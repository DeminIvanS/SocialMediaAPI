package example.mapper;

import example.model.entity.Notification;
import example.model.enums.NotificationType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationMapper implements RowMapper<Notification> {
    @Override
    public Notification mapRow(ResultSet resultSet, int i) throws SQLException {
        return Notification.builder()
                .id(resultSet.getInt("id"))
                .notificationType(NotificationType.valueOf(resultSet.getString("notification_type")))
                .sentTime(resultSet.getTimestamp("sent_time").getTime())
                .personId(resultSet.getInt("person_id"))
                .entityId(resultSet.getInt("entity_id"))
                .contact(resultSet.getString("contact"))
                .isRead(resultSet.getBoolean("is_read"))
                .build();

    }
}
