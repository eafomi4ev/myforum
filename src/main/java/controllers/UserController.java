package controllers;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import services.UserDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import services.AccountService;
//import start.UserProfile;

/**
 * Created by egor on 23.02.17.
 */

@RestController
public class UserController {

    private final UserDAO userDAO;

    UserController(@NotNull UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    @RequestMapping(path = "/signup", method = RequestMethod.POST, produces = "application.properties/json", consumes = "application/json")
    public ResponseEntity signup() {

        Connection connection = null;
        //URL к базе состоит из протокола:подпротокола://[хоста]:[порта_СУБД]/[БД] и других_сведений
        String url = "jdbc:postgresql://localhost:5000/myforum";
        //Имя пользователя БД
        String name = "egor";
        //Пароль
        String password = "";

        try {
            //Загружаем драйвер
            Class.forName("org.postgresql.Driver");
            System.out.println("Драйвер подключен");
            //Создаём соединение
            connection = DriverManager.getConnection(url, name, password);
            System.out.println("Соединение установлено");
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new JSONObject().put("msg", "Тест пройден").toString());

    }


}
