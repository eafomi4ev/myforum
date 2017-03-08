package application.services;

import application.models.UserModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by egor on 06.03.17.
 */
@Service
public class UserDAO {

    private JdbcTemplate jdbcTemplate;

    public UserDAO(@NotNull JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public final void insert(final UserModel user) {
        String sql = "INSERT INTO users (nickname, fullname, about, email) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getNickname(), user.getFullname(), user.getAbout(), user.getEmail());

    }

    public final UserModel getByNick(final String nickName) {
        String sql = "SELECT * FROM users WHERE lower(nickname)=lower(?)";
        UserModel user = jdbcTemplate.queryForObject(sql, new Object[]{nickName}, new UserModelMapper());
        return user;
    }

    public final List<UserModel> get(UserModel user) {
        String sql = "SELECT * FROM users WHERE lower(nickname)=lower(?) OR lower(email)=lower(?)";
        return jdbcTemplate.query(sql, new Object[]{user.getNickname(), user.getEmail()}, new UserModelMapper());
    }


    //Преобразование
    private static final class UserModelMapper implements RowMapper<UserModel> {

        @Override
        public UserModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            UserModel user = new UserModel(resultSet.getString("nickname"),
                    resultSet.getString("fullname"),
                    resultSet.getString("about"),
                    resultSet.getString("email"));
            return user;

        }
    }

//    public static UserModel read(ResultSet rs, int rowNum) throws SQLException {
//        return new UserModel(
//                rs.getInt("id"),
//                rs.getString("about"),
//                rs.getString("email"),
//                rs.getString("fullname"),
//                rs.getString("nickname")
//        );
//    }
}
