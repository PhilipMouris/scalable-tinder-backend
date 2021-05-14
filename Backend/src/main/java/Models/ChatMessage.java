package Models;

import java.sql.Timestamp;
import java.util.Date;

public class ChatMessage {
    private String sourceUserId;
    private String text;
    private String media;
    private String createdAt;

    public ChatMessage(String sourceUserId, String text, String media) {
        this.sourceUserId = sourceUserId;
        this.text = text;
        this.media = media;
        this.createdAt =  new Timestamp(new Date().getTime()).toString();
    }

    public ChatMessage(){
        super();
    }

    public String getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(String sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
