package nu.larka.ambientpresence.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.philips.lighting.model.PHLight;

import java.util.ArrayList;
import java.util.List;

import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.adapter.UserInfoDeviceAdapter;
import nu.larka.ambientpresence.model.Device;
import nu.larka.ambientpresence.model.HueBridgeDevice;
import nu.larka.ambientpresence.model.HueLightDevice;
import nu.larka.ambientpresence.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfoFragment extends Fragment {


    private User user;
    private Firebase mFirebaseRef;
    private String uid;
    private Button userStateButton;
    private Button selfStateButton;
    private TextView userStatusTextView;
    private ListView deviceListView;
    private UserInfoDeviceAdapter deviceAdapter;
    private ArrayList<HueLightDevice> hueLightDeviceArrayList;


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
        userStatusTextView = (TextView) view.findViewById(R.id.user_status);
        userStateButton = (Button) view.findViewById(R.id.user_info_button);
        selfStateButton = (Button) view.findViewById(R.id.user_ban_button);


        userName.setText(user.getUsername());
        userFullName.setText(user.getName());
        userStatusTextView.setText(getUserStatusText());

        if (user.hasImage()) {
            userImage.setImageBitmap(user.getImage());
        } else {
            userImage.setImageResource(R.drawable.home500);
        }

        deviceListView = (ListView) view.findViewById(R.id.device_list_view);
        if (user.getSelfState().equals(User.FOLLOWING) && user.getEnvironmentNames() != null) {
            // FIXME When adding more than 3 users, buttons at bottom disapears
            deviceAdapter = new UserInfoDeviceAdapter(view.getContext(), hueLightDeviceArrayList, user);
            deviceListView.setAdapter(deviceAdapter);
        } else {
            deviceListView.setVisibility(View.INVISIBLE);
        }

        setStateOfButtons();

        // Inflate the layout for this fragment
        return view;
    }

    private String getUserStatusText() {
        switch (user.getState()) {
            case User.PENDING:
                return " - " + getString(R.string.user_is_pending);
            case User.FOLLOWING:
                return " - " + getString(R.string.user_is_following);
            case User.BANNED:
                return " - " + getString(R.string.user_is_banned);
            default:
                return "";
        }
    }

    private void setStateOfButtons() {
        switch (user.getSelfState()) {
            case User.NOSTATE:
                switch (user.getState()) {
                    case User.NOSTATE:
                    case User.FOLLOWING:
                        userStateButton.setOnClickListener(sendFollowRequestListener);
                        userStateButton.setText(R.string.send_follow_request);
                        break;
                    case User.PENDING:
                        userStateButton.setOnClickListener(acceptFollowRequestListener);
                        userStateButton.setText(R.string.accept_follow_request);
                        break;
                    case User.BANNED:
                        userStateButton.setEnabled(false);
                        userStateButton.setText(R.string.user_is_banned);
                        break;
                }
                selfStateButton.setOnClickListener(banUserListener);
                selfStateButton.setText(R.string.ban_user);
                break;
            case User.PENDING:
                switch (user.getState()) {
                    case User.NOSTATE:
                        selfStateButton.setOnClickListener(unFollowUserListener);
                        selfStateButton.setText(R.string.unfollow_user);
                        userStateButton.setEnabled(false);
                        userStateButton.setText(R.string.waiting_for_follow_accept);
                        break;
                    case User.FOLLOWING:
                    case User.BANNED:
                        userStateButton.setEnabled(false);
                        userStateButton.setText(R.string.waiting_for_follow_accept);
                        selfStateButton.setOnClickListener(banUserListener);
                        selfStateButton.setText(R.string.ban_user);
                        break;
                    case User.PENDING:
                        userStateButton.setOnClickListener(acceptFollowRequestListener);
                        userStateButton.setText(R.string.accept_follow_request);
                        selfStateButton.setOnClickListener(unFollowUserListener);
                        selfStateButton.setText(R.string.unfollow_user);
                        break;
                }
                break;
            case User.FOLLOWING:
                switch (user.getState()) {
                    case User.NOSTATE:
                    case User.FOLLOWING:
                        userStateButton.setEnabled(false);
                        userStateButton.setText(R.string.following_user);
                        selfStateButton.setOnClickListener(unFollowUserListener);
                        selfStateButton.setText(R.string.unfollow_user);
                        break;
                    case User.BANNED:
                        userStateButton.setEnabled(false);
                        userStateButton.setText(R.string.following_user);
                        selfStateButton.setOnClickListener(banUserListener);
                        selfStateButton.setText(R.string.ban_user);
                        break;
                    case User.PENDING:
                        userStateButton.setOnClickListener(acceptFollowRequestListener);
                        userStateButton.setText(R.string.accept_follow_request);
                        selfStateButton.setOnClickListener(unFollowUserListener);
                        selfStateButton.setText(R.string.unfollow_user);
                        break;
                }
                break;
            case User.BANNED:
                userStateButton.setEnabled(false);
                userStateButton.setText(R.string.user_is_banned);
                selfStateButton.setOnClickListener(unBanUserListener);
                selfStateButton.setText(R.string.unbann_user);
                break;
        }
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
            user.sendFollowRequest(mFirebaseRef, uid);
            setStateOfButtons();
            userStatusTextView.setText(getUserStatusText());
        }
    };

    private View.OnClickListener acceptFollowRequestListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            user.acceptFollowRequest(mFirebaseRef, uid);
            setStateOfButtons();
            userStatusTextView.setText(getUserStatusText());
        }
    };

    private View.OnClickListener banUserListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            user.banUser(mFirebaseRef, uid);
            setStateOfButtons();
            userStatusTextView.setText(getUserStatusText());
        }
    };

    private View.OnClickListener unBanUserListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            user.unBanUser(mFirebaseRef, uid);
            setStateOfButtons();
            userStatusTextView.setText(getUserStatusText());
        }
    };

    private View.OnClickListener unFollowUserListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            user.unfollowUser(mFirebaseRef, uid);
            setStateOfButtons();
            userStatusTextView.setText(getUserStatusText());
        }
    };

    public void setHueDeviceArrayList(ArrayList<HueLightDevice> hueLightDeviceArrayList) {
        this.hueLightDeviceArrayList = hueLightDeviceArrayList;
    }
}
