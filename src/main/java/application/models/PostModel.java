package application.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PostModel {
    @JsonProperty
    private int id;
    @JsonProperty
    private Integer parent = 0;
    @JsonProperty
    private boolean isEdited;
    @JsonProperty
    private String author;
    @JsonProperty
    private String forum;
    @JsonProperty
    private String message;
    @JsonProperty
    private int thread;
    @JsonProperty
    private String created;

    private int forumId;
    private int userId;

    @JsonCreator
    public PostModel(@JsonProperty("id") int id, @JsonProperty("parent") int parent,
                @JsonProperty("author") String author, @JsonProperty("message") String message,
                @JsonProperty("isEdited") boolean isEdited, @JsonProperty("forum") String forum,
                @JsonProperty("thread") int thread, @JsonProperty("created") String created ){
        this.id = id;
        this.parent = parent;
        this.author = author;
        this.forum = forum;
        this.message = message;
        this.thread = thread;
        this.isEdited = isEdited;
        this.created = created;
    }

    public PostModel(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(boolean edited) {
        isEdited = edited;
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

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getForumId() {
        return forumId;
    }

    public void setForumId(int forumId) {
        this.forumId = forumId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
