package application.controllers;

import application.models.PostModel;
import application.services.ThreadDAO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by egor on 15.03.17.
 */

@RestController
@RequestMapping(path = "/api/thread")
public class ThreadController {

    private ThreadDAO threadServiceDAO;

    public ThreadController(ThreadDAO threadServiceDAO) {
        this.threadServiceDAO = threadServiceDAO;
    }

    @RequestMapping(path = "/{threadId}/create")
    public ResponseEntity create(@RequestBody List<PostModel> posts, @PathVariable("threadId") String threadId) {
        int id = 0;
        try {
            id = Integer.parseInt(threadId);
        } catch (NumberFormatException e){
            System.out.print(threadId);
        }

        for (PostModel post : posts) {
            post.setThread(id);
        }

        threadServiceDAO.createPost(posts);

        List<PostModel> list=threadServiceDAO.getPostsInThread(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(list);
    }

}
