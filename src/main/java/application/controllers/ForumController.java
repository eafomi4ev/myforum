package application.controllers;

import application.models.ForumModel;
import application.models.ThreadModel;
import application.services.ForumDAO;
import application.services.UserDAO;
import application.support.ResponseMsg;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * Created by egor on 06.03.17.
 */

@RestController
@RequestMapping(path = "/api/forum")
public final class ForumController {
    private ForumDAO forumServiceDAO;
    private UserDAO userServiceDAO;

    ForumController(JdbcTemplate jdbcTemplate, UserDAO userServiceDAO) {
        this.forumServiceDAO = new ForumDAO(jdbcTemplate, userServiceDAO);
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity forumCreate(@RequestBody ForumModel forum) {

        try {
            forumServiceDAO.create(forum);
//            JSONObject json = new JSONObject();
//            json.put("title", forum.getTitle());
//            json.put("user", forum.getUser());
//            json.put("slug", forum.getSlug());
            return ResponseEntity.status(HttpStatus.CREATED).body(forumServiceDAO.getbySlug(forum.getSlug()));//201
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumServiceDAO.getbySlug(forum.getSlug()));//409
        } catch (DataIntegrityViolationException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMsg("Пользователь отсутствует в системе."));//404

        }
    }

    //Получение информации о форуме по его идентификатору.
    @RequestMapping(path = "/{slug}/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity forumDetails(@PathVariable("slug") String slug) {
        try {
            return ResponseEntity.ok(forumServiceDAO.getbySlug(slug));//200
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMsg("Форум отсутствует в системе."));//404
        }
//        try {
//        } catch (DuplicateKeyException e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumServiceDAO.getbySlug(forum.getSlug()));//409
//        } catch (DataIntegrityViolationException exception) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMsg("Пользователь отсутствует в системе."));//404
//
//        }
    }

    //Получение информации о форуме по его идентификатору.
    @RequestMapping(path = "/{slug}/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createThread(@PathVariable("slug") String slug, @RequestBody ThreadModel thread) {
        try {
            thread.setSlug(slug);
            forumServiceDAO.createThread(thread);
            return ResponseEntity.status(HttpStatus.CREATED).body(thread);//201
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMsg("Форум отсутствует в системе."));//404
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumServiceDAO.getThreadBySlug(slug));//409
        }
//        try {
//        } catch (DuplicateKeyException e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumServiceDAO.getbySlug(forum.getSlug()));//409
//        } catch (DataIntegrityViolationException exception) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMsg("Пользователь отсутствует в системе."));//404
//
//        }
    }





}
