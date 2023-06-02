package example.repository;

import example.handler.exception.EntityNotFoundException;
import example.mapper.FriendshipStatusMapper;
import example.model.entity.FriendshipStatus;
import example.model.enums.FriendshipStatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FriendshipStatusRepository {
    private final RowMapper<FriendshipStatus> rowMapper = new FriendshipStatusMapper();
    private final JdbcTemplate jdbcTemplate;
    public FriendshipStatus findById(int id) {
        try {
            String sqlQuery = "SELECT * FROM friendship_status WHERE id = ?";
            return jdbcTemplate.queryForObject(sqlQuery,rowMapper, id);
        }catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("friendship_status id = " + id);
        }
    }
    public int save(FriendshipStatus friendshipStatus) {
        String sqlQuery = "INSERT INTO friendship_status (time, name, code) " +
                "VALUE (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"id"});
            ps.setTimestamp(1, Timestamp.valueOf(friendshipStatus.getTime()));
            ps.setString(2, friendshipStatus.getName());
            ps.setString(3,friendshipStatus.getCode().name());
            return ps;
        }, keyHolder);
        Optional<Number> key = Optional.of(keyHolder.getKey());
        return key.get().intValue();
    }
    public List<FriendshipStatus> getApplicationsFriendshipStatus(Integer srcPersonId, Integer id) {
        try {
            String sqlQuery = "SELECT * FROM friendship_status fs\n" +
                    "JOIN friendship f ON fs.id = f.status_id\n" +
                    "WHERE src_person_id = ? AND dst_person_id = ?";
            return jdbcTemplate.query(sqlQuery, rowMapper, srcPersonId, id);
        }catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person id = " + id + " or " + srcPersonId);
        }
    }
    public void updateCode(Integer id, FriendshipStatusCode friendshipStatusCode) {
        String sqlQuery = "UPDATE friendship_status SET code = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, friendshipStatusCode.name(), id);
    }
    public void delete(FriendshipStatus friendshipStatus) {
        String sqlQuery = "DELETE FROM friendship_status WHERE id = " + friendshipStatus.getId();
        jdbcTemplate.update(sqlQuery);
    }
    public List<FriendshipStatus> findByPersonId(Integer dstPersonId, Integer srcPersonId) {
        String sqlQuery = "SELECT fs.* FROM friendship_status fss JOIN friendship fs ON fss.id = fs.status_id " +
                "WHERE fs.src_person_id = ? AND fs.dst_person_id = ?";
        return jdbcTemplate.query(sqlQuery, rowMapper, srcPersonId, dstPersonId);
     }
}
