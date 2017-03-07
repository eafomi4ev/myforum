package application.services;

import application.models.UserModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by egor on 06.03.17.
 */
public class UserDAO {

    @Autowired
    @NotNull
    private JdbcTemplate jdbcTemplate;

//    @Autowired
//    public UserDAO(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }

    public final void insert(final UserModel user) {
        String sql = "INSERT INTO users (nickname, fullname, about, email) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getNickname(), user.getFullname(), user.getAbout(), user.getEmail());

    }

    public final UserModel getByNick(final String nickName) {
        String sql = "SELECT * FROM users WHERE nickname=?";
        UserModel user = jdbcTemplate.queryForObject(sql, new Object[]{nickName}, new UserModelMapper());
        return user;
    }


    //Преобразование
    private static final class UserModelMapper implements RowMapper<UserModel> {

        @Override
        public UserModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            UserModel user = new UserModel(resultSet.getInt("id"),
                    resultSet.getString("nickname"),
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
