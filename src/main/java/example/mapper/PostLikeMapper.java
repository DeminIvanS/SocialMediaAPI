package example.mapper;

import example.model.entity.PostLike;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostLikeMapper implements RowMapper<PostLike> {
    @Override
    public PostLike mapRow(ResultSet resultSet, int i) throws SQLException {

        return PostLike.builder()
                .id(resultSet.getInt("id"))
                .time(resultSet.getTimestamp("time").getTime())
                .personId(resultSet.getInt("person_id"))
                .postId(resultSet.getInt("post_id"))
                .type(resultSet.getString("type"))
                .build();
    }
}
