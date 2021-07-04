package Models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
    
	
@Entity
@Table(name = "chats")
public class Chat {
    @Id
    private String id;
    private String _key;
    private String userAId;
    private String userBId;
    private List<ChatMessage> messages;
    private String createdAt;

    public Chat(String _key, String userAId, String userBId, List<ChatMessage> messages) {
        this._key = _key;
        this.userAId = userAId;
        this.userBId = userBId;
        this.messages = messages;
        this.createdAt =  new Timestamp(new Date().getTime()).toString();
    }

    public Chat(){
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String get_key() {
        return _key;
    }

    public void set_key(String _key) {
        this._key = _key;
    }

    public String getUserA_id() {
        return userAId;
    }

    public void setUserA_id(String userAId) {
        this.userAId = userAId;
    }

    public String getUserB_id() {
        return userBId;
    }

    public void setUserB_id(String userBId) {
        this.userBId = userBId;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
