package nu.larka.ambientpresence.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.awt.font.TextAttribute;
import java.util.jar.Manifest;

import nu.larka.ambientpresence.MainActivity;
import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfoFragment extends Fragment {


    private User user;
    private Firebase mFirebaseRef;
    private String uid;

    public UserInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        TextView userName = (TextView) view.findViewById(R.id.user_username);
        TextView userFullName = (TextView) view.findViewById(R.id.user_fullname);
        ImageView userImage = (ImageView) view.findViewById(R.id.user_image_view);
        Button userStateButton = (Button) view.findViewById(R.id.user_info_button);
        Button selfStateButton = (Button) view.findViewById(R.id.user_ban_button);

        userName.setText(user.getUsername());
        userFullName.setText(user.getName());

        if (user.hasImage()) {
            userImage.setImageBitmap(user.getImage());
        } else {
            userImage.setImageResource(R.drawable.home500);
        }

        switch (user.getState()) {
            case User.NOSTATE:
            case User.FOLLOWING:
                // Send request to follow
                userStateButton.setOnClickListener(sendFollowRequestListener);
                userStateButton.setText(R.string.send_follow_request);
                break;
            case User.PENDING:
                // Accept follow request
                userStateButton.setOnClickListener(acceptFollowRequestListener);
                userStateButton.setText(R.string.accept_follow_request);
                break;
            case User.BANNED:
                // Hidden
                userStateButton.setEnabled(false);
                userStateButton.setText(R.string.user_is_banned);
                break;
        }

        if (user.getSelfState().equals(User.FOLLOWING) || user.getSelfState().equals(User.PENDING)) {
            selfStateButton.setOnClickListener(unFollowUserListener);
            selfStateButton.setText(R.string.unfollow_user);
        } else {
            selfStateButton.setOnClickListener(banUserListener);
            selfStateButton.setText(R.string.ban_user);
        }
        // Inflate the layout for this fragment
        return view;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public void setFirebaseRef(Firebase mFirebaseRef, String uid) {
        this.mFirebaseRef = mFirebaseRef;
        this.uid = uid;
    }


    private View.OnClickListener sendFollowRequestListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFirebaseRef.child(MainActivity.USERS).child(uid).child(MainActivity.FOLLOWING_USERS).child(user.getUID()).setValue(User.PENDING);
            mFirebaseRef.child(MainActivity.USERS).child(user.getUID()).child(MainActivity.OTHERUSERS).child(uid).child(MainActivity.STATE).setValue(User.PENDING);
            mFirebaseRef.child(MainActivity.USERS).child(user.getUID()).child(MainActivity.OTHERUSERS).child(uid).child(MainActivity.CREATEDAT).setValue(System.currentTimeMillis());
            user.setState(User.PENDING);
        }
    };

    private View.OnClickListener acceptFollowRequestListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFirebaseRef.child(MainActivity.USERS).child(user.getUID()).child(MainActivity.FOLLOWING_USERS).child(uid).setValue(User.FOLLOWING);
            mFirebaseRef.child(MainActivity.USERS).child(uid).child(MainActivity.ACCEPTEDUSERS).child(user.getUID()).setValue(User.ACCEPTED);
            mFirebaseRef.child(MainActivity.USERS).child(uid).child(MainActivity.OTHERUSERS).child(user.getUID()).child(MainActivity.STATE).setValue(User.FOLLOWING);
            mFirebaseRef.child(MainActivity.USERS).child(uid).child(MainActivity.OTHERUSERS).child(user.getUID()).child(MainActivity.CREATEDAT).setValue(System.currentTimeMillis());
            user.setState(User.FOLLOWING);
        }
    };

    private View.OnClickListener banUserListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFirebaseRef.child(MainActivity.USERS).child(user.getUID()).child(MainActivity.FOLLOWING_USERS).child(user.getUID()).setValue(User.BANNED);
            mFirebaseRef.child(MainActivity.USERS).child(uid).child(MainActivity.ACCEPTEDUSERS).child(user.getUID()).removeValue();
            mFirebaseRef.child(MainActivity.USERS).child(user.getUID()).child(MainActivity.OTHERUSERS).child(uid).child(MainActivity.STATE).setValue(User.BANNED);
            mFirebaseRef.child(MainActivity.USERS).child(user.getUID()).child(MainActivity.OTHERUSERS).child(uid).child(MainActivity.CREATEDAT).setValue(System.currentTimeMillis());
            user.setState(User.BANNED);
        }
    };

    private View.OnClickListener unFollowUserListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFirebaseRef.child(MainActivity.USERS).child(uid).child(MainActivity.FOLLOWING_USERS).child(user.getUID()).removeValue();
            mFirebaseRef.child(MainActivity.USERS).child(user.getUID()).child(MainActivity.OTHERUSERS).child(uid).removeValue();
            user.setState(User.NOSTATE);
        }
    };
}
