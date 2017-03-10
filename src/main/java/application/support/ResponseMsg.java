package application.support;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by egor on 10.03.17.
 */
public final class ResponseMsg {
    @JsonProperty
    private String msg;

    @JsonCreator
    public ResponseMsg(@JsonProperty String msg) {
        this.msg = msg;
    }
}
