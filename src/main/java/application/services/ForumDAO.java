package application.services;

import application.models.ForumModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by egor on 06.03.17.
 */
public final class ForumDAO {

    private JdbcTemplate jdbcTemplate;

    public ForumDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public final void create(ForumModel forum) {
        String sql = "INSERT INTO forums (title, user_nick, slug, posts, threads) VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, forum.getTitle(), forum.getUser(), forum.getSlug(), forum.getPosts(), forum.getThreads());
    }

    public final ForumModel getbySlug(String slug) {
        String sql = "SELECT * FROM forums WHERE LOWER(slug) = LOWER(?)";
        List<ForumModel> forums = jdbcTemplate.query(sql, new Object[]{slug}, new ForumDAO.ForumModelMapper());
        return forums.get(0);

//        return jdbcTemplate.query("SELECT * FROM forums WHERE LOWER(slug) = LOWER(?)", new Object[]{slug}, new ForumDAO.ForumModelMapper());

    }



    //Преобразование
    private static final class ForumModelMapper implements RowMapper<ForumModel> {

        @Override
        public ForumModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            ForumModel forum = new ForumModel(resultSet.getString("title"),
                    resultSet.getString("user_nick"),
                    resultSet.getString("slug"),
                    resultSet.getInt("posts"),
                    resultSet.getInt("threads"));
            return forum;

        }
    }


}
