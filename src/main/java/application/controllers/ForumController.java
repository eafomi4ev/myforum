package application.controllers;


import application.models.ForumModel;
import application.models.ThreadModel;
import application.models.UserModel;
import application.services.ForumDAO;
import application.services.ThreadDAO;
import application.services.UserDAO;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/forum")
public class ForumController {
    private ForumDAO forumServiceDAO;
    private ThreadDAO threadServiceDAO;
    private UserDAO userServiceDAO;

    @Autowired
    ForumController(ForumDAO forumServiceDAO, ThreadDAO threadServiceDAO, UserDAO userServiceDAO) {
        this.forumServiceDAO = forumServiceDAO;
        this.threadServiceDAO = threadServiceDAO;
        this.userServiceDAO = userServiceDAO;
    }

    @PostMapping(path = "/create")
    public ResponseEntity forumCreate(@RequestBody ForumModel forum) {

        UserModel user;
        try {
            user = userServiceDAO.get(forum.getUser());
        } catch (DataAccessException e) {
            return ResponseEntity.notFound().build();
        }

        forum.setUserId(user.getId());
        if (!forum.getUser().equals(user.getNickname())) {
            forum.setUser(user.getNickname());
        }

        try {
            forumServiceDAO.create(forum);
        } catch (DuplicateKeyException e) {
            forum = getBySlugWithAuthor(forum.getSlug());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forum);
        } catch (DataAccessException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(forum);
    }

    @GetMapping(path = "/{slug}/details")
    public ResponseEntity slugDetails(@PathVariable(name = "slug") String slug) {
        ForumModel forum = getBySlugWithAuthor(slug);
        if (forum == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(forum);
    }

    @PostMapping(path = "/{slug}/create")
    public ResponseEntity slugCreate(@PathVariable(name = "slug") String slug, @RequestBody ThreadModel thread) {

        UserModel user;
        try {
            user = userServiceDAO.get(thread.getAuthor());
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

        ForumModel forum = getBySlugWithAuthor(slug);
        if (forum == null) {
            return ResponseEntity.notFound().build();
        }

        thread.setUserId(user.getId());
        thread.setForumId(forum.getId());
        thread.setForum(forum.getSlug());

        try {
            threadServiceDAO.create(thread);
        } catch (DuplicateKeyException e) {
            thread = threadServiceDAO.getBySlugWithFullData(thread.getSlug());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(thread);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(thread);
    }


    @GetMapping(path = "/{slug}/users")
    public ResponseEntity slugUsers(@PathVariable(name = "slug") String slug,
                                    @RequestParam(name = "limit", required = false) Integer limit,
                                    @RequestParam(name = "since", required = false) String since,
                                    @RequestParam(name = "desc", required = false) Boolean desc) {

        ForumModel forum = getBySlugWithAuthor(slug);
        if (forum == null) {
            return ResponseEntity.notFound().build();
        }

        List<UserModel> users = userServiceDAO.getUsersInForum(forum, limit, since, desc);

        return ResponseEntity.ok(users);
    }

    @GetMapping(path = "/{slug}/threads")
    public ResponseEntity slugThreads(@PathVariable(name = "slug") String slug,
                                      @RequestParam(name = "limit", required = false) Integer limit,
                                      @RequestParam(name = "since", required = false) String since,
                                      @RequestParam(name = "desc", required = false) Boolean desc) {
        ForumModel forum = getBySlug(slug);
        if (forum == null) {
            return ResponseEntity.notFound().build();
        }

        List<ThreadModel> threads;
        try {
            threads = threadServiceDAO.getByForumId(forum, limit, since, desc);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(threads);
    }

    @Nullable
    private ForumModel getBySlug(String slug) {
        ForumModel forum;
        try {
            forum = forumServiceDAO.getBySlug(slug);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return forum;
    }

    @Nullable
    private ForumModel getBySlugWithAuthor(String slug) {
        ForumModel forum;
        try {
            forum = forumServiceDAO.getBySlugWithAuthor(slug);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return forum;
    }
}
