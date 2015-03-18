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
        if (state.equals(MainActivity.FOLLOWING) || state.equals(MainActivity.PENDING)) {
            mFirebaseRef.child(MainActivity.USERS + user.getUID() + MainActivity.OTHERUSERS)
                    .child(uid).setValue(MainActivity.NOSTATE);
            user.setState(MainActivity.NOSTATE);
            v.setBackground(v.getResources().getDrawable(R.drawable.follow));
        } else {
            mFirebaseRef.child(MainActivity.USERS + user.getUID() + MainActivity.OTHERUSERS)
                    .child(uid).setValue(MainActivity.PENDING);
            user.setState(MainActivity.PENDING);
            v.setBackground(v.getResources().getDrawable(R.drawable.pending));
        }
    }
}