package nu.larka.ambientpresence.model;

import android.graphics.Bitmap;

/**
 * Created by martin on 15-03-12.
 */
public class User implements Comparable<User> {

    public static final String FOLLOWING = "following";
    public static final String PENDING = "pending";
    public static final String BANNED = "banned";
    public static final String SELF = "self";
    public static final String NOSTATE = "nostate";
    public static final String ACCEPTED = "accepted";

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

    @Override
    public int compareTo(User another) {
        switch (this.getState()) {
            case PENDING:
                if (another.getState().equals(PENDING)) return this.getName().compareTo(another.getName());
                else return -1;
            case FOLLOWING:
                if (another.getState().equals(PENDING)) return 1;
                else if (another.getState().equals(BANNED)) return -1;
                else return this.getName().compareTo(another.getName());
            case BANNED:
                if (!another.getState().equals(BANNED)) return 1;
                else return this.getName().compareTo(another.getName());
        }
        return 0;
    }
}
