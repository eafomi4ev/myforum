package application.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

/**
 * Created by egor on 11.03.17.
 */

public class ThreadModel {
    private int id;
    private String title;
    private String author;
    private String forum;
    private String message;
    private int votes;
    private String slug;

//    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm a z")
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private Timestamp created;

    @JsonCreator
    public ThreadModel(@JsonProperty("id") int id,
                       @JsonProperty("title") String title, @JsonProperty("author") String author,
                       @JsonProperty("forum") String forum, @JsonProperty("message") String message,
                       @JsonProperty("votes") int votes, @JsonProperty("slug") String slug,
                       @JsonProperty("created") Timestamp created) {

        this.id = id;
        this.title = title;
        this.author = author;
        this.forum = forum;
        this.message = message;
        this.votes = votes;
        this.slug = slug;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }
}
