package nu.larka.ambientpresence.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

        TextView userName = (TextView) view.findViewById(R.id.user_info_name);
        Button acceptButton = (Button) view.findViewById(R.id.user_info_accept_button);
        Button banUserButton = (Button) view.findViewById(R.id.user_info_ban_button);

        userName.setText(user.getName());
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO CHANGE TO UNFOLLOW after pressed
                // Accept follow request
                mFirebaseRef.child(MainActivity.USERS).child(user.getUID()).child(MainActivity.FOLLOWING_USERS).child(uid).setValue(User.FOLLOWING);
                mFirebaseRef.child(MainActivity.USERS).child(uid).child(MainActivity.ACCEPTEDUSERS).child(user.getUID()).setValue(User.ACCEPTED);
                mFirebaseRef.child(MainActivity.USERS).child(uid).child(MainActivity.OTHERUSERS).child(user.getUID()).setValue(User.FOLLOWING);
                user.setState(User.FOLLOWING);
            }
        });

        banUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseRef.child(MainActivity.USERS).child(uid).child(MainActivity.OTHERUSERS).child(user.getUID()).setValue(User.BANNED);
                mFirebaseRef.child(MainActivity.USERS).child(user.getUID()).child(User.FOLLOWING).child(uid).setValue(User.BANNED);
                user.setState(User.BANNED);
            }
        });

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
}
