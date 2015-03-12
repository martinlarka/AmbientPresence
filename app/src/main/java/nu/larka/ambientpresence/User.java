package nu.larka.ambientpresence;

/**
 * Created by martin on 15-03-12.
 */
public class User {
    private String UID;
    private String[] followerRequests;
    private String[] bannedUsers;

    public User() {}

    public User(String UID) {
        this.UID = UID;
    }

    public String getUID() {
        return UID;
    }

    public String[] getFollowerRequests() {
        return followerRequests;
    }

    public String[] getBannedUsers() {
        return bannedUsers;
    }
}
