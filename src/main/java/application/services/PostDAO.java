package application.services;

import application.Instances;
import application.models.PostModel;
import application.models.PostUpdateModel;
import application.models.ThreadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Repository
@Transactional
public class PostDAO {
    private static final PostMapper postMapper = new PostMapper();
    private static final ParentMapper parentMapper = new ParentMapper();

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PostDAO(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    public void create(final ThreadModel thread, final List<PostModel> posts) throws SQLException {
        String sql = "INSERT INTO posts (parent_id, user_id, forum_id, thread_id, is_edited, message, created, nickname, path) " +
                "VALUES (?, (SELECT id FROM users WHERE LOWER(nickname) = LOWER(?)), ?, ?, ?, ?, ?, ?, " +
                "array_append((SELECT path FROM posts WHERE id = ?), currval('posts_id_seq')::INT));";

        try {
            Connection connection = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());

            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            for (PostModel post : posts) {
                if (post.getCreated() != null) {
                    timestamp = Instances.getTimestampFromString(post.getCreated());
                }

                preparedStatement.setInt(1, post.getParent());
                preparedStatement.setString(2, post.getAuthor());
                preparedStatement.setInt(3, thread.getForumId());
                preparedStatement.setInt(4, thread.getId());
                preparedStatement.setBoolean(5, post.getIsEdited());
                preparedStatement.setString(6, post.getMessage());
                preparedStatement.setTimestamp(7, timestamp);
                preparedStatement.setString(8, post.getAuthor());
                preparedStatement.setInt(9, post.getParent());

                preparedStatement.addBatch();

                post.setThread(thread.getId());
                post.setForum(thread.getForum());
                post.setCreated(dateFormat.format(timestamp));
            }

            preparedStatement.executeBatch();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            int i = 0;
            while (rs.next()) {
                int id = rs.getInt(1);
                posts.get(i).setId(id);
                i++;
            }

            preparedStatement.close();
        } catch (BatchUpdateException e) {
            throw e;
        } catch (SQLException e) {
            throw e;
        }

        sql = "UPDATE forums SET posts = posts + ? WHERE id = ?;";
        jdbcTemplate.update(sql, posts.size(), thread.getForumId());
    }

    public List<PostModel> getPostsFlat(final ThreadModel thread, final Integer limit, final Integer offset, final Boolean desc) {
        final String SQL = "SELECT p.id, parent_id, f.slug, thread_id, nickname, is_edited, p.message, p.created " +
                "FROM posts p " +
                "JOIN forums f ON (f.id = p.forum_id) " +
                "WHERE p.thread_id = ? " +
                "ORDER BY created " + (desc ? "DESC" : "ASC") + ", id " + (desc ? "DESC" : "ASC") + " " +
                "LIMIT ? OFFSET ?;";

        return jdbcTemplate.query(SQL, postMapper, thread.getId(), limit, offset);
    }

    public List<PostModel> getPostsTree(final ThreadModel thread, final Integer limit, final Integer offset, final Boolean desc) {
        final String SQL = "SELECT p.id, parent_id, f.slug, thread_id, nickname, is_edited, p.message, p.created " +
                "FROM posts p " +
                "JOIN forums f ON (f.id = p.forum_id) " +
                "WHERE p.thread_id = ? " +
                "ORDER BY p.path " + (desc ? "DESC" : "ASC") + " LIMIT ? OFFSET ?;";

        return jdbcTemplate.query(SQL, postMapper, thread.getId(), limit, offset);
    }

    public List<Integer> getParents(final ThreadModel thread, final Integer limit, final Integer offset, final Boolean desc) {
        final String SQL = "SELECT p.id " +
                "FROM posts p " +
                "WHERE parent_id = 0 AND p.thread_id = ? " +
                "ORDER BY p.id " + (desc ? "DESC" : "ASC") + ", id LIMIT ? OFFSET ?;";
        return jdbcTemplate.query(SQL, parentMapper, thread.getId(), limit, offset);
    }

    public List<PostModel> getPostsParentTree(final ThreadModel thread, final Boolean desc, final List<Integer> parents) {
        final String SQL = "SELECT p.id, parent_id, f.slug, thread_id, nickname, is_edited, p.message, p.created " +
                "FROM posts p " +
                "JOIN forums f ON (f.id = p.forum_id) " +
                "WHERE p.path[1] = ? AND p.thread_id = ? " +
                "ORDER BY path " + (desc ? "DESC" : "ASC") + ", p.id " + (desc ? "DESC" : "ASC") + ";";

        List<PostModel> result = new ArrayList<>();
        for (Integer parent : parents) {
            result.addAll(jdbcTemplate.query(SQL, postMapper, parent, thread.getId()));
        }
        return result;
    }

    public PostModel getById(final Integer id) {
        final String SQL = "SELECT p.id, parent_id, f.slug, thread_id, nickname, is_edited, p.message, p.created " +
                "FROM posts p " +
                "JOIN forums f ON (f.id = p.forum_id) " +
                "WHERE p.id = ?;";
        return jdbcTemplate.queryForObject(SQL, postMapper, id);
    }

    public void update(final PostModel post, final PostUpdateModel postUpdate) {
        if (post.getMessage().equals(postUpdate.getMessage())) {
            return;
        }

        final String SQL = "UPDATE posts SET message = ?, is_edited = TRUE WHERE id = ?;";
        jdbcTemplate.update(SQL, postUpdate.getMessage(), post.getId());

        post.setMessage(postUpdate.getMessage());
        post.setIsEdited(true);
    }

    public List<Integer> getChildren(final Integer thread_id) {
        final String SQL = "SELECT id FROM posts WHERE thread_id=?;";
        return jdbcTemplate.queryForList(SQL, Integer.class, thread_id);
    }

    private static final class PostMapper implements RowMapper<PostModel> {
        public PostModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            final PostModel post = new PostModel();
            post.setId(rs.getInt("id"));
            post.setParent(rs.getInt("parent_id"));
            post.setForum(rs.getString("slug"));
            post.setThread(rs.getInt("thread_id"));
            post.setAuthor(rs.getString("nickname"));
            post.setIsEdited(rs.getBoolean("is_edited"));
            post.setMessage(rs.getString("message"));

            Timestamp created = rs.getTimestamp("created");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            post.setCreated(dateFormat.format(created));

            return post;
        }
    }

    private static final class ParentMapper implements RowMapper<Integer> {
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt("id");
        }
    }
}
