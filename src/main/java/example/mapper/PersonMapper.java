package example.mapper;

import example.model.entity.Person;
import example.model.enums.MessagePermission;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet resultSet, int i) throws SQLException {
        Person person = new Person();

        person.setId(resultSet.getInt("id"));
        person.setFirstName(resultSet.getString("first_name"));
        person.setLastName(resultSet.getString("last_name"));
        person.setRegDate(resultSet.getTimestamp("reg_date").getTime());
        person.setBirthDate(resultSet.getTimestamp("birth_date") == null ? null :
                resultSet.getTimestamp("birth_date").getTime());
        person.setEmail(resultSet.getString("email"));
        person.setPhone(resultSet.getString("phone"));
        person.setPass(resultSet.getString("pass"));
        person.setPhoto(resultSet.getString("photo"));
        person.setAbout(resultSet.getString("about"));
        person.setCity(resultSet.getString("city"));
        person.setCountry(resultSet.getString("country"));
        person.setConfirmCode(resultSet.getInt("confirmation_code"));
        person.setIsApproved(resultSet.getBoolean("is_approved"));
        person.setMessagePermission(resultSet.getString("message_permission") == null ? MessagePermission.ALL :
                MessagePermission.valueOf(resultSet.getString("message_permission")));
        person.setLastOnlineTime(resultSet.getTimestamp("last_online_time") == null ? null :
                resultSet.getTimestamp("last_online_time").getTime());
        person.setIsBlocked(resultSet.getBoolean("is_blocked"));
        person.setNotificationSessionId(resultSet.getString("notification_session_id"));
        person.setOnlineStatus(resultSet.getString("online_status"));
        person.setIsDelete(resultSet.getBoolean("is_delete"));
        person.setDeletedTime(resultSet.getTimestamp("delet_time") == null ? null :
                resultSet.getTimestamp("delete_time").getTime());
        return person;
    }
}
