package nu.larka.ambientpresence;

/**
 * Created by martin on 15-03-12.
 */
public class User {
    private String email;
    private String[] followerRequests;
    private String[] bannedUsers;

    public User() {}

    public User(String email) {
        this.email = email;
    }

    public String getUID() {
        return email;
    }

    public String[] getFollowerRequests() {
        return followerRequests;
    }

    public String[] getBannedUsers() {
        return bannedUsers;
    }
}
