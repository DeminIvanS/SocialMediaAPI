package example.repository;

import example.handler.exception.EntityNotFoundException;
import example.handler.exception.ErrorException;
import example.handler.exception.UnableCreateEntityException;
import example.mapper.MessageMapper;
import example.model.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class MessageRepository {
    private final MessageMapper rowMapper = new MessageMapper();
    private final JdbcTemplate jdbcTemplate;

    public Integer save(Message message) {
        String sqlQuery = "INSERT INTO message(time, author_id, recipient_id, message_text, read_status, dialog_id)" +
                " VALUE (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"id"});
                ps.setTimestamp(1, Timestamp.valueOf(message.getTime()));
                ps.setInt(2, message.getAuthorId());
                ps.setInt(3, message.getRecipientId());
                ps.setString(4, message.getMessageText());
                ps.setString(5, message.getReadStatus().toString());
                ps.setInt(6, message.getDialogId());
                return ps;
            }, keyHolder);
        }catch (DataAccessException e) {
            throw new UnableCreateEntityException("message with author id = " + message.getAuthorId() +
                    " recipient id = " + message.getRecipientId() + " with text '" + message.getMessageText() +
                    "' cannot be sent");
        }
        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }
    public void update(Message message) {
        String sqlQuery = "UPDATE message SET time = ?, message_text = ?, is_delete = ? WHERE id = ?";
        try {
            jdbcTemplate.update(sqlQuery, message.getTime(), message.getMessageText());
        }catch (DataAccessException e) {
            throw new UnableCreateEntityException("message_id = " + message.getId());
        }
    }
    public Message findById(Integer id) {
        String sqlQuery = "SELECT * FROM message WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, rowMapper, id);
        }catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("message id = " + id);
        }
    }
    public List<Message> findByDialogId(Integer dialogId, Integer offset, Integer limit){
        String sqlQuery = "SELECT * FROM message WHERE dialog_id = " + dialogId + " ORDER BY time DESC";
        sqlQuery = ((offset != null) && (limit != null)) ? sqlQuery + " offset " + offset + " limit " + limit : sqlQuery;
        try {
            return jdbcTemplate.query(sqlQuery, rowMapper);
        }catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("dialogs with person id = " + dialogId);
        }
    }
    public Integer countUnreadByRecipientId(Integer recipientId) {
        String sqlQuery = "SELECT COUNT(*) FROM message WHERE recipient_id = ? AND read_status LIKE 'SENT'";
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class, recipientId);
    }
    public Integer countByDialogId(Integer dialogId) {

        String sqlQuery = "SELECT COUNT(*) FROM message WHERE dialog_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class, dialogId);
    }

    public Integer countUnreadByDialogIdAndRecipientId(Integer dialogId, Integer recipientId) {
        String sqlQuery = "SELECT COUNT(*) FROM message WHERE dialog_id = ? AND recipient_id = ? " +
                "AND read_status LIKE 'SENT'";
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class, recipientId);
    }
    public void deleteByDialogId(Integer dialogId){
        String sqlQuery = "DELETE FROM message WHERE dialog_id = ?";
        try {
            jdbcTemplate.update(sqlQuery, dialogId);
        }catch (DataAccessException e) {
            throw new UnableCreateEntityException("message with dialog_id = " + dialogId);
        }
    }
    public Integer getPersonalCount(int id) {
        try {
            String sqlQuery = "SELECT COUNT(*) FROM message WHERE author_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        }catch (EmptyResultDataAccessException e) {
            throw  new EntityNotFoundException("id = " + id);
        }
    }
    public List<Message> getLastUndeletedByDialogId (Integer dialogId) {
        String sqlQuery = "SELECT * FROM message WHERE id = " +
                "(SELECT MAX(id) FROM message WHERE id_delete = false AND dialog_id = ?";
        return jdbcTemplate.query(sqlQuery, rowMapper, dialogId);
    }
    public List<Message> findByDialogIdAndRecipientId(Integer dialogId, Integer recipientId) {
        String sqlQuery = "SELECT * FROM message WHERE dialog_id = " + dialogId +
                " AND recipient_id = " + recipientId;
        try {
            return jdbcTemplate.query(sqlQuery, rowMapper);
        }catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("dialog with person id = " + dialogId);
        }
    }
}
