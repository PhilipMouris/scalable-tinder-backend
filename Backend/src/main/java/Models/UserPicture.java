package Models;

import java.sql.Timestamp;
import java.util.Date;

public class UserPicture {
    private String url;
    private boolean isMain;
    private String uploadedAt;

    public UserPicture(){
        
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(String uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public UserPicture(String url, boolean isMain, String uploadedAt) {
        this.url = url;
        this.isMain = isMain;
        this.uploadedAt =  new Timestamp(new Date().getTime()).toString();
    }

   

}
