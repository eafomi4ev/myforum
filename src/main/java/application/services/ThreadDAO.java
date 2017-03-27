package application.services;

import application.models.PostModel;
import application.models.ThreadModel;
import application.models.VoteModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        List<PostModel> posts = jdbcTemplate.query(sql, new Object[]{threadId}, new PostDAO.PostModelMapper());
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
        List<PostModel> posts = jdbcTemplate.query(sql.toString(), new Object[]{threadId}, new PostDAO.PostModelMapper());
        return posts;
    }

    public List<PostModel> getPostsInThread(String threadSlug) {
        String sql = "SELECT * FROM posts WHERE thread = ?";
        List<PostModel> posts = jdbcTemplate.query(sql, new Object[]{threadSlug}, new PostDAO.PostModelMapper());
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
        if (post.getParent() != 0) {
            PostDAO postDAO = new PostDAO(jdbcTemplate);
            PostModel parentPost = postDAO.getPostByID(post.getParent());
            if (post.getThread() != parentPost.getThread()) {
                throw new IllegalArgumentException("Родительский пост отсутствует в ветке");
            }
        }

        StringBuffer sql = new StringBuffer("INSERT INTO posts (parent, author, message, isedited, forum, thread, created) " +
                "VALUES(?, (SELECT nickname FROM users WHERE LOWER(nickname)=LOWER(?)), ?, ?, " +
                "(SELECT forum FROM threads WHERE id = ?), ?, ");

        if (post.getCreated() == null) {
            sql.append("DEFAULT )");
        } else {
            sql.append("'").append(post.getCreated().toString()).append("')");
        }

        jdbcTemplate.update(sql.toString(), post.getParent(), post.getAuthor(), post.getMessage(),
                post.getIsEdited(), post.getThread(), post.getThread());
        sql.setLength(0);

        sql.append("UPDATE forums SET posts = posts + 1 WHERE slug = (SELECT forum FROM threads WHERE id = ?)"); //todo: сделать keyHolder, обновлять запись в forums по id
        jdbcTemplate.update(sql.toString(), post.getThread());
    }

    public PostModel getLastAddedPost() {
        String sql = "SELECT * FROM posts WHERE id = (SELECT max(id) FROM posts)";
        return jdbcTemplate.query(sql, new PostDAO.PostModelMapper()).get(0);
    }

    //TODO: разобраться с получением веток тут и в ForumDAO
    public List<ThreadModel> getThreadBySlug(String threadSlug) {
        String sql = "SELECT * FROM threads WHERE LOWER(slug) = LOWER(?)";
        return jdbcTemplate.query(sql, new Object[]{threadSlug}, new ThreadModelMapper());
    }

    public List<ThreadModel> getThreadById(int id) { //todo: Должен возвращать 1 thread, а не List
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
        try {
            if (sort.equals("flat")) {
                return getPostsInFlatSort(threadId, limit, marker, desc);
            }
            if (sort.equals("tree")) {
                return getPostsInTreeSort(threadId, limit, marker, desc);
            }
            if (sort.equals("parent_tree")) {
                return getPostsInParentTreeSort(threadId, limit, marker, desc);
            }
            return null;
        } catch (IndexOutOfBoundsException e) {
            throw e;
        }
    }


    private List<PostModel> getPostsInFlatSort(int threadId, Integer limit, int marker, boolean desc) {
//        List<PostModel> posts;

        StringBuffer sql = new StringBuffer("SELECT * FROM posts WHERE thread = ? ORDER BY created");

        if (desc) {
            sql.append(" DESC");
        }

        sql.append(", id");

        if (desc) {
            sql.append(" DESC");
        }

        sql.append(" LIMIT ? OFFSET ?");



        try {
            ThreadModel thread = getThreadById(threadId).get(0);
        } catch (IndexOutOfBoundsException e) {
            throw e;
        }

        return jdbcTemplate.query(sql.toString(), new Object[]{threadId, limit, marker}, new PostDAO.PostModelMapper());

    }

    private List<PostModel> getPostsInTreeSort(int threadId, Integer limit, int marker, boolean desc) {
        final List<Object> arguments = new ArrayList<>();
        StringBuffer sql = new StringBuffer("WITH RECURSIVE rec (id, path) AS (SELECT id, array_append('{}'::INTEGER[], id) FROM posts " +
                "WHERE parent = 0 AND thread = ? UNION ALL SELECT p.id, array_append(path, p.id) FROM posts p JOIN " +
                "rec ON rec.id = p.parent AND p.thread = ?) SELECT p.* FROM rec JOIN " +
                "posts p ON rec.id = p.id ORDER BY rec.path");
        arguments.add(threadId);
        arguments.add(threadId);

        if (desc) {
            sql.append(" DESC");
        }

        sql.append(", id");

        if (desc) {
            sql.append(" DESC");
        }

        if (limit > 0) {
            sql.append(" LIMIT ?");
            arguments.add(limit);
        }

        if (marker > 0) {
            sql.append(" OFFSET ?");
            arguments.add(marker);
        }

        return jdbcTemplate.query(sql.toString(), arguments.toArray(), new PostDAO.PostModelMapper());
    }

    private List<PostModel> getPostsInParentTreeSort(int threadId, Integer limit, int marker, boolean desc) {

        final ArrayList<Object> parameters = new ArrayList<>();
        StringBuffer sql = new StringBuffer("WITH RECURSIVE rec (id, path) AS (SELECT id, array_append('{}'::INTEGER[], id) FROM " +
                "(SELECT DISTINCT id FROM posts WHERE thread = ? AND parent = 0 ORDER BY id ");
        parameters.add(threadId);

        if (desc) {
            sql.append(" DESC ");
        }

        if (limit > 0) {
            sql.append(" LIMIT ? ");
            parameters.add(limit);
        }

        if (marker > 0) {
            sql.append(" OFFSET ? ");
            parameters.add(marker);
        }

        sql.append(") roots " +
                "UNION ALL " +
                "SELECT p.id, array_append(path, p.id) FROM posts p " +
                "JOIN rec rp ON rp.id = p.parent) " +
                "SELECT p.* FROM rec JOIN posts p ON rec.id = p.id ORDER BY rec.path ");

        if (desc) {
            sql.append(" DESC ");
        }

        return jdbcTemplate.query(sql.toString(), parameters.toArray(), new PostDAO.PostModelMapper());
    }


    public ThreadModel detailsUpdate(int threadID, ThreadModel thread) {

        StringBuffer sql = new StringBuffer("UPDATE threads SET");
        final List<Object> arguments = new ArrayList<>();
        if (thread.getTitle() != null && !thread.getTitle().isEmpty()) {
            sql.append(" title = ?,");
            arguments.add(thread.getTitle());
        }

        if (thread.getMessage() != null && !thread.getMessage().isEmpty()) {
            sql.append(" message = ?,");
            arguments.add(thread.getMessage());
        }

        if (arguments.size() != 0) {
            sql.deleteCharAt(sql.length() - 1);
            sql.append(" WHERE id = ?");
            arguments.add(threadID);
            jdbcTemplate.update(sql.toString(), arguments.toArray());
        }
        return getThreadById(threadID).get(0);
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
