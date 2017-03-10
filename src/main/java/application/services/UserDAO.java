package application.services;

import application.models.UserModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public final UserModel get(final String nickName) {
        String sql = "SELECT * FROM users WHERE lower(nickname)=lower(?)";
        UserModel user = jdbcTemplate.queryForObject(sql, new Object[]{nickName}, new UserModelMapper());
        return user;
    }

    public final List<UserModel> get(final String nickName, final String email) {
        String sql = "SELECT * FROM users WHERE lower(nickname)=lower(?) OR lower(email)=lower(?)";
        return jdbcTemplate.query(sql, new Object[]{nickName, email}, new UserModelMapper());
    }

//    public final List<UserModel> get(UserModel user) {
//        String sql = "SELECT * FROM users WHERE lower(nickname)=lower(?) OR lower(email)=lower(?)";
//        return jdbcTemplate.query(sql, new Object[]{user.getNickname(), user.getEmail()}, new UserModelMapper());
//    }

    public final UserModel updateUserProfile(final String nickName, final UserModel user) {

        StringBuffer values = new StringBuffer("VALUES (");
        StringBuffer sql = new StringBuffer("UPDATE users SET ");
        boolean isFirstArgumentAdded = false;
//        List<String> addedArgument = new ArrayList<>();
        UserModel newUser = get(nickName);
        /*Этот if не перемещать, другие if перед ним не ставить, тк в первом if гарантировано isFirstArgumentAdded = false*/
        if (!StringUtils.isEmpty(user.getNickname())) {
            sql.append("nickname = '" + user.getNickname() + "',");
            newUser.setNickname(user.getNickname());
            isFirstArgumentAdded = true;
        }
        if (!StringUtils.isEmpty(user.getFullname())) {
            sql.append("fullname = '" + user.getFullname() + "',");
            newUser.setFullname(user.getFullname());
            isFirstArgumentAdded = true;
        }

        if (!StringUtils.isEmpty(user.getAbout())) {
            sql.append("about = '" + user.getAbout() + "',");
            newUser.setAbout(user.getAbout());
            isFirstArgumentAdded = true;

        }
        if (!StringUtils.isEmpty(user.getEmail())) {
            sql.append("email = '" + user.getEmail() + "',");
            newUser.setEmail(user.getEmail());
            isFirstArgumentAdded = true;

        }

        sql.deleteCharAt(sql.length() - 1);

        sql.append(" WHERE nickname = '").append(nickName).append("'");

        int count = jdbcTemplate.update(sql.toString());

        if (count == 0) {
            return null;
        }

//        UserModel newUser = get(user.getNickname());
        return newUser;
//        for (String argument: addedArgument) {
//            if argument.equals("")
//        }


//        String sql = "UPDATE users SET nickname = ?, fullname = ?, about = ?, email = ?"
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
