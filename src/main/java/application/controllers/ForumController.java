package application.controllers;

import application.support.ResponseMsg;
import application.models.ForumModel;
import application.services.ForumDAO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by egor on 06.03.17.
 */

@RestController
@RequestMapping(path = "/api/forum")
public final class ForumController {
    private ForumDAO forumServiceDAO;

    ForumController(JdbcTemplate jdbcTemplate) {
        this.forumServiceDAO = new ForumDAO(jdbcTemplate);
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody ForumModel forum) {

        try {
            forumServiceDAO.create(forum);
            return ResponseEntity.status(HttpStatus.CREATED).body(forum);//201
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumServiceDAO.getbySlug(forum.getSlug()));//409
        } catch (DataIntegrityViolationException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMsg("Пользователь отсутствует в системе."));//404

        }


    }




}
