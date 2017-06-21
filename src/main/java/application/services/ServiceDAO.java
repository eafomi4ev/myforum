package application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ServiceDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ServiceDAO(JdbcTemplate template){
        this.jdbcTemplate = template;
    }

    public int getCountUsers() {
        final String sql = "SELECT count(*) FROM users;";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int getCountForums() {
        final String sql = "SELECT count(*) FROM forums;";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int getCountThreads() {
        final String sql = "SELECT count(*) FROM threads;";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int getCountPost() {
        final String sql = "SELECT count(*) FROM posts;";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public void clear() {
        final String sql = "TRUNCATE TABLE users, forums, threads, votes, posts, forum_users CASCADE;";
        jdbcTemplate.execute(sql);
    }
}
