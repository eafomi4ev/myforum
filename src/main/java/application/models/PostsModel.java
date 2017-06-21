package application.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PostsModel {
    @JsonProperty
    private String marker;

    @JsonProperty
    private List<PostModel> posts;

    @JsonCreator
    public PostsModel(@JsonProperty("marker") String marker, @JsonProperty("posts") List<PostModel> posts){
        this.marker = marker;
        this.posts = posts;
    }

    public PostsModel(){
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public List<PostModel> getPosts() {
        return posts;
    }

    public void setPosts(List<PostModel> posts) {
        this.posts = posts;
    }
}
