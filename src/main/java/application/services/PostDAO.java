package application.services;

import application.models.PostModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by egor on 19.03.17.
 */

@Service
public final class PostDAO {
    private JdbcTemplate jdbcTemplate;

    public PostDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PostModel getPostByID(int id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        List<PostModel> posts = jdbcTemplate.query(sql, new Object[]{id}, new PostDAO.PostModelMapper());
        return posts.get(0);
    }

    public int detailsUpdate(int postID, PostModel post) {
        PostModel oldPost = getPostByID(postID);
        if (post.getMessage().equals(oldPost.getMessage())) {
            return 1;
        }

        String sql = "UPDATE posts SET message = ?, isedited = TRUE WHERE id = ?";
        return jdbcTemplate.update(sql, post.getMessage(), postID);
    }

    public static final class PostModelMapper implements RowMapper<PostModel> {
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
}
