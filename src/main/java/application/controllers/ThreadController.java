package application.controllers;

import application.models.PostModel;
import application.models.PostPageModel;
import application.models.ThreadModel;
import application.models.VoteModel;
import application.services.ForumDAO;
import application.services.ThreadDAO;
import org.springframework.dao.DataIntegrityViolationException;
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
@RequestMapping(path = "/api/thread/{threadSlugOrID}")
public final class ThreadController {

    private ThreadDAO threadServiceDAO;
    private ForumDAO forumServiceDAO;

    public ThreadController(ThreadDAO threadServiceDAO) {
        this.threadServiceDAO = threadServiceDAO;
    }

    @RequestMapping(path = "/create")
    public ResponseEntity create(@RequestBody List<PostModel> posts, @PathVariable("threadSlugOrID") String threadSlugOrID) {
        int id;
        try {
            id = getThreadIdFromString(threadSlugOrID);//Содержит запрос к БД
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (posts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Integer> parentsID = new ArrayList<>();
        List<PostModel> list = new ArrayList<>();
        for (PostModel post : posts) {
            post.setThread(id);

            try {
                threadServiceDAO.createPost(post);
                list.add(threadServiceDAO.getLastAddedPost());//TODO: Как-то запоминать id добавленных постов и потом вытаскивать их
            } catch (DuplicateKeyException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(threadServiceDAO.getPostsInThread(id));//409
            } catch (IndexOutOfBoundsException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(list);
    }

    @RequestMapping(path = "/vote")
    public ResponseEntity createVote(@RequestBody VoteModel vote, @PathVariable("threadSlugOrID") String threadSlugOrID) {
        int id;
        try {
            id = getThreadIdFromString(threadSlugOrID);
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.notFound().build();
        }


        List<ThreadModel> threads = threadServiceDAO.getThreadById(id);

        if (threads.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            threadServiceDAO.createVote(vote, id);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.notFound().build();
        }

        threads = threadServiceDAO.getThreadById(id);// todo: Возможно как-то пересчитывать значение голосов непосредственно здесь и не ходить второй в БД


        return ResponseEntity.status(HttpStatus.OK).body(threads.get(0));
    }

    @RequestMapping(path = "/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity details(@PathVariable("threadSlugOrID") String threadSlugOrID) {
        final List<ThreadModel> threads;
        try {
            threads = threadServiceDAO.getThreadById(getThreadIdFromString(threadSlugOrID));
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.notFound().build();
        }
        if (!threads.isEmpty()) {
            return ResponseEntity.ok(threads.get(0));
        } else {
            return ResponseEntity.notFound().build();
        }


    }

    @RequestMapping(path = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity posts(@PathVariable("threadSlugOrID") String threadSlugOrID,
                                @RequestParam(value = "limit", required = false, defaultValue = "null") Integer limit,
                                @RequestParam(value = "marker", required = false, defaultValue = "0") int marker,
                                @RequestParam(value = "sort", required = false, defaultValue = "flat") String sort,
                                @RequestParam(value = "desc", required = false, defaultValue = "false") boolean descString) {
        int threadID;
        try {
            threadID = getThreadIdFromString(threadSlugOrID);
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.notFound().build();
        }

        List<PostModel> posts;
        try {
            posts = threadServiceDAO.getPostsInThread(threadID, limit, marker, sort, descString);
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.notFound().build();
        }
        if (!posts.isEmpty())
            marker += limit;


        PostPageModel postPage = new PostPageModel(Integer.toString(marker), posts);

        return ResponseEntity.ok(postPage);


    }

    @RequestMapping(path = "/details", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity detailsUpdate(@PathVariable("threadSlugOrID") String threadSlugOrID, @RequestBody ThreadModel thread) {
        int threadID;
        try {
            threadID = getThreadIdFromString(threadSlugOrID);
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.notFound().build();
        }

        try {
            threadServiceDAO.detailsUpdate(threadID, thread);
            return ResponseEntity.ok().body(threadServiceDAO.getThreadById(threadID).get(0));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.OK).body(threadServiceDAO.getThreadById(threadID).get(0));
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.notFound().build();
        }


    }


    private int getThreadIdFromString(String threadSlugOrID) {
        int id;
        try {
            id = Integer.parseInt(threadSlugOrID);
        } catch (NumberFormatException e) {
            final List<ThreadModel> tmpThread = threadServiceDAO.getThreadBySlug(threadSlugOrID);
            id = tmpThread.get(0).getId();
        }
        return id;
    }


}
