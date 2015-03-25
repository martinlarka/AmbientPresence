package nu.larka.ambientpresence.model;

import android.graphics.Bitmap;

/**
 * Created by martin on 15-03-12.
 */
public class User {
    private String UID;
    private String name;


    private String username;
    private String state;
    private Bitmap image;

    public User() {}

    public User(String UID) {
        this.UID = UID;
    }

    public User(String UID, String name) {
        this.UID = UID;
        this.name = name;
    }

    public String getUID() {
        return UID;
    }

    public String getName() { return name; }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean hasImage() {
        return this.image != null;
    }
}
