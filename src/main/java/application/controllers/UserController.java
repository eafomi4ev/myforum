package application.controllers;

import application.models.UserModel;
import application.services.UserDAO;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * Created by egor on 05.03.17.
 */

@RestController
public class UserController {

    @NotNull
    private final UserDAO service;

    private final JdbcTemplate jdbcTemplate;
    UserController(JdbcTemplate jdbcTemplate){
        this.service = new UserDAO(jdbcTemplate);
        this.jdbcTemplate = jdbcTemplate;
    }

//    UserController(){}

    @RequestMapping(path = "/user/{nickname}/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String create(@PathVariable("nickname") String nickname, @RequestBody UserModel user) {

        System.out.print(nickname);
        service.insert(user);

        return new JSONObject().put("msg", "Связь установлена").toString();
    }

}

