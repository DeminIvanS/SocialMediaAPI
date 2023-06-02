package example.repository;

import example.handler.exception.EntityNotFoundException;
import example.mapper.PersonSettingsMapper;
import example.model.entity.PersonSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PersonSettingsRepository {
    private final RowMapper<PersonSetting> rowMapper = new PersonSettingsMapper();
    private final JdbcTemplate jdbcTemplate;

    public void save(Integer personId) {
        String sqlQuery = "INSERT INTO person_settings (person_id) VALUE (?)";
        jdbcTemplate.update(sqlQuery, personId);
    }

    public void update(PersonSetting personSetting) {
        String sqlQuery = "UPDATE person_setting SET post_comment_notification = ?, " +
                "comment_comment_notification = ?, friend_birthday_notification = ? " +
                "like_notification = ?, post_notification = ?, message_notification = ? " +
                "friend_request_notification = ?, WHERE person_id = ?";
        jdbcTemplate.update(sqlQuery,
                personSetting.getPostCommentNotification(),
                personSetting.getCommentCommentNotification(),
                personSetting.getFriendRequestNotification(),
                personSetting.getMessageNotification(),
                personSetting.getFriendBirthdayNotification(),
                personSetting.getLikeNotification(),
                personSetting.getPostNotification(),
                personSetting.getPersonId());
    }

    public PersonSetting findByPersonId(Integer personId) {
        try {
            String sqlQuery = "SELECT * FROM person_setting WHERE person_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, rowMapper, personId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person_id = " + personId);
        }
    }

    public void delete(Integer personId) {
        try {
            String sqlQuery = "DELETE FROM person_setting WHERE person_id = " + personId;
            jdbcTemplate.update(sqlQuery);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person_id = " + personId);
        }
    }
}
