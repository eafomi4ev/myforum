package application.services;


import application.Instances;
import application.models.ForumModel;
import application.models.ThreadModel;
import application.models.ThreadUpdateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Repository
@Transactional
public class ThreadDAO {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ThreadDAO(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(ThreadModel thread) {
        String sql;
        Object[] object;
        if (thread.getCreated() == null) {
            sql = "INSERT INTO threads (title, user_id, forum_id, message, slug) VALUES (?, ?, ?, ?, ?) RETURNING id;";
            object = new Object[]{thread.getTitle(), thread.getUserId(), thread.getForumId(), thread.getMessage(), thread.getSlug()};
        } else {
            Timestamp timestamp = Instances.getTimestampFromString(thread.getCreated());

            sql = "INSERT INTO threads (title, user_id, forum_id, message, slug, created) VALUES (?, ?, ?, ?, ?, ?) RETURNING id;";
            object = new Object[]{thread.getTitle(), thread.getUserId(), thread.getForumId(), thread.getMessage(), thread.getSlug(), timestamp};
        }
        thread.setId(jdbcTemplate.queryForObject(sql, object, Integer.class));

        jdbcTemplate.update("UPDATE forums SET threads = threads + 1 WHERE id = ?;", thread.getForumId());
    }

    public ThreadModel getByIdWithForumData(Integer id) {
        String sql = "SELECT t.id AS t_id, f.id AS f_id, f.slug AS f_slug FROM threads t JOIN forums f ON f.id=t.forum_id WHERE t.id=?;";
        return jdbcTemplate.queryForObject(sql, new ThreadForumMapper(), id);
    }

    public ThreadModel getByIdWithFullData(Integer id) {
        String sql = "SELECT t.id AS t_id, t.title AS t_title, nickname, t.message AS msg, " +
                "t.slug AS t_slug, f.slug AS f_slug, created, votes " +
                "FROM threads t " +
                "JOIN forums f ON f.id=t.forum_id " +
                "JOIN users u ON u.id=t.user_id "+
                "WHERE t.id=?;";
        return jdbcTemplate.queryForObject(sql, new ThreadForumUserMapper(), id);
    }

    public ThreadModel getBySlugWithForumData(String slug) {
        String sql = "SELECT t.id AS t_id, f.id AS f_id, f.slug AS f_slug FROM threads t JOIN forums f ON f.id=t.forum_id WHERE LOWER(t.slug) = LOWER(?);";
        return jdbcTemplate.queryForObject(sql, new ThreadForumMapper(), slug);
    }

    public ThreadModel getBySlugWithFullData(String slug) {
        String sql = "SELECT t.id AS t_id, t.title AS t_title, nickname, t.message AS msg, " +
                "t.slug AS t_slug, f.slug AS f_slug, created, votes " +
                "FROM threads t " +
                "JOIN forums f ON f.id=t.forum_id " +
                "JOIN users u ON u.id=t.user_id " +
                "WHERE LOWER(t.slug) = LOWER(?);";
        return jdbcTemplate.queryForObject(sql, new ThreadForumUserMapper(), slug);
    }

    public List<ThreadModel> getByForumId(ForumModel forum, Integer limit, String since, Boolean desc) {
        StringBuilder sql = new StringBuilder("SELECT t.id AS t_id, " +
                "t.title AS t_title, nickname, t.message AS msg, " +
                "t.slug AS t_slug, f.slug AS f_slug, created, votes " +
                "FROM threads t " +
                "JOIN forums f ON f.id=t.forum_id " +
                "JOIN users u ON u.id=t.user_id " +
                "WHERE f.id = ? ");

        List<Object> props = new ArrayList<>();
        props.add(forum.getId());


        if (since != null) {
            sql.append("AND created ");

            if (desc != null && desc) {
                sql.append("<= ? ");
            } else {
                sql.append(">= ? ");
            }

            Timestamp timestamp = Instances.getTimestampFromString(since);
            props.add(timestamp);
        }

        sql.append("ORDER BY created ");

        if (desc != null && desc) {
            sql.append("DESC ");
        }

        sql.append("LIMIT ?;");
        props.add(limit);


        return jdbcTemplate.query(sql.toString(), new ThreadForumUserMapper(), props.toArray());
    }

    public void update(ThreadModel thread, ThreadUpdateModel threadUpdate) {
        StringBuilder sql = new StringBuilder("UPDATE threads SET");
        List<Object> props = new ArrayList<>();

        if (threadUpdate.getTitle() != null) {
            sql.append(" title = ?,");
            props.add(threadUpdate.getTitle());

            thread.setTitle(threadUpdate.getTitle());
        }
        if (threadUpdate.getMessage() != null) {
            sql.append(" message = ?,");
            props.add(threadUpdate.getMessage());

            thread.setMessage(threadUpdate.getMessage());
        }
        if (props.isEmpty()) {
            return;
        }

        sql.deleteCharAt(sql.length() - 1);

        sql.append(" WHERE id = ?;");
        props.add(thread.getId());
        jdbcTemplate.update(sql.toString(), props.toArray());
    }

    private static class ThreadForumMapper implements RowMapper<ThreadModel> {
        public ThreadModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            ThreadModel thread = new ThreadModel();
            thread.setId(rs.getInt("t_id"));
            thread.setForumId(rs.getInt("f_id"));
            thread.setForum(rs.getString("f_slug"));

            return thread;
        }
    }

    private static class ThreadForumUserMapper implements RowMapper<ThreadModel> {
        public ThreadModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            ThreadModel thread = new ThreadModel();
            thread.setId(rs.getInt("t_id"));
            thread.setTitle(rs.getString("t_title"));
            thread.setAuthor(rs.getString("nickname"));
            thread.setMessage(rs.getString("msg"));
            thread.setSlug(rs.getString("t_slug"));
            thread.setForum(rs.getString("f_slug"));

            Timestamp created = rs.getTimestamp("created");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            thread.setCreated(dateFormat.format(created));

            thread.setVotes(rs.getInt("votes"));

            return thread;
        }
    }
}
