package example.repository;

import example.handler.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TagRepository {
    private final JdbcTemplate jdbcTemplate;

    public void addTag(String tagString, int postId) {
        try {
            Integer tagId;
            String sqlQuery = "SELECT id FROM tag WHERE tag = '" + tagString + "'";
            List<Integer> idTags = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("id"));
            if (idTags.isEmpty()) {
                String sqlInsertQuery = "INSERT INTO tag (tag) VALUES('" + tagString + "')";
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> connection.prepareStatement(sqlInsertQuery, new String[]{"id"}), keyHolder);
                tagId = (Integer) keyHolder.getKey();
            } else {
                Optional<Integer> id = Optional.of(idTags.get(0));
                tagId = id.get();
            }
            jdbcTemplate.update("INSERT INTO post2tag (tag_id, post_id) " +
                    "VALUES (?, ?)", tagId, postId);
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());

        }
    }

    private List<Integer> getTagIdsByPostId(int postId) throws DataAccessException {
        String sqlQuery = "SELECT tag_id FROM post2tag WHERE post_id = " + postId;
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("tag_id"));
    }

    public List<String> findTagsByPostId(int postId) {

        List<String> rpeatList;
        try {
            List<Integer> tagIds = getTagIdsByPostId(postId);
            List<String> tags = new ArrayList<>();
            tagIds.forEach(tagId -> tags.add(jdbcTemplate.queryForObject("SELECT tag FROM tag WHERE id = " + tagId, String.class)));
            rpeatList = tags;
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
        return rpeatList;
    }

    public void deleteTagsByPostId(int postId) {
        try {
            List<Integer> tagsIds = getTagIdsByPostId(postId);
            tagsIds.forEach(tagId -> {
                jdbcTemplate.update("DELETE FROM post2tag WHERE tag_id = ?", tagId);
                jdbcTemplate.update("DELETE FROM tag WHERE id = ?", tagId);
            });
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
    }
    public void updateTagsPostId(int postId, List<String> tags) {
        deleteTagsByPostId(postId);
        tags.forEach(tag -> addTag(tag,postId));
    }
}