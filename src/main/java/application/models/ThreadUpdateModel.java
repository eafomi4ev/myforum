package application.models;

/**
 * Created by egor on 18.06.17.
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ThreadUpdateModel {
    @JsonProperty
    private String title;
    @JsonProperty
    private String message;

    @JsonCreator
    public ThreadUpdateModel(@JsonProperty("title") String title,
                        @JsonProperty("message") String message){
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
