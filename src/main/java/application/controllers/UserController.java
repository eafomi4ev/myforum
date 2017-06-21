package application.controllers;

import application.models.UserModel;
import application.services.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {
    private final UserDAO userDAO;

    @Autowired
    UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @PostMapping(path = "/{nickname}/create")
    public ResponseEntity create(@PathVariable(name = "nickname") final String nickname,
                                 @RequestBody UserModel newUser) {
        newUser.setNickname(nickname);

        try {
            userDAO.create(newUser);
        } catch (DuplicateKeyException e) {
            List<UserModel> duplicates = userDAO.get(newUser);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(duplicates);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping(path = "/{nickname}/profile")
    public ResponseEntity getProfile(@PathVariable(name = "nickname") final String nickname) {
        UserModel user;
        try {
            user = userDAO.get(nickname);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

//        System.out.println("( get) user/" + nickname + "/profile");
        return ResponseEntity.ok(user);
    }

    @PostMapping(path = "/{nickname}/profile")
    public ResponseEntity setProfile(@PathVariable(name = "nickname") final String nickname,
                                     @RequestBody UserModel updateUser) {
        updateUser.setNickname(nickname);

        try {
            userDAO.updateUserProfile(updateUser);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

        return getProfile(nickname);
    }
}
