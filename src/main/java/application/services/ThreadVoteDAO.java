package application.services;

import application.models.ThreadModel;
import application.models.ThreadVoteModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@Transactional
public class ThreadVoteDAO {
    private static final ThreadVoteMapper getThreadVoteMapper = new ThreadVoteMapper();

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ThreadVoteDAO(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public ThreadVoteModel get(ThreadModel thread, ThreadVoteModel vote){
        String sql = "SELECT votes.id AS v_id " +
                "FROM votes " +
                "JOIN users on users.id=votes.user_id " +
                "WHERE LOWER(nickname) = LOWER(?) AND votes.thread_id = ?;";
        return jdbcTemplate.queryForObject(sql, getThreadVoteMapper, vote.getNickname(), thread.getId());
    }

    public void create(final ThreadModel thread, ThreadVoteModel vote) {
        String sql = "INSERT INTO votes (user_id, thread_id, voice) " +
                "VALUES ((SELECT id FROM users WHERE LOWER(nickname) = LOWER(?)), ?, ?);";
        jdbcTemplate.update(sql, vote.getNickname(), thread.getId(), vote.getVoice());

        sql = "UPDATE threads SET votes = (SELECT SUM(voice) FROM votes " +
                "WHERE (thread_id) = ?) WHERE id = ? RETURNING votes;";
        int votes =  jdbcTemplate.queryForObject(sql, Integer.class, thread.getId(), thread.getId());
        thread.setVotes(votes);
    }

    public void insert(final ThreadModel thread, final ThreadVoteModel vote) {
        String sql = "UPDATE votes SET voice=? WHERE id=?;";
        jdbcTemplate.update(sql, vote.getVoice(), vote.getId());

        sql = "UPDATE threads SET votes = (SELECT SUM(voice) FROM votes " +
                "WHERE (thread_id) = ?) WHERE id = ? RETURNING votes;";
        int votes =  jdbcTemplate.queryForObject(sql, Integer.class, thread.getId(), thread.getId());
        thread.setVotes(votes);
    }

    private static final class ThreadVoteMapper implements RowMapper<ThreadVoteModel> {
        public ThreadVoteModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            final ThreadVoteModel threadVote = new ThreadVoteModel();
            threadVote.setId(rs.getInt("v_id"));

            return threadVote;
        }
    }
}

