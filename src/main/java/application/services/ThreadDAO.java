package application.services;

import application.models.PostModel;
import application.models.ThreadModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by egor on 15.03.17.
 */
@Service
public final class ThreadDAO {

    private JdbcTemplate jdbcTemplate;

    public ThreadDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PostModel> getPostsInThread(int threadId) {
        String sql = "SELECT * FROM posts WHERE thread = ?";
        List<PostModel> posts = jdbcTemplate.query(sql, new Object[]{threadId}, new PostModelMapper());
        return posts;
    }

    public void createPost(List<PostModel> posts) {
        StringBuffer sql = new StringBuffer("INSERT INTO posts (parent, author, message, isedited, forum, thread, created) " +
                "VALUES(?, (SELECT nickname FROM users WHERE LOWER(nickname)=LOWER(?)), ?, ?, " +
                "(SELECT forum FROM threads WHERE id = ?), ?, ").append(
                        posts.get(0).getCreated() == null ? "DEFAULT" : posts.get(0).getCreated().toString()).append(")");


        System.out.println(sql.toString());

        jdbcTemplate.update(sql.toString(), posts.get(0).getParent(), posts.get(0).getAuthor(), posts.get(0).getMessage(),
                posts.get(0).getIsEdited(), posts.get(0).getThread(), posts.get(0).getThread());

    }

    protected static final class ThreadModelMapper implements RowMapper<ThreadModel> {
        @Override
        public ThreadModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {

            ThreadModel thread = new ThreadModel(
                    resultSet.getInt("id"),
                    resultSet.getString("title"),
                    resultSet.getString("author"),
                    resultSet.getString("forum"),
                    resultSet.getString("message"),
                    resultSet.getInt("votes"),
                    resultSet.getString("slug"),
                    resultSet.getTimestamp("created"));

            return thread;

        }
    }

    protected static final class PostModelMapper implements RowMapper<PostModel> {
        @Override
        public PostModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {

            PostModel post = new PostModel(
                    resultSet.getInt("id"),
                    resultSet.getInt("parent"),
                    resultSet.getString("author"),
                    resultSet.getString("message"),
                    resultSet.getBoolean("isedited"),
                    resultSet.getString("forum"),
                    resultSet.getInt("thread"),
                    resultSet.getTimestamp("created"));

            return post;

        }
    }


}
