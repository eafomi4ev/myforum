package application.controllers;

import application.models.*;
import application.services.PostDAO;
import application.services.ThreadDAO;
import application.services.ThreadVoteDAO;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(path = "/api/thread")
public class ThreadController {
    private final ThreadDAO threadServiceDAO;
    private final ThreadVoteDAO threadVoteServiceDAO;
    private final PostDAO postServiceDAO;

    @Autowired
    ThreadController(ThreadDAO threadServiceDAO, ThreadVoteDAO threadVoteServiceDAO, PostDAO postServiceDAO) {
        this.threadServiceDAO = threadServiceDAO;
        this.threadVoteServiceDAO = threadVoteServiceDAO;
        this.postServiceDAO = postServiceDAO;
    }

    @Nullable
    private ThreadModel getBySlugOrIdJoinAll(final String slugOrId) {
        final ThreadModel thread;
        try {
            if (slugOrId.matches("\\d+")) {
                final Integer id = Integer.parseInt(slugOrId);
                thread = threadServiceDAO.getByIdWithFullData(id);
            } else {
                thread = threadServiceDAO.getBySlugWithFullData(slugOrId);
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return thread;
    }


    @PostMapping(path = "/{slugOrId}/create")
    public ResponseEntity slugCreate(@PathVariable(name = "slugOrId") final String slugOrId,
                                     @RequestBody List<PostModel> posts) {
        if (posts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ThreadModel thread;
        try {
            if (slugOrId.matches("\\d+")) {
                final Integer id = Integer.parseInt(slugOrId);
                thread = threadServiceDAO.getByIdWithForumData(id);
            } else {
                thread = threadServiceDAO.getBySlugWithForumData(slugOrId);
            }
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

        final List<Integer> children = postServiceDAO.getChildren(thread.getId());
        for (PostModel post : posts) {
            if (post.getParent() != null &&
                    post.getParent() != 0 &&
                    !children.contains(post.getParent())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }

        try {
            postServiceDAO.create(thread, posts);
        } catch(BatchUpdateException e) {
            return ResponseEntity.notFound().build();
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(posts);
    }

    @GetMapping(path = "/{slugOrId}/details")
    public ResponseEntity getSlugDetails(@PathVariable(name = "slugOrId") final String slugOrId) {

        final ThreadModel thread = getBySlugOrIdJoinAll(slugOrId);
        if (thread == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(thread);

    }

    @PostMapping(path = "/{slugOrId}/details")
    public ResponseEntity setSlugDetails(@PathVariable(name = "slugOrId") final String slugOrId,
                                         @RequestBody ThreadUpdateModel threadUpdate) {

        final ThreadModel thread = getBySlugOrIdJoinAll(slugOrId);
        if (thread == null) {
            return ResponseEntity.notFound().build();
        }

        threadServiceDAO.update(thread, threadUpdate);

        return ResponseEntity.ok(thread);
    }

    @GetMapping(path = "/{slugOrId}/posts")
    public ResponseEntity slugPosts(@PathVariable(name = "slugOrId") final String slugOrId,
                                    @RequestParam(name = "limit", required = false, defaultValue = "0") final Integer limit,
                                    @RequestParam(name = "marker", required = false, defaultValue = "0") final String marker,
                                    @RequestParam(name = "sort", required = false, defaultValue = "flat") final String sort,
                                    @RequestParam(name = "desc", required = false, defaultValue = "false") final Boolean desc) {
        final ThreadModel thread = getBySlugOrIdJoinAll(slugOrId);
        if (thread == null) {
            return ResponseEntity.notFound().build();
        }

        Integer offset;
        if (marker.matches("\\d+")) {
            offset = Integer.parseInt(marker);
        } else {
            return ResponseEntity.notFound().build();
        }

        final PostsModel posts = new PostsModel();
        Integer size;
        try {
            switch (sort) {
                case "flat":
                    posts.setPosts(postServiceDAO.getPostsFlat(thread, limit, offset, desc));
                    size = posts.getPosts().size();
                    break;
                case "tree":
                    posts.setPosts(postServiceDAO.getPostsTree(thread, limit, offset, desc));
                    size = posts.getPosts().size();
                    break;
                case "parent_tree":
                    final List<Integer> parents = postServiceDAO.getParents(thread, limit, offset, desc);
                    size = parents.size();
                    posts.setPosts(postServiceDAO.getPostsParentTree(thread, desc, parents));
                    break;
                default:
                    return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

        offset += size;
        posts.setMarker(offset.toString());

        return ResponseEntity.ok(posts);
    }

    @PostMapping(path = "/{slugOrId}/vote")
    public ResponseEntity slugVote(@PathVariable(name = "slugOrId") final String slugOrId,
                                   @RequestBody ThreadVoteModel vote) {

        final ThreadModel thread = getBySlugOrIdJoinAll(slugOrId);
        if (thread == null) {
            return ResponseEntity.notFound().build();
        }

        ThreadVoteModel existingVote;
        try {
            existingVote = threadVoteServiceDAO.get(thread, vote);
        } catch (EmptyResultDataAccessException e) {
            existingVote = null;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

        try {
            if (existingVote == null) {
                threadVoteServiceDAO.create(thread, vote);
            } else {
                vote.setId(existingVote.getId());
                threadVoteServiceDAO.insert(thread, vote);
            }
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(thread);
    }
}
