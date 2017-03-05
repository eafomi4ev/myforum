package controllers;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.UserDAO;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import services.AccountService;
//import start.UserProfile;

/**
 * Created by egor on 03.03.17.
 */

@RestController
public class UserController {

    private final UserDAO userDAO;

    UserController(@NotNull UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    @RequestMapping(path = "/test")
    public String test() {

        return new JSONObject().put("msg", "Связь есть").toString();

    }


}
