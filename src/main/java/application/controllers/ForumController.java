package application.controllers;

import application.models.ForumModel;
import application.models.ThreadModel;
import application.models.UserModel;
import application.services.ForumDAO;
import application.services.UserDAO;
import application.support.ResponseMsg;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by egor on 06.03.17.
 */

@RestController
@RequestMapping(path = "/api/forum")
public final class ForumController {

    private ForumDAO forumServiceDAO;
    private UserDAO userServiceDAO;

    public ForumController(ForumDAO forumServiceDAO, UserDAO userServiceDAO) {
        this.forumServiceDAO = forumServiceDAO;
        this.userServiceDAO = userServiceDAO;
    }


    @RequestMapping(path = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity forumCreate(@RequestBody ForumModel forum) {

        try {
            forumServiceDAO.create(forum);
            return ResponseEntity.status(HttpStatus.CREATED).body(forumServiceDAO.getForumbySlug(forum.getSlug()));//201
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumServiceDAO.getForumbySlug(forum.getSlug()));//409
        } catch (DataIntegrityViolationException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMsg("Пользователь отсутствует в системе."));//404

        }
    }

    //Получение информации о форуме по его идентификатору.
    @RequestMapping(path = "/{slug}/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity forumDetails(@PathVariable("slug") String slug) {
        try {
            return ResponseEntity.ok(forumServiceDAO.getForumbySlug(slug));//200
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMsg("Форум отсутствует в системе."));//404
        }
    }

    //Создание ветки в форуме
    @RequestMapping(path = "/{slug}/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createThread(@PathVariable("slug") String forumSlug, @RequestBody ThreadModel thread) {
        List<ThreadModel> threads;
        UserModel user;
        try {
//            if (thread.getForum() == null) {
//                thread.setForum(forumSlug);
//            }

            threads = forumServiceDAO.getThreadBySlug(thread.getSlug());
            if (threads != null && !threads.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(threads.get(0));
            }

            ForumModel forum = forumServiceDAO.getForumbySlug(forumSlug);
            if (forum == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();//404

            }

            forumServiceDAO.createThread(thread);
            threads = forumServiceDAO.getThreads(thread.getTitle(), thread.getAuthor());
            if (threads.isEmpty()) {
                ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(threads.get(0));//201
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMsg("Форум отсутствует в системе."));//404
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumServiceDAO.getThreads(thread.getTitle(), thread.getAuthor()));//409
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMsg("Юзер отсутствует в системе."));//404

        }
    }

    //Получение информации о ветках форума по заданным параметрам. Если desc = true, то created д.б. <= since.
    @RequestMapping(path = "/{slug}/threads", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getThreadsList(@PathVariable("slug") String forumSlug,
                                         @RequestParam(value = "limit", required = false) String limit,
                                         @RequestParam(value = "since", required = false) String tmpSince,
                                         @RequestParam(value = "desc", required = false) boolean desc) {

        List<ThreadModel> threadList = forumServiceDAO.getThreads(forumSlug);
        if (threadList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Timestamp since = null;
        if (!StringUtils.isEmpty(tmpSince)) //Todo: перенести этот блок кода в ForumDAO
        {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                since = new Timestamp(dateFormat.parse(tmpSince).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        threadList = forumServiceDAO.getThreads(forumSlug, limit, since, desc);
        if (!threadList.isEmpty()) {
            return ResponseEntity.ok(threadList);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("[]");//404
        }


    }
}

