package application.controllers;

import application.models.ForumModel;
import application.models.PostDetailsModel;
import application.models.PostModel;
import application.models.PostUpdateModel;
import application.services.ForumDAO;
import application.services.PostDAO;
import application.services.ThreadDAO;
import application.services.UserDAO;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/post")
public class PostController {
    private final PostDAO postServiceDAO;
    private final UserDAO userServiceDAO;
    private final ThreadDAO threadServiceDAO;
    private final ForumDAO forumServiceDAO;

    @Autowired
    PostController(PostDAO postServiceDAO, UserDAO userServiceDAO, ThreadDAO threadServiceDAO, ForumDAO forumServiceDAO) {
        this.postServiceDAO = postServiceDAO;
        this.userServiceDAO = userServiceDAO;
        this.threadServiceDAO = threadServiceDAO;
        this.forumServiceDAO = forumServiceDAO;
    }

    @Nullable
    private PostModel getPostDetails(final Integer id) {
        PostModel post;
        try {
            post = postServiceDAO.getById(id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return post;
    }

    @GetMapping(path = "/{id}/details")
    public ResponseEntity getIdDetails(@PathVariable(name = "id") Integer id,
                                       @RequestParam(name = "related", required = false) List<String> related) {
        final PostModel post = getPostDetails(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        final PostDetailsModel postDetails = new PostDetailsModel();
        postDetails.setPost(post);

        if (related != null) {
            for (String item : related) {
                if (item.equals("user")) {
                    postDetails.setAuthor(userServiceDAO.get(post.getAuthor()));
                } else if (item.equals("thread")) {
                    postDetails.setThread(threadServiceDAO.getByIdWithFullData(post.getThread()));
                } else if (item.equals("forum")) {
                    ForumModel forum = forumServiceDAO.getBySlugWithAuthor(post.getForum());
                    postDetails.setForum(forum);
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
        }

        return ResponseEntity.ok(postDetails);
    }

    @PostMapping(path = "/{id}/details")
    public ResponseEntity setIdDetails(@PathVariable(name = "id") int id,
                                       @RequestBody PostUpdateModel postUpdate) {

        final PostModel post = getPostDetails(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        if (postUpdate.getMessage() != null) {
            postServiceDAO.update(post, postUpdate);
        }

        return ResponseEntity.ok(post);
    }
}
