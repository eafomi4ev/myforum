package application.controllers;

import application.models.UserModel;
import application.services.UserDAO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * Created by egor on 05.03.17.
 */

@RestController
@RequestMapping(path = "/user/{nickname}")
public class UserController {

    private final UserDAO userServiceDAO;

//    private final JdbcTemplate jdbcTemplate;

    UserController(JdbcTemplate jdbcTemplate) {
        this.userServiceDAO = new UserDAO();
//        this.jdbcTemplate = jdbcTemplate;
    }

//    UserController(){}

    @RequestMapping(path = "/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserModel> create(@PathVariable("nickname") String nickName, @RequestBody UserModel user) {

        try {
            userServiceDAO.insert(user);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(userServiceDAO.getByNick(nickName));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(userServiceDAO.getByNick(nickName));

    }

    @RequestMapping(path = "/profile", method = RequestMethod.GET, produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserModel> profile(@PathVariable("nickname") String nickName) {
        UserModel user = null;
        try {
            user = userServiceDAO.getByNick(nickName);

        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(userServiceDAO.getByNick(nickName));
        }


        return ResponseEntity.status(HttpStatus.CREATED).body(userServiceDAO.getByNick(nickName));

    }


}



























