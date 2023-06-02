package example.mapper;

import example.model.entity.PersonSetting;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonSettingsMapper implements RowMapper<PersonSetting> {
    @Override
    public PersonSetting mapRow(ResultSet resultSet, int i) throws SQLException {

        return PersonSetting.builder()
                .id(resultSet.getInt("id"))
                .personId(resultSet.getInt("person_id"))
                .postCommentNotification(resultSet.getBoolean("post_comment_notification"))
                .commentCommentNotification(resultSet.getBoolean("comment_comment_notification"))
                .friendRequestNotification(resultSet.getBoolean("friend_request_notification"))
                .messageNotification(resultSet.getBoolean("message_notification"))
                .friendBirthdayNotification(resultSet.getBoolean("friend_birthday_notification"))
                .likeNotification(resultSet.getBoolean("like_notification"))
                .postNotification(resultSet.getBoolean("post_notification"))
                .build();
    }
}
