package application.services;

import application.models.UserModel;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by egor on 06.03.17.
 */
public class UserDAO {

    private JdbcTemplate jdbcTemplate;

    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public final void insert(final UserModel user) {
        String query = "INSERT INTO \"User\" (nickname, fullname, about, email) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(query, user.getNickname(), user.getFullname(), user.getAbout(), user.getEmail());
    }
}
