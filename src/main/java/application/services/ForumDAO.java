package application.services;


import application.models.ForumModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@Transactional
public class ForumDAO {
    private static final ForumMapper forumMapper = new ForumMapper();
    private static final ForumUserMapper forumUserMapper = new ForumUserMapper();

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ForumDAO(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(final ForumModel forum) {
        String sql = "INSERT INTO forums (title, user_id, slug) VALUES(?, ?, ?);";
        jdbcTemplate.update(sql, forum.getTitle(), forum.getUserId(), forum.getSlug());
    }

    public ForumModel getBySlug(String slug) {
        final String sql = "SELECT * FROM forums WHERE LOWER(slug) = LOWER(?);";
        return jdbcTemplate.queryForObject(sql, forumMapper, slug);
    }

    public ForumModel getBySlugWithAuthor(final String slug) {
        String sql = "SELECT * FROM forums f JOIN users u ON u.id=f.user_id WHERE LOWER(slug) = LOWER(?);";
        return jdbcTemplate.queryForObject(sql, forumUserMapper, slug);
    }

    private static final class ForumMapper implements RowMapper<ForumModel> {
        public ForumModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            ForumModel forum = new ForumModel();
            forum.setId(rs.getInt("id"));
            forum.setTitle(rs.getString("title"));
            forum.setSlug(rs.getString("slug"));
            forum.setUserId(rs.getInt("user_id"));

            return forum;
        }
    }

    private static final class ForumUserMapper implements RowMapper<ForumModel> {
        public ForumModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            ForumModel forum = new ForumModel();
            forum.setId(rs.getInt("id"));
            forum.setTitle(rs.getString("title"));
            forum.setUser(rs.getString("nickname"));
            forum.setSlug(rs.getString("slug"));
            forum.setUserId(rs.getInt("user_id"));
            forum.setPosts(rs.getInt("posts"));
            forum.setThreads(rs.getInt("threads"));

            return forum;
        }
    }
}
