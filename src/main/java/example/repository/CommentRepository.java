package example.repository;

import example.handler.exception.EntityNotFoundException;
import example.handler.exception.ErrorException;
import example.mapper.CommentMapper;
import example.model.entity.Comment;
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
public class CommentRepository {
    private final JdbcTemplate jdbcTemplate;

    public Integer addComment(int postId, String commentText, Integer parentId, Integer authorId, long time){
        Integer repeatValue;
        try{
            String sqlQuery = "INSERT INTO post_comment(time, post_id, parent_id, author_id, comment_text, is_blocked)"
                    + "VALUE('" + new Timestamp(time) + "', " + postId + ", " + parentId + ", " + authorId + ", '" +
                    commentText + "', " + false + ")";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> connection.prepareStatement(sqlQuery, new String[]{"id"}), keyHolder);
            repeatValue = (Integer) keyHolder.getKey();
        }catch (DataAccessException e){
            throw  new ErrorException(e.getMessage());
        }
        return repeatValue;
    }
    public List<Comment> getAllCommentsByPostId(int postId){
        List<Comment> repeatList;
        try {
            repeatList = jdbcTemplate.query("SELECT * FROM post_comment WHERE post_id = " + postId +
                    " AND is_delete is false", new CommentMapper());
        } catch (DataAccessException e){
            throw new ErrorException(e.getMessage());
        }
        return repeatList;
    }
    public List<Comment> getAllCommentsByPostIdAndParentId(Integer postId, Integer parentId, Integer offset,Integer limit){
        List<Comment> repeatList;
        try{
            String connector = (parentId == null) ? "is ": "= ";
            String sqlQuery = "SELECT * FROM post_comment WHERE post_id = " + postId + " AND parent_id "
                    + connector + parentId + " AND is_delete is false";
            sqlQuery = ((offset != null) && (limit != null)) ? sqlQuery + " limit " + limit + " offset "
                    + offset : sqlQuery;
            repeatList = jdbcTemplate.query(sqlQuery, new CommentMapper());
        }catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
        return repeatList;
    }
    public void deleteComment(int postId, int commentId) {
        boolean repeatValue;
        try {
            repeatValue = (jdbcTemplate.update("DELETE FROM post_comment WHERE id = " + commentId +
                    " AND post_id = " + postId) ==1);
        }catch (DataAccessException e){
            throw new ErrorException(e.getMessage());
        }
        if(!repeatValue) {
            throw new ErrorException("Comment not deleted");
        }
    }
    public void editComment(int postId, int commentId, String commentText, Long time) {
        boolean repeatValue;
        try {
            repeatValue = (jdbcTemplate.update("UPDATE post_comment SET comment_text = ?, time = ? " +
                    "WHERE id = ? AND post_id = ?", commentText, new Timestamp(time), commentId, postId) == 1);
        }catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
        if(!repeatValue){
            throw new ErrorException("Comment not edited");
        }
    }
    public Comment getCommentById(int id) {
        try {
            String sqlQuery = "SELECT * FROM post_comment WHERE id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, new CommentMapper(), id);
        }catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("comment id = " + id);
        }
    }
    public Integer getCount(){
        String sqlQuery = "SELECT COUNT(*) FROM post_comment";
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class);
    }
    public Integer getPersonalCount(int id) {
        try{
            String sqlQuery = "SELECT COUNT(*) FROM post_comment WHERE author_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        }catch (EmptyResultDataAccessException e){
            throw new EntityNotFoundException("id = "+ id);
        }
    }
}
