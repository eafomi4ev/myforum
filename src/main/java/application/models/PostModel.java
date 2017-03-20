package application.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

/**
 * Created by egor on 15.03.17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostModel {

    private int id;
    private int parent;
    private String author;
    private String message;

    private Boolean isEdited;

    private String forum;
    private int thread;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Timestamp created;

    public PostModel(@JsonProperty("id") int id, @JsonProperty("parent") int parent, @JsonProperty("author") String author,
                     @JsonProperty("message") String message, @JsonProperty("isEdited") boolean isEdited,
                     @JsonProperty("forum") String forum, @JsonProperty("thread") int thread,
                     @JsonProperty("created") Timestamp created) {

        this.id = id;
        this.parent = parent;
        this.author = author;
        this.message = message;
        this.isEdited = isEdited;
        this.forum = forum;
        this.thread = thread;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsEdited() {
        return this.isEdited;
    }

    public void setIsEdited(boolean isEdited) {
        this.isEdited = isEdited;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }
}
