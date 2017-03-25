package application.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by egor on 17.03.17.
 */


public class VoteModel {

    private String nickname;
    private int voice;
    private int thread_id;

    @JsonCreator
    public VoteModel(@JsonProperty("nickname") String nickname, @JsonProperty("voice") int voice, @JsonProperty("thread_id") int thread_id) {
        this.nickname = nickname;
        this.voice = voice;
        this.thread_id = thread_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }

    public int getThread_id() {
        return thread_id;
    }

    public void setThread_id(int thread_id) {
        this.thread_id = thread_id;
    }
}
