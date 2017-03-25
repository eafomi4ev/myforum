package application.services;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by egor on 18.03.17.
 */

@Service
public class VotesDAO {

    private JdbcTemplate jdbcTemplate;

    public VotesDAO(@NotNull JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }




}
