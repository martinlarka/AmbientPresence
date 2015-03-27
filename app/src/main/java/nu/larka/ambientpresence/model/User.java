package nu.larka.ambientpresence.model;

import android.graphics.Bitmap;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import nu.larka.ambientpresence.MainActivity;

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

    public String getSelfState() {
        return selfState;
    }

    public void setSelfState(String selfState) {
        this.selfState = selfState;
    }

    private String selfState;
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

    public void sendFollowRequest(Firebase firebase, String selfUID) {
        firebase.child(MainActivity.USERS).child(selfUID).child(MainActivity.FOLLOWING_USERS).child(this.UID).setValue(PENDING);
        firebase.child(MainActivity.USERS).child(this.UID).child(MainActivity.OTHERUSERS).child(selfUID).setValue(PENDING);
        this.state = PENDING;
    }

    public void acceptFollowRequest(Firebase firebase, String selfUID) {
        firebase.child(MainActivity.USERS).child(this.UID).child(MainActivity.FOLLOWING_USERS).child(selfUID).setValue(FOLLOWING);
        firebase.child(MainActivity.USERS).child(selfUID).child(MainActivity.ACCEPTEDUSERS).child(this.UID).setValue(ACCEPTED);
        firebase.child(MainActivity.USERS).child(selfUID).child(MainActivity.OTHERUSERS).child(this.UID).setValue(FOLLOWING);
        // TODO remove old value in otherusers
        this.state = FOLLOWING;
    }


    public void banUser(Firebase firebase, String selfUID) {
        firebase.child(MainActivity.USERS).child(this.UID).child(MainActivity.FOLLOWING_USERS).child(this.UID).setValue(BANNED);
        firebase.child(MainActivity.USERS).child(selfUID).child(MainActivity.ACCEPTEDUSERS).child(this.UID).removeValue();
        firebase.child(MainActivity.USERS).child(selfUID).child(MainActivity.OTHERUSERS).child(this.UID).setValue(BANNED);
        // TODO remove old value in otherusers
        this.state = BANNED;
    }

    public void unfollowUser(Firebase firebase, String selfUID) {
        firebase.child(MainActivity.USERS).child(selfUID).child(MainActivity.FOLLOWING_USERS).child(this.UID).removeValue();
        // TODO remove old value in otherusers
        this.state = NOSTATE;
    }

    public static void registerUser(Firebase userRef, AuthData authData) {
        userRef.child(MainActivity.USERNAME).setValue(userNameify((String) authData.getProviderData().get("displayName")));
        userRef.child(MainActivity.NAME).setValue(authData.getProviderData().get("displayName"));
        userRef.child(MainActivity.FOLLOWING_USERS).child(authData.getUid()).setValue(User.SELF);
        userRef.child(MainActivity.ACCEPTEDUSERS).child(authData.getUid()).setValue(User.SELF);
        userRef.child(MainActivity.OTHERUSERS).child(authData.getUid()).setValue(User.SELF);
    }

    // TODO Let users give own usernames, or fix email to username
    private static String userNameify(String username) {
        return username.toLowerCase().replace(" ", "").replace("å", "a").replace("ä", "a").replace("ö", "o");
    }

    public void unBanUser(Firebase mFirebaseRef, String uid) {
        // TODO implement mehtod
    }
}
