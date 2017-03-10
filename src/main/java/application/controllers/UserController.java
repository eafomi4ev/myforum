package application.controllers;

import application.models.UserModel;
import application.services.UserDAO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
            return ResponseEntity.status(HttpStatus.CONFLICT).body(userServiceDAO.get(user.getNickname(), user.getEmail()));//409
        }


    }

    /*Параметр consumes определяет тип содержимого тела запроса. например, consumes="application/json" определяет, что
    Content-Type запроса, который отправил клиент должен быть "application/json". Можно задать отрицательное указание:
    consumes="!application/json". Тогда будет требоваться любой Content-Type, кроме указанного. допускается указание
    нескольких значений: ("text/plain", "application/*).

    Параметр produces определяет формат возвращаемого методом значения. Если на клиенте в header'ах не указан заголовок
    Accept, то не имеет значение, что установлено в produces. Если же заголовок Accept установлен, то значение produces
    должно совпадать с ним для успешного возвращения результата клиенту. Параметр produces может также содержать
    перечисление значений.
    */
    @RequestMapping(path = "/profile", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity profile(@PathVariable("nickName") String nickName) {
        try {
            return ResponseEntity.ok(userServiceDAO.get(nickName));//200
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь отсутствует в системе.");//404
        }

    }

    @RequestMapping(path = "/profile", method = RequestMethod.POST, produces = "application/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity profile(@PathVariable("nickName") String nickName, @RequestBody UserModel userDataForUpfdate) {
        System.out.print(nickName);

        try {
            UserModel user = userServiceDAO.updateUserProfile(nickName, userDataForUpfdate);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь отсутствует в системе.");//404
            } else {
                return ResponseEntity.ok(user);//200
            }
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMsg("Новые данные профиля пользователя конфликтуют с имеющимися пользователями."));
        }

    }

    private final class ResponseMsg {
        @JsonProperty
        private String msg;

        @JsonCreator
        ResponseMsg(@JsonProperty String msg) {
            this.msg = msg;
        }
    }

}



























