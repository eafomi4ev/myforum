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
@RequestMapping(path = "/api/user/{nickName}")
public class UserController {

    private final UserDAO userServiceDAO;


    UserController(JdbcTemplate jdbcTemplate) {
        this.userServiceDAO = new UserDAO(jdbcTemplate);
    }

//    UserController(){}

    @RequestMapping(path = "/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity create(@PathVariable("nickName") String nickName, @RequestBody UserModel user) {

        try {
            user.setNickname(nickName);
            userServiceDAO.insert(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);//201
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(userServiceDAO.get(user));//409
        }


    }

//    @RequestMapping(path = "/profile", method = RequestMethod.GET, produces = "application/json", consumes = "application/json")
//    public ResponseEntity profile(@PathVariable("nickname") String nickName) {
//        List<UserModel> users = null;
//        try {
//            users = userServiceDAO.get(users);
////            users.setNickname(nickName);
//        } catch (DuplicateKeyException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь отсутствует в системе.");//404
//        }
//
//        return ResponseEntity.ok().body(userServiceDAO.get(users));//200
//
//    }


}



























