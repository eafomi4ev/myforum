package application.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by egor on 10.03.17.
 */

public class ForumModel {
    @JsonProperty
    private String title;

    @JsonProperty
    private String user;

    @JsonProperty
    private String slug;

    @JsonProperty
    private int posts;

    @JsonProperty
    private int threads;

    @JsonCreator
    public ForumModel(@JsonProperty("title") String title, @JsonProperty("user") String user, @JsonProperty("slug") String slug,
                      @JsonProperty("post") int posts, @JsonProperty("threads") int threads) {

        this.title = title;
        this.user = user;
        this.slug = slug;
        this.posts = posts;
        this.threads = threads;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }



}
