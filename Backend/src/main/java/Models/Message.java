package Models;

import com.arangodb.velocypack.annotations.SerializedName;

import java.io.Serializable;

public class Message implements Serializable {
    private String method;
    private String command;
    private int page;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    private int limit;


    @SerializedName("userData")
    private UserData userData;
    private BanData banData;


    public BanData getBanData() {
        return banData;
    }

    public void setBanData(BanData banData) {
        this.banData = banData;
    }

    private String userID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    private String bio;

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
    

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
