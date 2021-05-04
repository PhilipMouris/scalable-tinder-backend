package Models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class UserData {
    @Id
    private String id;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String _key;
    private String bio;
    private String birthDate;
    private UserLocation location;
    private UserLinks links;
    private List<UserPicture> profilePictures;
    private List<String> videos;
    private List<UserInterest> interests;
    private UserPreference preferences;
    private String[] firebaseTokens;

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String get_key() {
        return _key;
    }

    public void set_key(String _key) {
        this._key = _key;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String[] getFirebaseTokens(){
        return firebaseTokens;
    }


    public UserLocation getLocation() {
        return location;
    }

    public void setLocation(UserLocation location) {
        this.location = location;
    }

    public UserLinks getLinks() {
        return links;
    }

    public void setLinks(UserLinks links) {
        this.links = links;
    }

    public List<UserPicture> getProfilePictures() {
        return profilePictures;
    }

    public void setProfilePictures(List<UserPicture> profilePictures) {
        this.profilePictures = profilePictures;
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }

    public List<UserInterest> getInterests() {
        return interests;
    }

    public void setInterests(List<UserInterest> interests) {
        this.interests = interests;
    }

    public UserPreference getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreference preferences) {
        this.preferences = preferences;
    }
}
