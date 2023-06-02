package example.repository;

import example.handler.exception.EntityNotFoundException;
import example.mapper.NotificationMapper;
import example.model.entity.Notification;
import example.model.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {

    private final RowMapper<Notification> rowMapper = new NotificationMapper();
    private final JdbcTemplate jdbcTemplate;

    public Notification findById(int id) {
        try {
            String sqlQuery = "SELECT * FROM notification WHERE id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, rowMapper, id);
        }catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("notification id = " + id);
        }
    }
    public List<Notification> findByPersonId(int personId) {
        try {
            String sqlQuery = "SELECT * FROM notification WHERE person_id = ?";
            return jdbcTemplate.query(sqlQuery, rowMapper, personId);
        }catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person_id = " + personId);
        }
    }
    public void updateReadStatus (Notification notification) {
        String sqlQuery = "UPDATE notification SET is_read = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, notification.isRead(), notification.getId());
    }
    public void save(Notification notification){
        String sqlQuery = "INSERT INTO notification (notification_type,sent_time, person_id, " +
        "entity_id, contact, is_read) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, notification.getNotificationType().name(), new Timestamp(notification.getSentTime()),
                notification.getPersonId(), notification.getEntityId(), notification.getContact(), notification.isRead());
    }
    public void deleteFromType(NotificationType notificationType, Integer personId, Integer entityId) {
        String sqlQuery = "DELETE FROM notification WHERE notification_type = ? AND person_id = ? AND entity_id = ?";
        jdbcTemplate.update(sqlQuery, notificationType.name(), personId, entityId);
    }

}
