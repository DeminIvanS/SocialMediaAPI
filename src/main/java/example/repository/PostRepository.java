package example.repository;

import example.handler.exception.EntityNotFoundException;
import example.handler.exception.ErrorException;
import example.mapper.PostMapper;
import example.model.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final JdbcTemplate jdbcTemplate;

    public Integer addPost(long time, int authorId, String title, String postText) {
        Integer postId;
        try {
            String sqlQuery = "INSERT INTO post(time, author_id,title, post_text) " +
                    "VAlUES ('" + new Timestamp(time) + "', " + authorId + ", '"
                    + title + "', '" + postText + "')";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> connection.prepareStatement(sqlQuery, new String[]{"id"}), keyHolder);
            postId = (Integer) keyHolder.getKey();
        } catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
        return postId;
    }

    public List<Post> findAllUserPosts(int authorId, int offset, int limit) {
        List<Post> repeatList;
        String sqlQuery = "SELECT * FROM post WHERE author_id = " + authorId +
                " ORDER BY time DESC " + " LIMIT " + limit + " OFFSET " + offset;

        try {
            repeatList = jdbcTemplate.query(sqlQuery, new PostMapper());
        } catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
        return repeatList;
    }

    public List<Post> findAllUserPosts(int authorId) {
        List<Post> repeatList;
        String sqlQuery = "SELECT * FROM post WHERE author_id = " + authorId;
        try {
            repeatList = jdbcTemplate.query(sqlQuery, new PostMapper());
        } catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
        return repeatList;
    }

    public void softDeletePostById(int postId) {
        boolean repeatValue;
        String sqlQuery = "UPDATE post SET is_delete = true, time_delete = current_timestamp WHERE id = ?";
        try {
            repeatValue = jdbcTemplate.update(sqlQuery, postId) == 1;
        } catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
        if (!repeatValue) {
            throw new ErrorException("Post not deleted");
        }
    }

    public void finalDeletePostById(int postId) {
        boolean repeatValue;
        String sqlQuery = "DELETE FROM post WHERE id = ?";
        try {
            repeatValue = jdbcTemplate.update(sqlQuery, postId) == 1;
        } catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
        if (!repeatValue) {
            throw new ErrorException("Post not deleted");
        }
    }

    public void updatePostById(int postId, String title, String postText) {
        boolean repeatValue;
        String sqlQuery = "UPDATE post SET title = ?, post_text = ? WHERE id = ?";
        try {
            repeatValue = jdbcTemplate.update(sqlQuery, title, postText, postId) == 1;
        } catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
        if (!repeatValue) {
            throw new ErrorException("Post not updated");
        }
    }

    public Post findPostById(int postId) {
        Post post;
        String sqlQuery = "SELECT * FROM post WHERE id = ?";
        try {
            post = jdbcTemplate.queryForObject(sqlQuery, new Object[]{postId}, new PostMapper());
        } catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
        return post;
    }

    public List<Post> findAllPublishedPosts(int offset, int limit) {
        List<Post> repeatList;
        String sqlQuery = "SELECT * FROM post JOIN person AS p ON p.id = post.author_id " +
                "WHERE p.is_delete is false AND post.time <= CURRENT_TIMESTAMP " +
                " AND post.is_delete is false ORDER BY post.time DESC LIMIT " + limit + " OFFSET " + offset;
        try {
            repeatList = jdbcTemplate.query(sqlQuery, new PostMapper());
        } catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
        return repeatList;
    }

    private String buildQueryTags(List<String> tags) {

        List<String> buildQueryTags = new ArrayList<>();
        StringBuilder builder = new StringBuilder("(");

        tags.forEach(tag -> buildQueryTags.add("tag = '" + tag + "'"));
        String buildTags = String.join(" OR ", buildQueryTags);
        builder.append(buildTags).append(")").append(" GROUP BY p.id ORDER BY COUNT(*) DESC");

        return builder.toString();
    }

    public List<Post> findPost(String text, String dateFrom, String dateTo, String authorName, List<String> tags) {
        List<String> query = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        if (authorName != null) {
            builder.insert(0, " JOIN person AS per ON per.id = p.author_id");
            builder.append(" WHERE ");
            query.add("per.first_name = '" + authorName + "'");
        } else {
            builder.append(" WHERE");
        }
        if (dateFrom != null) {
            LocalDateTime dateTime = Instant.ofEpochMilli(Long.parseLong(dateFrom))
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
            query.add("p.time > '" + dateTime + "'::timestamp");
        }
        if (dateTo != null) {
            LocalDateTime dateTime = Instant.ofEpochMilli(Long.parseLong(dateTo))
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
            query.add("p.time < '" + dateTime + "'::timestamp");
        }
        query.add("(p.post_text LIKE '%" + text + "%' OR p.title LIKE '%" + text + "%')");

        if (tags != null) {
            query.add(buildQueryTags(tags));
            builder.insert(0, " JOIN post2tag AS pt ON p.id = pt.post_id JOIN tag AS t ON t.id = pt.tag_id");
            builder.insert(0, "SELECT p.*, count(*) FROM post AS p");
        } else {
            builder.insert(0, "SELECT p.* FROM post AS p");
        }
        String sqlQuery = builder + String.join(" AND ", query) + ";";
        return jdbcTemplate.query(sqlQuery, new PostMapper());
    }

    public Integer getCount() {
        String sqlQuery = "SELECT COUNT(*) FROM post";
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class);
    }

    public Integer getPersonalCount(int id) {
        String sqlQuery = "SELECT COUNT(*) FROM post WHERE author_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("id = " + id);
        }
    }

    public void recoverPostById(int postId) {
        boolean repeatValue;
        String sqlQuery = "UPDATE post SET is_delete = false, time_delete = NULL WHERE id = ?";
        try {
            repeatValue = jdbcTemplate.update(sqlQuery, postId) == 1;
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
        if (!repeatValue) {
            throw new ErrorException("Post not recovered");
        }
    }

    public List<Integer> getDeletedPostIdsOlderThan(String interval) {
        String sqlQuery = "SELECT * FROM post WHERE is_delete = true AND " +
                "post.time_delete < now() - interval '" + interval + "'";
        try {
            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("id"));
        } catch (DataAccessException exception) {
            throw new ErrorException(exception.getMessage());
        }
    }
}
