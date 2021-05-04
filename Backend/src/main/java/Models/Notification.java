package Models;

import com.google.gson.Gson;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "notifications")
public class Notification {
    private String _key;
    private String id;
    private int userID;
    private String type;
    private String title;
    private String body;
    private String createdAt;

    public Notification(int userID,String type,String title,String body){
        this.userID = userID;
        this.type = type;
        this.title = title;
        this.body = body;
        this.createdAt = new Timestamp(new Date().getTime()).toString();
    }
    public String getTitle(){
        return title;
    }
    public String getBody(){
        return body;
    }
    public Notification(){
        this.createdAt = new Timestamp(new Date().getTime()).toString();
    }
     @Override
    public String toString(){
           Gson gson = new Gson();
           return gson.toJson(this).toString();
    }

}
