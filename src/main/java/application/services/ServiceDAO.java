package application.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by egor on 21.03.17.
 */
@Service
public class ServiceDAO {

    private JdbcTemplate jdbcTemplate;

    public ServiceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Object getCountUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Object getCountForums() {
        String sql = "SELECT COUNT(*) FROM forums";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Object getCountThreads() {
        String sql = "SELECT COUNT(*) FROM threads";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Object getCountPosts() {
        String sql = "SELECT COUNT(*) FROM posts";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public void clearDataBase() {
        jdbcTemplate.update("DELETE FROM posts");
        jdbcTemplate.update("DELETE FROM votes");
        jdbcTemplate.update("DELETE FROM threads");
        jdbcTemplate.update("DELETE FROM forums");
        jdbcTemplate.update("DELETE FROM users");
    }
}
