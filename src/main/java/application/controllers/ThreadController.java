package application.controllers;

import application.models.PostModel;
import application.models.PostPageModel;
import application.models.ThreadModel;
import application.models.VoteModel;
import application.services.ForumDAO;
import application.services.ThreadDAO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by egor on 15.03.17.
 */

@RestController
@RequestMapping(path = "/api/thread/{threadSugOrID}")
public final class ThreadController {

    private ThreadDAO threadServiceDAO;
    private ForumDAO forumServiceDAO;

    public ThreadController(ThreadDAO threadServiceDAO) {
        this.threadServiceDAO = threadServiceDAO;
    }

    @RequestMapping(path = "/create")
    public ResponseEntity create(@RequestBody List<PostModel> posts, @PathVariable("threadSugOrID") String threadSugOrID) {
        int id = getThreadIdFromString(threadSugOrID);//Содержит запрос к БД

        List<Integer> parentsID = new ArrayList<>();
        List<PostModel> list = new ArrayList<>();
        for (PostModel post : posts) {
            post.setThread(id);

            try {
                threadServiceDAO.createPost(post);
                list.add(threadServiceDAO.getLastAddedPost());//TODO: Как-то запоминать id добавленных постов и потом вытаскивать их
            } catch (DuplicateKeyException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(threadServiceDAO.getPostsInThread(id));//409
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(list);
    }

    @RequestMapping(path = "/vote")
    public ResponseEntity createVote(@RequestBody VoteModel vote, @PathVariable("threadSugOrID") String threadSugOrID) {

        int id = getThreadIdFromString(threadSugOrID);


        List<ThreadModel> threads = threadServiceDAO.getThreadById(id);

        if (threads.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        threadServiceDAO.createVote(vote, id);

        threads = threadServiceDAO.getThreadById(id);// todo: Возможно как-то пересчитывать значение голосов непосредственно здесь и не ходить второй в БД


        return ResponseEntity.status(HttpStatus.OK).body(threads.get(0));
    }

    @RequestMapping(path = "/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity details(@PathVariable("threadSugOrID") String threadSugOrID) {
        final List<ThreadModel> threads = threadServiceDAO.getThreadById(getThreadIdFromString(threadSugOrID));
        if (!threads.isEmpty()) {
            return ResponseEntity.ok(threads.get(0));
        } else {
            return ResponseEntity.notFound().build();
        }


    }

    @RequestMapping(path = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity posts(@PathVariable("threadSugOrID") String threadSugOrID,
                                @RequestParam(value = "limit", required = false, defaultValue = "null") Integer limit,
                                @RequestParam(value = "marker", required = false, defaultValue = "0") int marker,
                                @RequestParam(value = "sort", required = false, defaultValue = "flat") String sort,
                                @RequestParam(value = "desc", required = false, defaultValue = "false") boolean descString) {
        int threadID = getThreadIdFromString(threadSugOrID);

        List<PostModel> posts = threadServiceDAO.getPostsInThread(threadID, limit, marker, sort, descString);
        marker += limit;
        PostPageModel postPage = new PostPageModel(marker, posts);

        if (!posts.isEmpty()) {
            return ResponseEntity.ok(postPage.toJson());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    private int getThreadIdFromString(String threadSugOrID) {
        int id;
        try {
            id = Integer.parseInt(threadSugOrID);
        } catch (NumberFormatException e) {
            final List<ThreadModel> tmpThread = threadServiceDAO.getThreadBySlug(threadSugOrID);
            id = tmpThread.get(0).getId();
        }
        return id;
    }


}
