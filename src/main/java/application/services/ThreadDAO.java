package application.services;

import application.models.PostModel;
import application.models.ThreadModel;
import application.models.VoteModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    public List<PostModel> getPostsInThreadWithParent(int threadId, List<Integer> parentsID) {
        System.out.println();
        StringBuffer parentsIDString = new StringBuffer("(");
        for (Integer id : parentsID) {
            parentsIDString.append(id.toString()).append(',');
        }
        parentsIDString.setCharAt(parentsIDString.length() - 1, ')');
//        parentsIDString.deleteCharAt(parentsIDString.length() - 1);
        System.out.println(parentsIDString.toString());
        StringBuffer sql = new StringBuffer("SELECT * FROM posts WHERE thread = ? AND parent IN ").append(parentsIDString.toString());
        List<PostModel> posts = jdbcTemplate.query(sql.toString(), new Object[]{threadId}, new PostModelMapper());
        return posts;
    }

    public List<PostModel> getPostsInThread(String threadSlug) {
        String sql = "SELECT * FROM posts WHERE thread = ?";
        List<PostModel> posts = jdbcTemplate.query(sql, new Object[]{threadSlug}, new PostModelMapper());
        return posts;
    }

    public void createPost(PostModel post) { //todo Сделать keyHolder и возвращать id добавленного поста

//        PreparedStatementCreator preparedStatement = new PreparedStatementCreator() {
//            @Override
//            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//
//                StringBuffer sql = new StringBuffer("INSERT INTO posts (parent, author, message, isedited, forum, thread, created) ")
//                        .append(
//                                "VALUES(" +
//                                        "?, " +
//                                        "(SELECT nickname FROM users WHERE LOWER(nickname)=LOWER(?)), " +
//                                        "?, " +
//                                        "?, " +
//                                        "(SELECT forum FROM threads WHERE id = ?), " +
//                                        "? ,")
//                        .append(post.getCreated() == null ? "DEFAULT" : post.getCreated())
//                        .append(")");
//
//
//                PreparedStatement ps = con.prepareStatement(sql.toString());
//
//                ps.setInt(1, post.getParent());
//                ps.setString(2, post.getAuthor());
//                ps.setString(3, post.getMessage());
//                ps.setBoolean(4, post.getIsEdited());
//                ps.setInt(5, post.getThread());
//                ps.setInt(6, post.getThread());
////                ps.setTimestamp(7, post.getCreated() == null ? DEFAULT : post.getCreated());
//                return ps;
//            }
//        };
//
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//
//        jdbcTemplate.update(preparedStatement, keyHolder);
//
//        return keyHolder.getKey().intValue();
//
        StringBuffer sql = new StringBuffer("INSERT INTO posts (parent, author, message, isedited, forum, thread, created) " +
                "VALUES(?, (SELECT nickname FROM users WHERE LOWER(nickname)=LOWER(?)), ?, ?, " +
                "(SELECT forum FROM threads WHERE id = ?), ?, ").append(
                post.getCreated() == null ? "DEFAULT" : post.getCreated().toString()).append(")");

        jdbcTemplate.update(sql.toString(), post.getParent(), post.getAuthor(), post.getMessage(),
                post.getIsEdited(), post.getThread(), post.getThread());
    }

    public PostModel getLastAddedPost() {
        String sql = "SELECT * FROM posts WHERE id = (SELECT max(id) FROM posts)";
        return jdbcTemplate.query(sql, new PostModelMapper()).get(0);
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

    public List<PostModel> getPostsInThread(int threadId, Integer limit, int marker, String sort, boolean desc) {
        if (sort.equals("flat")) {
            return getPostsInFlatSort(threadId, limit, marker, desc);
        }
        return null;
    }


    private List<PostModel> getPostsInFlatSort(int threadId, Integer limit, int marker, boolean desc) {
//        List<PostModel> posts;

        StringBuffer sql = new StringBuffer("SELECT * FROM posts WHERE thread = ? ORDER BY created");

        if (desc) {
            sql.append(" DESC");
        }

        sql.append(" LIMIT ? OFFSET ?");

        return jdbcTemplate.query(sql.toString(), new Object[]{threadId, limit, marker}, new PostModelMapper());

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
