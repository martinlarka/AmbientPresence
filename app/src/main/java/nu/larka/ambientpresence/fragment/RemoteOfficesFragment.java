package nu.larka.ambientpresence.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.tubesock.Base64;

import java.util.ArrayList;

import nu.larka.ambientpresence.MainActivity;
import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.adapter.FollowedUsersAdapter;
import nu.larka.ambientpresence.model.User;


public class RemoteOfficesFragment extends Fragment {

    private FollowedUsersAdapter followedUsersAdapter;
    private ArrayList<User> followerList = new ArrayList<>();
    private ArrayList<User> otherUsersList = new ArrayList<>();
    private Button activityButton;
    private Firebase mFirebaseRef;
    private String uid;
    private GridView followedUsersGridView;

    public RemoteOfficesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_remote_offices, container, false);
        // Inflate the layout for this fragment
        followedUsersGridView = (GridView) view.findViewById(R.id.gridview);
        followedUsersAdapter = new FollowedUsersAdapter(view.getContext());
        followedUsersAdapter.setFollowers(followerList);
        followedUsersGridView.setAdapter(followedUsersAdapter);

        activityButton = (Button) view.findViewById(R.id.activity_button);
        activityButton.setOnClickListener(activityButtonClickListener);
        registerUserActivityCallback();

        followedUsersGridView.setOnItemClickListener(itemClickListener);
        registerFollowingUsersCallback();

        return view;
    }

    public void updateActivityButton(String str) {
        activityButton.setText(str);
        final Animation animation = new AlphaAnimation(1, 0.5f);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        activityButton.setAnimation(animation);
    }

    private void notifyAdapterDataChanged() {
        followedUsersAdapter.notifyDataSetChanged();
        followedUsersGridView.invalidateViews();
        followedUsersGridView.setAdapter(followedUsersAdapter);
    }

    private void registerFollowingUsersCallback() {
            mFirebaseRef.child(MainActivity.USERS + uid + MainActivity.FOLLOWING_USERS).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // On added - Check state and make action
                    if (!dataSnapshot.getValue().equals(MainActivity.SELF)) {
                        setFollowingUserInfo(dataSnapshot.getKey(), (String) dataSnapshot.getValue());
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    // On changed - Check new state and make action
                    for (User u : followerList) {
                        if (dataSnapshot.getKey().equals(u.getUID()) && !dataSnapshot.getValue().equals(MainActivity.SELF)) {
                            u.setState((String) dataSnapshot.getValue());
                        }
                    }
                    notifyAdapterDataChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    // On remove - Check state and make actions
                    for (int i = 0; i < followerList.size(); i++) {
                        if (dataSnapshot.getKey().equals(followerList.get(i).getUID())) {
                            followerList.remove(i);
                        }
                    }
                    notifyAdapterDataChanged();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
    }

    // TODO REFREACTOR, Only new events trigger update activity button
    private void registerUserActivityCallback() {
            mFirebaseRef.child(MainActivity.USERS + uid + MainActivity.OTHERUSERS).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // On added - Check state and make action
                    if (!dataSnapshot.getValue().equals(MainActivity.SELF)) {
                        setUserActivityInfo(dataSnapshot.getKey(), (String) dataSnapshot.getValue());
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    // On changed - Check new state and make action
                    /*for (User u : otherUsersList) {
                        if (dataSnapshot.getKey().equals(u.getUID())) {
                            u.setState((String) dataSnapshot.getValue());
                        }
                    }
                    updateActivityButton("BARFOO");*/
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    // On remove - Check state and make actions
                    for (int i = 0; i < otherUsersList.size(); i++) {
                        if (dataSnapshot.getKey().equals(otherUsersList.get(i).getUID())) {
                            otherUsersList.remove(i);
                        }
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
    }

    private void setFollowingUserInfo(final String userUID, final String userState) {
        mFirebaseRef.child(MainActivity.USERS).child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = new User(userUID, (String) dataSnapshot.child(MainActivity.NAME).getValue());
                user.setUsername((String) dataSnapshot.child(MainActivity.USERNAME).getValue());
                user.setState(userState);

                // TODO USE thumbnails for small images
                String str = (String) dataSnapshot.child(MainActivity.USER_IMAGE).getValue();
                if (str != null) {
                    byte[] imageAsBytes = Base64.decode(str.getBytes());
                    user.setImage(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                }
                followerList.add(user);
                notifyAdapterDataChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void setUserActivityInfo(final String userUID, final String userState) {
        mFirebaseRef.child(MainActivity.USERS).child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = new User(userUID, (String) dataSnapshot.child(MainActivity.NAME).getValue());
                user.setUsername((String) dataSnapshot.child(MainActivity.USERNAME).getValue());
                user.setState(userState);

                // TODO USE thumbnails for small images
                String str = (String) dataSnapshot.child(MainActivity.USER_IMAGE).getValue();
                if (str != null) {
                    byte[] imageAsBytes = Base64.decode(str.getBytes());
                    user.setImage(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                }
                otherUsersList.add(user);
                updateActivityButton("FOOBAR");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
// TODO MAX TREE NUMBERS OF FOLLOWING
            // Follow new clicked
            if (position == followerList.size()) {
                // Start follow new fragment
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                FollowNewFragment followFragment = new FollowNewFragment();
                followFragment.setFireRef(mFirebaseRef, uid);
                transaction.replace(R.id.info_fragment, followFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            } else { // Load setup of pressed office
                // Start user info fragment
                UserInfoFragment userInfoFragment = new UserInfoFragment();
                userInfoFragment.setUser(followerList.get(position));
                userInfoFragment.setFirebaseRef(mFirebaseRef, uid);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.info_fragment, userInfoFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        }
    };

    private View.OnClickListener activityButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Start activity fragment!!
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            ActivityFragment mActivityFragment = new ActivityFragment();
            // TODO Send correct user list
            mActivityFragment.setOtherUsersList(otherUsersList);
            mActivityFragment.setFirebaseRef(mFirebaseRef, uid);
            transaction.replace(R.id.info_fragment, mActivityFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    };

    public void setFirebase(Firebase mFirebaseRef, String uid) {
        this.mFirebaseRef = mFirebaseRef;
        this.uid = uid;
    }
}
