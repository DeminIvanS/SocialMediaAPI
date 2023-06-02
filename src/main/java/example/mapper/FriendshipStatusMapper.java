package example.mapper;

import example.model.entity.FriendshipStatus;
import example.model.enums.FriendshipStatusCode;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendshipStatusMapper implements RowMapper<FriendshipStatus> {
    @Override
    public FriendshipStatus mapRow(ResultSet resultSet, int i) throws SQLException {
        FriendshipStatus friendshipStatus = new FriendshipStatus();

        friendshipStatus.setId(resultSet.getInt("id"));
        friendshipStatus.setTime(resultSet.getTimestamp("time").toLocalDateTime());
        friendshipStatus.setName(resultSet.getString("name"));
        friendshipStatus.setCode(FriendshipStatusCode.valueOf(resultSet.getString("code")));
        return friendshipStatus;
    }
}
