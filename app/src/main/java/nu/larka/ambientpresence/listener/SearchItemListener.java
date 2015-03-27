package nu.larka.ambientpresence.listener;

import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;

import nu.larka.ambientpresence.MainActivity;
import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.model.User;

/**
 * Created by martin on 15-03-18.
 */
public class SearchItemListener implements View.OnClickListener {

    private Firebase mFirebaseRef;
    private User user;
    private String uid;

    public SearchItemListener(Firebase firebaseRef, User user, String uid) {
        this.mFirebaseRef = firebaseRef;
        this.user = user;
        this.uid = uid;
    }

    @Override
    public void onClick(View v) {
        String state = user.getState();

            // Unfollow or remove pending
        if (state.equals(User.FOLLOWING) || state.equals(User.PENDING) || state.equals(User.BANNED)) {
            // TODO Remove user from "other users"
            mFirebaseRef.child(MainActivity.USERS + user.getUID() + MainActivity.OTHERUSERS)
                    .child(uid).removeValue();
            mFirebaseRef.child(MainActivity.USERS + uid + MainActivity.FOLLOWING_USERS)
                    .child(user.getUID()).removeValue();
            user.setState(User.NOSTATE);
            v.setBackground(v.getResources().getDrawable(R.drawable.follow));
        } else { // Follow user
            mFirebaseRef.child(MainActivity.USERS + user.getUID() + MainActivity.OTHERUSERS)
                    .child(String.valueOf(System.currentTimeMillis()))
                    .child(uid).setValue(User.PENDING);
            mFirebaseRef.child(MainActivity.USERS + uid + MainActivity.FOLLOWING_USERS)
                    .child(user.getUID()).setValue(User.PENDING);
            user.setState(User.PENDING);
            v.setBackground(v.getResources().getDrawable(R.drawable.pending));
        }
    }
}
