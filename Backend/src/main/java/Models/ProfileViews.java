package Models;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "profileViews")

public class ProfileViews {
    private String _key;
    private List<Viewer> viewers;
    private String viewedUser;

    public ProfileViews(String _key, List<Viewer> viewers, String viewedUser) {
        this._key = _key;
        this.viewers = viewers;
        this.viewedUser = viewedUser;
    }
    //private String createdAt;


    public String get_key() { return _key; }

    public void set_key(String _key) { this._key = _key; }

    public String getViewedUser() {
        return viewedUser;
    }

    public void setViewedUser(String viewedUser) { this.viewedUser = viewedUser; }

    public List<Viewer> getViewers() {
        return viewers;
    }

    public void setViewers(List<Viewer> viewers) {
        this.viewers = viewers;
    }

//    public String getCreatedAt() { return createdAt; }
//
//    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
