package application.models;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Created by egor on 19.03.17.
 */


public class PostPageModel {

    private String marker;
    private List<PostModel> posts;

    private final ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

    public PostPageModel(String marker, List<PostModel> posts) {
        this.marker = marker;
        this.posts = posts;
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

//    public ObjectNode toJson() {
//        final ArrayNode arrayNode = mapper.createArrayNode();
//        for (PostModel post : posts) {
//            arrayNode.add(mapper.convertValue(post, JsonNode.class));
//        }
//
//        final ObjectNode node = mapper.createObjectNode();
//        node.put("marker", marker.toString());
//        node.set("posts", arrayNode);
//
//        return node;
//    }
}
