package Models;

import com.google.gson.Gson;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "notifications")
public class Notification {
    private int userID;
    private String type;
    private String title;
    private String body;
    private Date createdAt;

    public Notification(int userID,String type,String title,String body){
        this.userID = userID;
        this.type = type;
        this.title = title;
        this.body = body;
        this.createdAt = (new Date());
    }
     @Override
    public String toString(){
           Gson gson = new Gson();
           return gson.toJson(this).toString();
    }

}
