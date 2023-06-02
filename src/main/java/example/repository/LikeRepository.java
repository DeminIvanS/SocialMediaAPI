package example.repository;

import example.handler.exception.EntityNotFoundException;
import example.handler.exception.ErrorException;
import example.mapper.PostLikeMapper;
import example.model.entity.PostLike;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeRepository {
    private final RowMapper<PostLike> rowMapper = new PostLikeMapper();
    private final JdbcTemplate jdbcTemplate;

    public Integer addLike(long time, Integer personId, Integer postLikedId, String type) {
        try {
            String sqlQuery = "INSERT INTO post_like(time, person_id, post_id, type) VALUES " +
                    "('" + new Timestamp(time) + "', " + personId + ", " + postLikedId +
                    ", '" + type + "')";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> connection.prepareStatement(sqlQuery, new String[]{"id"}), keyHolder);
            return (Integer) keyHolder.getKey();
        }catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
    }
    public void deleteLike(String type, Integer postLikedId, Integer personId) {
        String sqlQuery = "DELETE FROM post_like WHERE post_id = ? AND type = ?";
        try {
            if(personId != null) {
                jdbcTemplate.update(sqlQuery + " AND person_id = ?", postLikedId, type);
            }else {
                jdbcTemplate.update(sqlQuery, postLikedId, type);
            }
        }catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
    }
    public List<Integer> getLikedUserList(Integer postLikedId, String type) {
        List<Integer> repeatList;

    try {
        repeatList = jdbcTemplate.query("SELECT person_id FROM post_like WHERE post_id = "
                + postLikedId + " AND type '" + type + "'", (rs, rowNum)-> rs.getInt("person_id"));
        }catch (DataAccessException e) {
        throw new ErrorException(e.getMessage());
        }
    return repeatList;
    }
    public List<Integer> isLikedByUser(Integer userId, Integer objectLikedId, String type) {

        List<Integer> retList;
        try {
            retList = jdbcTemplate.query("SELECT id FROM post_like WHERE person_id = " + userId +
                    " AND post_id = " + objectLikedId + " AND type = '" + type + "'", (rs, rowNum) -> rs.getInt("id"));
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
        return retList;
    }
    public PostLike findById(Integer id) {
        try {
            String sqlQuery = "SELECT * FROM post_like WHERE id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, rowMapper, id);
        }catch (EmptyResultDataAccessException e) {
            throw  new EntityNotFoundException("id = " + id);
        }
    }
    public Integer getCount() {
        String sqlQuery = "SELECT COUNT(*) FROM post_like";
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class);
    }
    public Integer getPersonalCount(int id) {
        try {
            String sqlQuery = "SELECT COUNT(*) FROM post_like WHERE post_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        }catch (EmptyResultDataAccessException e) {
            throw  new EntityNotFoundException("id = " + id);
        }
    }
    public PostLike findByPostAndPersonAndType(Integer postId, Integer personId, String type) {
        String sql = "SELECT * FROM post_like WHERE post_id = ? AND person_id = ? AND type = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, postId, personId, type);
    }
}
