package example.repository;

import example.handler.exception.EntityNotFoundException;
import example.handler.exception.UnableUpdateEntityException;
import example.mapper.DialogMapper;
import example.model.entity.Dialog;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DialogRepository {

    private final DialogMapper rowMapper = new DialogMapper();
    private final JdbcTemplate jdbcTemplate;

    public Integer save(Dialog dialog) {

        String sqlQuery = "INSERT INTO dialog (first_person_id, second_person_id, last_message_id, last_active_time) " +
                " VALUE (" + dialog.getFirstPersonId() + ", " + dialog.getSecondPersonId() + ", "
                + dialog.getLastMessageId() + ", '" + Timestamp.valueOf(dialog.getLastActiveTime()) + "')";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> connection.prepareStatement(sqlQuery, new String[]{"id"}), keyHolder);
        return (Integer) keyHolder.getKey();
    }

    public void update(Dialog dialog) {

        String sqlQuery = "UPDATE dialog SET last_message_id = ?, last_active_time = ? WHERE id = ?";

        try{
            jdbcTemplate.update(sqlQuery, dialog.getLastMessageId(), dialog.getLastActiveTime(), dialog.getId());
        }catch (DataAccessException e) {
            throw new UnableUpdateEntityException("dialog id = " + dialog.getId());
        }
    }

    public Dialog findById(Integer id) {

        try {
                String sqlQuery = "SELECT * FROM dialog WHERE id = ?";
                return jdbcTemplate.queryForObject(sqlQuery, rowMapper, id);
            }catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("id = " + id);
        }
    }

    public List<Dialog> findByPersonId(Integer id, Integer offset, Integer limit) {

        String sqlQuery = "SELECT * FROM dialog WHERE first_person_id = ? OR second_person_id = ? " +
                "ORDER BY last_active_time DESC offset ? limit ?";
        try {
            return jdbcTemplate.query(sqlQuery, rowMapper, id, id, offset, limit);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("dialog with person id = " + id);
        }
    }

        public List<Dialog> findByPersonId(Integer id) {

            String sqlQuery = "SELECT * FROM dialog WHERE first_person_id = ? OR second_person_id = ?";

            try {

                return jdbcTemplate.query(sqlQuery, rowMapper, id, id);
            }catch (EmptyResultDataAccessException e) {
                throw new EntityNotFoundException("dialog with person id = " + id);
            }
        }

    public Dialog findByPersonIds(Integer firstPersonId,Integer secondPersonId) {

        String sqlQuery = "SELECT * FROM dialog WHERE first_person_id = ? AND second_person_id = ?";

        try {

            return jdbcTemplate.queryForObject(sqlQuery, rowMapper, firstPersonId, secondPersonId);
        }catch (EmptyResultDataAccessException e) {
            try {
                return jdbcTemplate.queryForObject(sqlQuery, rowMapper, secondPersonId, firstPersonId);
            }catch (EmptyResultDataAccessException e2){
                return null;
            }
        }
    }
    public void deleteById(Integer id) {

        String sqlQuery = "DELETE FROM dialog WHERE id = ?";

        try{
            jdbcTemplate.update(sqlQuery, id);
        }catch (DataAccessException e){
            throw new UnableUpdateEntityException("dialog id = " + id);
        }
    }
}
