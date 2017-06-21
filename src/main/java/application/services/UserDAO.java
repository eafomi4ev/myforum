package application.services;

import application.models.ForumModel;
import application.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class UserDAO {
    private static final UserModelMapper userModelMapper = new UserModelMapper();

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(final UserModel user) {
        final String SQL = "INSERT INTO users (nickname, fullname, email, about) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(SQL, user.getNickname(), user.getFullname(), user.getEmail(), user.getAbout());
    }

    public List<UserModel> get(final UserModel user) {
        String sql = "SELECT * FROM users WHERE lower(nickname)=lower(?) OR lower(email)=lower(?)";
        return jdbcTemplate.query(sql, new Object[]{user.getNickname(), user.getEmail()}, userModelMapper);
    }

    public UserModel get(final String nickName) {
        String sql = "SELECT * FROM users WHERE lower(nickname)=lower(?)";
        UserModel user = jdbcTemplate.queryForObject(sql, new Object[]{nickName}, userModelMapper);
        return user;
    }

    public void updateUserProfile(final UserModel user) {
        final StringBuilder sql = new StringBuilder("UPDATE users SET");
        final List<Object> userProperties = new ArrayList<>();

        if (user.getFullname() != null && !user.getFullname().isEmpty()) {
            sql.append(" fullname = ?,");
            userProperties.add(user.getFullname());
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            sql.append(" email = ?,");
            userProperties.add(user.getEmail());
        }

        if (user.getAbout() != null && !user.getAbout().isEmpty()) {
            sql.append(" about = ?,");
            userProperties.add(user.getAbout());
        }

        if (userProperties.isEmpty()) {
            return;
        }

        sql.deleteCharAt(sql.length() - 1);
        sql.append(" WHERE LOWER(nickname) = LOWER(?);");
        userProperties.add(user.getNickname());
        jdbcTemplate.update(sql.toString(), userProperties.toArray());
    }

    public List<UserModel> getUsersInForum(final ForumModel forum, final Integer limit, final String since, final Boolean desc) {
        final StringBuilder sql = new StringBuilder(
                "SELECT u.id, nickname, fullname, email, about " +
                "FROM users u " +
                "WHERE u.id IN (" +
                "SELECT user_id " +
                "FROM forum_users " +
                "WHERE forum_id = ?) ");

        final List<Object> props = new ArrayList<>();
        props.add(forum.getId());

        if (since != null) {
            if (desc != null && desc) {
                sql.append("AND LOWER(nickname) < LOWER(?) ");
            } else {
                sql.append("AND LOWER(nickname) > LOWER(?) ");
            }
            props.add(since);
        }

        sql.append("ORDER BY nickname ");
        if (desc != null) {
            sql.append((desc ? "DESC " : "ASC "));
        }
        if (limit != null) {
            sql.append("LIMIT ? ");
            props.add(limit);
        }

        sql.append(";");

        return jdbcTemplate.query(sql.toString(), props.toArray(), userModelMapper);
    }

    private static final class UserModelMapper implements RowMapper<UserModel> {
        public UserModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            final UserModel user = new UserModel();
            user.setId(rs.getInt("id"));
            user.setNickname(rs.getString("nickname"));
            user.setFullname(rs.getString("fullname"));
            user.setEmail(rs.getString("email"));
            user.setAbout(rs.getString("about"));

            return user;
        }
    }
}
