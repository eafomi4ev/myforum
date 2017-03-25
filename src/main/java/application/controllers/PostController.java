package application.controllers;

import application.models.ForumModel;
import application.models.PostModel;
import application.models.ThreadModel;
import application.models.UserModel;
import application.services.ForumDAO;
import application.services.PostDAO;
import application.services.ThreadDAO;
import application.services.UserDAO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by egor on 21.03.17.
 */

@RestController
@RequestMapping(path = "/api/post/{id}")
public class PostController {

    private PostDAO postServiceDAO;
    private ThreadDAO threadServiceDAO;
    private UserDAO userServiceDAO;
    private ForumDAO forumServiceDAO;

    public PostController(PostDAO postServiceDAO, ThreadDAO threadServiceDAO, UserDAO userServiceDAO, ForumDAO forumServiceDAO) {
        this.postServiceDAO = postServiceDAO;
        this.threadServiceDAO = threadServiceDAO;
        this.userServiceDAO = userServiceDAO;
        this.forumServiceDAO = forumServiceDAO;
    }

    @RequestMapping(path = "/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity details(@PathVariable("id") int id, @RequestParam(value = "related", required = false) String related) {
        PostModel post = null;
        try {
            post = postServiceDAO.getPostByID(id);
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.notFound().build();
        }

        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> map = new HashMap<>();

        if (related != null) {
            String[] relatedWord = related.split(",");

            for (String word: relatedWord) {
                if (word.equals("user")) {
                    UserModel user = userServiceDAO.get(post.getAuthor());
                    map.put("author", user);
                }

                if (word.equals("thread")) {
                    ThreadModel thread = threadServiceDAO.getThreadById(post.getThread()).get(0);
                    map.put("thread", thread);
                }

                if (word.equals("forum")) {
                    ForumModel forum = forumServiceDAO.getForumbySlug(post.getForum());
                    map.put("forum", forum);
                }
            }
        }

        map.put("post", post);
        return ResponseEntity.ok(map);
    }

    @RequestMapping(path = "/details", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity detailsUpdate(@PathVariable("id") int id, @RequestBody PostModel post) {
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        if (post.getMessage() != null) {
            try {
                if (postServiceDAO.detailsUpdate(id, post) == 0) {
                    return ResponseEntity.notFound().build();
                }
            } catch (IndexOutOfBoundsException e) {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok(postServiceDAO.getPostByID(id));
    }

}

