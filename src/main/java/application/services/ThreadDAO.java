package application.services;

import application.models.PostModel;
import application.models.ThreadModel;
import application.models.VoteModel;
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

    public List<PostModel> getPostsInThread(String threadSlug) {
        String sql = "SELECT * FROM posts WHERE thread = ?";
        List<PostModel> posts = jdbcTemplate.query(sql, new Object[]{threadSlug}, new PostModelMapper());
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

    //TODO: разобраться с получением веток тут и в ForumDAO
    public List<ThreadModel> getThreadBySlug(String threadSlug) {
        String sql = "SELECT * FROM threads WHERE LOWER(slug) = LOWER(?)";
        return jdbcTemplate.query(sql, new Object[]{threadSlug}, new ThreadModelMapper());
    }

    public List<ThreadModel> getThreadById(int id) {
        String sql = "SELECT * FROM threads WHERE id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, new ThreadModelMapper());
    }

    public void createVote(VoteModel vote, int threadId) {
        String sql = "INSERT INTO votes (nickname, voice, thread_id) VALUES ((SELECT nickname FROM users WHERE" +
                "  LOWER(users.nickname)=LOWER(?)), ?, ?) ON CONFLICT (nickname) DO UPDATE SET voice = ?;";

        jdbcTemplate.update(sql.toString(), vote.getNickname(), vote.getVoice(), threadId, vote.getVoice());

        sql = "UPDATE threads SET votes = (SELECT SUM(voice) FROM votes WHERE thread_id = ?) WHERE  id = ?";

        jdbcTemplate.update(sql, threadId, threadId);

    }

    protected static final class ThreadModelMapper implements RowMapper<ThreadModel> {
        @Override
        public ThreadModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {

            return new ThreadModel(
                    resultSet.getInt("id"),
                    resultSet.getString("title"),
                    resultSet.getString("author"),
                    resultSet.getString("forum"),
                    resultSet.getString("message"),
                    resultSet.getInt("votes"),
                    resultSet.getString("slug"),
                    resultSet.getTimestamp("created"));
        }

    }

    protected static final class PostModelMapper implements RowMapper<PostModel> {
        @Override
        public PostModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {

            return new PostModel(
                    resultSet.getInt("id"),
                    resultSet.getInt("parent"),
                    resultSet.getString("author"),
                    resultSet.getString("message"),
                    resultSet.getBoolean("isedited"),
                    resultSet.getString("forum"),
                    resultSet.getInt("thread"),
                    resultSet.getTimestamp("created"));

        }
    }

    protected static final class VoteModelMapper implements RowMapper<VoteModel> {

        @Override
        public VoteModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {

            return new VoteModel(
                    resultSet.getString("nickname"),
                    resultSet.getInt("voice"),
                    resultSet.getInt("thread_id"));
        }
    }


}
