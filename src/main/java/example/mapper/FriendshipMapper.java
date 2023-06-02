package example.mapper;

import example.model.entity.Friendship;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendshipMapper implements RowMapper<Friendship> {
    @Override
    public Friendship mapRow(ResultSet resultSet, int i) throws SQLException {

        Friendship friendship = new Friendship();

        friendship.setId(resultSet.getInt("id"));
        friendship.setStatusId(resultSet.getInt("status_id"));
        friendship.setSentTime(resultSet.getTimestamp("sent_time").toLocalDateTime());
        friendship.setSrcPersonId(resultSet.getInt("src_person-id"));
        friendship.setDstPersonId(resultSet.getInt("dst_person_id"));

        return friendship;
    }
}
