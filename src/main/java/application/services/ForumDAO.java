package application.services;

import application.models.ForumModel;
import application.models.ThreadModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by egor on 06.03.17.
 */
@Service
public final class ForumDAO {

    private JdbcTemplate jdbcTemplate;

    public ForumDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(ForumModel forum) {

        final String sql = "INSERT INTO forums (title, user_nick, slug, posts, threads) VALUES(?, (SELECT nickname FROM users WHERE LOWER(nickname)=LOWER(?)), ?, ?, ?)";

        jdbcTemplate.update(sql, forum.getTitle(), forum.getUser(), forum.getSlug(), forum.getPosts(), forum.getThreads());
    }

    public ForumModel getbySlug(String slug) {
        final String sql = "SELECT * FROM forums WHERE LOWER(slug) = LOWER(?)";
        List<ForumModel> forums = jdbcTemplate.query(sql, new Object[]{slug}, new ForumDAO.ForumModelMapper());


        return forums.get(0);
    }


    public void createThread(ThreadModel thread) {
        String sql = "INSERT INTO threads (title, author, forum, message, votes, slug, created) " +
                "VALUES(?, (SELECT nickname FROM users WHERE LOWER(users.nickname)=LOWER(?)), " +
                "(SELECT slug FROM forums WHERE LOWER(forums.slug)=LOWER(?)), ?, ? , ? , ?)";

        jdbcTemplate.update(sql, thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage(), thread.getVotes(), thread.getSlug(), thread.getCreated());
    }

    public List<ThreadModel> getThreads(String title, String nickname) {
        String sql = "SELECT * FROM threads WHERE LOWER(title) = LOWER(?) AND LOWER(author) = LOWER(?)";
        return jdbcTemplate.query(sql, new Object[]{title, nickname}, new ThreadDAO.ThreadModelMapper());
    }

    public List<ThreadModel> getThreads(String forum) {
        String sql = "SELECT * FROM threads WHERE LOWER(forum) = LOWER(?) ";
        return jdbcTemplate.query(sql, new Object[]{forum}, new ThreadDAO.ThreadModelMapper());
    }

    public List<ThreadModel> getThreads(String slug, String limit, Timestamp since, boolean desc) {
        StringBuffer sql = new StringBuffer("SELECT * FROM threads WHERE lower(forum) = lower(?)");
        if (!StringUtils.isEmpty(since)) {
            if (desc) {
                sql.append(" AND created <= '").append(since).append("'::TIMESTAMP").append(" ORDER BY created").append(" DESC");
            } else {
                sql.append(" AND created >= '").append(since).append("'::TIMESTAMP").append(" ORDER BY created");
            }
        } else {
            if (desc) {
                sql.append(" ORDER BY created").append(" DESC");
            } else {
                sql.append(" ORDER BY created");
            }
        }

        if (!StringUtils.isEmpty(limit)) {
            sql.append(" LIMIT ").append(limit);
        }

        if (sql.charAt(sql.length() - 1) == ',') {
            sql.deleteCharAt(sql.length() - 1);
        }

        List<ThreadModel> list = jdbcTemplate.query(sql.toString(), new Object[]{slug}, new ThreadDAO.ThreadModelMapper());
        return list;
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
