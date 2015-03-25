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
        if (state.equals(MainActivity.FOLLOWING) || state.equals(MainActivity.PENDING) || state.equals(MainActivity.BANNED)) {
            mFirebaseRef.child(MainActivity.USERS + user.getUID() + MainActivity.OTHERUSERS)
                    .child(uid).removeValue();
            mFirebaseRef.child(MainActivity.USERS + uid + MainActivity.FOLLOWING_USERS)
                    .child(user.getUID()).removeValue();
            user.setState(MainActivity.NOSTATE);
            v.setBackground(v.getResources().getDrawable(R.drawable.follow));
        } else { // Follow user
            mFirebaseRef.child(MainActivity.USERS + user.getUID() + MainActivity.OTHERUSERS)
                    .child(uid).child(MainActivity.STATE).setValue(MainActivity.PENDING);
            mFirebaseRef.child(MainActivity.USERS + user.getUID() + MainActivity.OTHERUSERS)
                    .child(uid).child(MainActivity.CREATEDAT).setValue(System.currentTimeMillis());
            mFirebaseRef.child(MainActivity.USERS + uid + MainActivity.FOLLOWING_USERS)
                    .child(user.getUID()).setValue(MainActivity.PENDING);
            user.setState(MainActivity.PENDING);
            v.setBackground(v.getResources().getDrawable(R.drawable.pending));
        }
    }
}
