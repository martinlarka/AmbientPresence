package nu.larka.ambientpresence.model;

/**
 * Created by martin on 15-03-12.
 */
public class User {
    private String UID;
    private String name;
    private String state;

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
}
