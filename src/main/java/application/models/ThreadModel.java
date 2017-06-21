package application.models;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ThreadModel {
    @JsonProperty
    private int id;
    @JsonProperty
    private String title;
    @JsonProperty
    private String author;

    @JsonIgnore
    private int userId;

    @JsonProperty
    private String forum;

    @JsonIgnore
    private int forumId;

    @JsonProperty
    private String message;
    @JsonProperty
    private int votes;
    @JsonProperty
    private String slug;
    private String created;

    @JsonCreator
    public ThreadModel(@JsonProperty("id") int id, @JsonProperty("title") String title,
                  @JsonProperty("author") String author, @JsonProperty("forum") String forum,
                  @JsonProperty("message") String message, @JsonProperty("votes") int votes,
                  @JsonProperty("slug") String slug, @JsonProperty("created") String created) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.forum = forum;
        this.message = message;
        this.votes = votes;
        this.slug = slug;
        this.created = created;
    }

    public ThreadModel() {
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public int getForumId() {
        return forumId;
    }

    public void setForumId(int forumId) {
        this.forumId = forumId;
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

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern= "yyyy-MM-dd'T'HH:mm:ss.SSS'+03:00'")
    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
