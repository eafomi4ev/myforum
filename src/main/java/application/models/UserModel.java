package application.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by egor on 06.03.17.
 */
public class UserModel {

    // Имя пользователя (уникальное поле). Данное поле допускает только латиницу, цифры и знак подчеркивания.
    // Сравнение имени регистронезависимо.
    // это поле - id в БД
    private String nickname;

    //Полное имя пользователя.
    private String fullname;

    //Описание пользователя.
    private String about;

    //Почтовый адрес пользователя (уникальное поле).
    private String email;

    public UserModel(){}

//    @JsonCreator
//    public UserModel(@JsonProperty("id") int id, @JsonProperty("nickname") String nickname, @JsonProperty("fullname") String fullname,
//                     @JsonProperty("about") String about, @JsonProperty("email") String email) {
//
//        this.id = id;
//        this.nickname = nickname;
//        this.fullname = fullname;
//        this.about = about;
//        this.email = email;
//    }

    @JsonCreator
    public UserModel(@JsonProperty("nickname") String nickname, @JsonProperty("fullname") String fullname,
                     @JsonProperty("about") String about, @JsonProperty("email") String email) {

        this.nickname = nickname;
        this.fullname = fullname;
        this.about = about;
        this.email = email;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {

        return nickname;
    }

    public String getFullname() {
        return fullname;
    }

    public String getAbout() {
        return about;
    }

    public String getEmail() {
        return email;
    }
}
