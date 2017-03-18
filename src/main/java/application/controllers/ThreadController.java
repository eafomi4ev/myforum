package application.controllers;

import application.models.PostModel;
import application.models.ThreadModel;
import application.models.VoteModel;
import application.services.ForumDAO;
import application.services.ThreadDAO;
import org.springframework.dao.DuplicateKeyException;
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
@RequestMapping(path = "/api/thread/{threadId}")
public class ThreadController {

    private ThreadDAO threadServiceDAO;
    private  ForumDAO forumServiceDAO;

    public ThreadController(ThreadDAO threadServiceDAO) {
        this.threadServiceDAO = threadServiceDAO;
    }

    @RequestMapping(path = "/create")
    public ResponseEntity create(@RequestBody List<PostModel> posts, @PathVariable("threadId") String threadId) {
        int id = -1;
        try {
            id = Integer.parseInt(threadId);
            for (PostModel post : posts) {
                post.setThread(id);
            }

        } catch (NumberFormatException e){
            List<ThreadModel> tmpThread = threadServiceDAO.getThreadBySlug(threadId);
            id = tmpThread.get(0).getId();
            for (PostModel post : posts) {
                post.setThread(id);
            }
        }

        try {
            threadServiceDAO.createPost(posts);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(threadServiceDAO.getPostsInThread(id));

        }

        List<PostModel> list = threadServiceDAO.getPostsInThread(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(threadServiceDAO.getPostsInThread(id));
    }

    @RequestMapping(path = "/vote")
    public ResponseEntity createVote(@RequestBody VoteModel vote, @PathVariable("threadId") String threadId){

        int id;
        try {
            id = Integer.parseInt(threadId);
        } catch (NumberFormatException e){
            List<ThreadModel> tmpThread = threadServiceDAO.getThreadBySlug(threadId);
            id = tmpThread.get(0).getId();
        }

        List<ThreadModel> threads = threadServiceDAO.getThreadById(id);

        if (threads.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        threadServiceDAO.createVote(vote, id);

        threads = threadServiceDAO.getThreadById(id);// todo: Возможно как-то пересчитывать значение голосов непосредственно здесь и не ходить второй в БД


        return ResponseEntity.status(HttpStatus.OK).body(threads.get(0));
    }

}
