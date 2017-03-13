package application.services;

import application.models.ForumModel;
import application.models.ThreadModel;
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
    private UserDAO userServiceDAO;

    public ForumDAO(JdbcTemplate jdbcTemplate, UserDAO userServiceDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.userServiceDAO = userServiceDAO;
    }

    public final void create(ForumModel forum) {

        String sql = "INSERT INTO forums (title, user_nick, slug, posts, threads) VALUES(?, (SELECT nickname FROM users WHERE LOWER(nickname)=LOWER(?)), ?, ?, ?)";

        jdbcTemplate.update(sql, forum.getTitle(), forum.getUser(), forum.getSlug(), forum.getPosts(), forum.getThreads());
    }

    public final ForumModel getbySlug(String slug) {
        String sql = "SELECT * FROM forums WHERE LOWER(slug) = LOWER(?)";
        List<ForumModel> forums = jdbcTemplate.query(sql, new Object[]{slug}, new ForumDAO.ForumModelMapper());


        return forums.get(0);
//        return jdbcTemplate.query("SELECT * FROM forums WHERE LOWER(slug) = LOWER(?)", new Object[]{slug}, new ForumDAO.ForumModelMapper());
    }


    public final void createThread(ThreadModel thread) {
        String sql = "INSERT INTO thread (title, author, forum, message, votes, slug, created) " +
                "VALUES(?, (SELECT nickname FROM users WHERE LOWER(users.nickname)=LOWER(?)), " +
                "(SELECT slug FROM forums WHERE LOWER(forums.slug)=LOWER(?)), ?, ? , ? , ?)";

//                "INSERT INTO thread (title, author, forum, message, votes, slug, created) " +
//                "VALUES(?, (SELECT nickname, slug FROM users JOIN forums on users.nickname = forums.user_nick " +
//                "WHERE LOWER(nickname)=LOWER(?) AND  LOWER(slug)=LOWER(?)), ?, DEFAULT , DEFAULT , ?)";

//        String sql = "INSERT INTO thread (title, author, forum, message, votes, slug, created) " +
//                "VALUES(?, ?, ?, ?, ?, ?, ?)";
//        Timestamp createdTimeInISO = Timestamp.valueOf(LocalDateTime.parse(thread.getCreated(), DateTimeFormatter.ISO_DATE_TIME));
        jdbcTemplate.update(sql, thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage(), thread.getVotes(), thread.getSlug(), thread.getCreated());
    }

    public final List<ThreadModel> getThreadBySlug(String slug) {
        String sql = "SELECT * FROM thread WHERE LOWER(slug) = LOWER(?)";
        return jdbcTemplate.query(sql, new Object[]{slug}, new ForumDAO.ThreadModelMapper());


//        ObjectMapper objectMapper = new ObjectMapper();
//        //Set pretty printing of json
//        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
//
//        //Disable the timestamp serialization
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        String json = null;
//        try {
//            json = objectMapper.writeValueAsString(thread);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        System.out.println("2. Convert Person to JSON - Date without timestamp");
//        System.out.println(json);

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

    private static final class ThreadModelMapper implements RowMapper<ThreadModel> {
        @Override
        public ThreadModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {

//            final Timestamp createdTimeInISO = resultSet.getTimestamp("created");
//            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            ThreadModel thread = new ThreadModel(resultSet.getInt("id"),
                    resultSet.getString("title"),
                    resultSet.getString("author"),
                    resultSet.getString("forum"),
                    resultSet.getString("message"),
                    resultSet.getInt("votes"),
                    resultSet.getString("slug"),
                    resultSet.getTimestamp("created")
//                    dateFormat.format(createdTimeInISO)
            );

            return thread;

        }
    }


}
