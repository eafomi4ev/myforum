package application.controllers;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

/**
 * Created by egor on 05.03.17.
 */

@Component
@RestController
public class UserController {

    UserController(){
    }

    @RequestMapping(path = "/create", method = RequestMethod.GET, produces = "application/json", consumes = "application/json")
    public String create() {
        Connection connection = null;
        //URL к базе состоит из протокола:подпротокола://[хоста]:[порта_СУБД]/[БД] и других_сведений
        String url = "jdbc:postgresql://localhost:5000/myforum";
        //Имя пользователя БД
        String name = "postgres";
        //Пароль
        String password = "";
        try {
            //Загружаем драйвер
            Class.forName("org.postgresql.Driver");
            System.out.println("Драйвер подключен");
            //Создаём соединение
            connection = DriverManager.getConnection(url, name, password);
            System.out.println("Соединение установлено");


            return new JSONObject().put("msg", "Связь установлена").toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return new JSONObject().put("msg", "Связь установлена").toString();
    }

}

