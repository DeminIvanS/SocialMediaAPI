package example.repository;

import example.handler.exception.EntityNotFoundException;
import example.mapper.FriendshipMapper;
import example.model.entity.Friendship;
import example.model.enums.FriendshipStatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendshipRepository {
    public static final String PERSON_ID = "person id = ";
    private final RowMapper<Friendship> rowMapper = new FriendshipMapper();
    private final JdbcTemplate jdbcTemplate;

    public List<Friendship> getStatus(int id, int srcPersonId){
        String sqlQuery = "SELECT f. * FROM public.friendship f\n" +
                "JOIN friendship_status fs on f.status_id = fs.id\n" +
                "WHERE (fs.code = 'REQUEST' OR fs.code = 'FRIEND') AND f.src_person_id = ? and f.dst_person_id = ? " +
                "OR (fs.code = 'REQUEST' OR fs.code = 'FRIEND') AND f.src_person_id = ? and f.dst_person_id = ? ";
        return jdbcTemplate.query(sqlQuery, rowMapper, id, srcPersonId, srcPersonId, id);
    }
    public void save(Friendship friendship) {
        String sqlQuery = "INSERT INTO friendship(status_id, sent_time, src_person_id, dst_person_id " +
                "VALUE (?,?,?,?)";
        jdbcTemplate.update(sqlQuery, friendship.getStatusId(), friendship.getSentTime(),
                friendship.getSrcPersonId(), friendship.getDstPersonId());
    }
    public List<Friendship> findByPersonId(int id){
        try {
            String sqlQuery = "SELECT * FROM friendship WHERE src_person_id = ? OR dst_person_id = ?";
            return jdbcTemplate.query(sqlQuery, rowMapper, id, id);
        }catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(PERSON_ID + id);
        }
    }
    public List<Friendship> findByPersonIdAndStatus(Integer id, FriendshipStatusCode statusCode) {
        try {
            String sqlQuery = "SELECT * FROM friendship fs " +
                    "JOIN friendship_status fss ON fs.status_id = fss.id " +
                    "WHERE(src_person-id = ? OR dst_person_id = ? AND code LIKE ?";
            return jdbcTemplate.query(sqlQuery, rowMapper, id, id, statusCode.toString());
        }catch (EmptyResultDataAccessException e){
            throw new EntityNotFoundException(PERSON_ID + id + " and status = " + statusCode);
        }
    }
    public void delete(Friendship friendship){
        try {
            String sqlQuery = "DELETE FROM friendship WHERE src_person_id = " + friendship.getSrcPersonId() +
                    " AND dst_person_id = " + friendship.getDstPersonId();
            jdbcTemplate.update(sqlQuery);
        }catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(PERSON_ID + friendship.getSrcPersonId() +
                    " or " + friendship.getDstPersonId());
        }
    }
    public List<Friendship> findByFriendship(int srcPersonId, int dstPersonId) {
        try {
            String sqlQuery = "SELECT * FROM friendship WHERE src_person_id = ? AND dst_person_id = ?";
            return jdbcTemplate.query(sqlQuery, rowMapper,srcPersonId,dstPersonId);
        }catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(PERSON_ID + srcPersonId + " or " + dstPersonId);
        }
    }
    public Friendship findOneByIdAndFriendshipStatus(int srcPersonId, int dstPersonId, int statusId) {
        try {
            String sqlQuery = "SELECT * FROM friendship WHERE src_person_id = ? AND dst_person_id = ? AND status_id";
            return jdbcTemplate.queryForObject(sqlQuery, rowMapper, srcPersonId, dstPersonId, statusId);
        }catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(PERSON_ID + srcPersonId + " or " + dstPersonId + " or " + statusId);
        }
    }
    public List<Friendship> findAllFriendsByPersonId (Integer id) {
        try {
            String sqlQuery = "SELECT * FROM friendship fs " +
                    "JOIN friendship_status fss ON fs.status_id = fss.id " +
                    "WHERE (src_person_id = ?) AND code LIKE 'FRIEND'";
            return jdbcTemplate.query(sqlQuery, rowMapper, id);
        }catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(PERSON_ID + id);
        }
    }
    public Friendship findById(Integer id) {
        try {
            String sqlQuery = "SELECT * FROM friendship WHERE id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, rowMapper, id);
        }catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("id = " + id);
        }
    }
}
