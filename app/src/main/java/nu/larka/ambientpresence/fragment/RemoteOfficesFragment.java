package nu.larka.ambientpresence.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.tubesock.Base64;

import java.util.ArrayList;
import java.util.Collections;

import nu.larka.ambientpresence.MainActivity;
import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.adapter.FollowedUsersAdapter;
import nu.larka.ambientpresence.model.User;


public class RemoteOfficesFragment extends Fragment {

    private FollowedUsersAdapter followedUsersAdapter;
    private ActivityFragment mActivityFragment = null;
    private ArrayList<User> followerList = new ArrayList<>();
    private ArrayList<User> otherUsersList = new ArrayList<>();
    private Button activityButton;
    private Button homeButton;
    private Firebase mFirebaseRef;
    private String uid;
    private GridView followedUsersGridView;
    private int newActivities = 0;
    private HomeFragment mHomeFragment;

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
        homeButton = (Button) view.findViewById(R.id.home_button);
        homeButton.setOnClickListener(homeButtonClickListener);

        // Populate otheruserlist
        populateOtherUsersList();
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

    private void notifyFollowedUsersAdapterDataChanged() {
        followedUsersAdapter.notifyDataSetChanged();
        followedUsersGridView.invalidateViews();
        followedUsersGridView.setAdapter(followedUsersAdapter);
    }

    private void registerFollowingUsersCallback() {
        mFirebaseRef.child(MainActivity.USERS + uid + MainActivity.FOLLOWING_USERS)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        // On added - Check state and make action
                        if (!dataSnapshot.getValue().equals(User.SELF)) {
                            setFollowingUserInfo(dataSnapshot.getKey(), (String) dataSnapshot.getValue());
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        // On changed - Check new state and make action
                        for (User u : followerList) {
                            if (dataSnapshot.getKey().equals(u.getUID()) && !dataSnapshot.getValue().equals(User.SELF)) {
                                u.setState((String) dataSnapshot.getValue());
                                notifyFollowedUsersAdapterDataChanged();
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        // On remove - Check state and make actions
                        for (int i = 0; i < followerList.size(); i++) {
                            if (dataSnapshot.getKey().equals(followerList.get(i).getUID())) {
                                followerList.remove(i);
                            }
                        }
                        notifyFollowedUsersAdapterDataChanged();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
    }

    private void populateOtherUsersList() {
        mFirebaseRef.child(MainActivity.USERS + uid + MainActivity.OTHERUSERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> createdAt = dataSnapshot.getChildren();
                        for (DataSnapshot d : createdAt) {
                            Iterable<DataSnapshot> otherUsers = d.getChildren();
                            for (DataSnapshot user : otherUsers) {
                                if (!user.getValue().equals(User.SELF)) {
                                    setUserActivityInfo(user.getKey(), (String) user.getValue());
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
    }

    private void registerUserActivityCallback() {
        Query q = mFirebaseRef.child(MainActivity.USERS + uid + MainActivity.OTHERUSERS)
                .orderByKey().startAt(String.valueOf(System.currentTimeMillis()));
        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // On added - Check state and make action
                Iterable<DataSnapshot> createdAt = dataSnapshot.getChildren();
                for (DataSnapshot d : createdAt) {
                    if (!d.getValue().equals(User.SELF)) {
                        setUserActivityInfo(d.getKey(), String.valueOf(d.getValue()));
                        newActivities++;
                        updateActivityButton("" + newActivities);
                        if (mActivityFragment != null)
                            mActivityFragment.notifyUserActivityAdapter();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // Never acceced?
                Iterable<DataSnapshot> createdAt = dataSnapshot.getChildren();
                for (DataSnapshot d : createdAt) {
                    // On changed - Check new state and make action
                    for (User u : otherUsersList) {
                        if (d.getKey().equals(u.getUID())) {
                            // Check if values changed
                            u.setState((String) d.getValue());
                            newActivities++;
                            updateActivityButton("" + newActivities);
                            if (mActivityFragment != null)
                                mActivityFragment.notifyUserActivityAdapter();
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // On remove - Check state and make actions

                Iterable<DataSnapshot> createdAt = dataSnapshot.getChildren();
                for (DataSnapshot d : createdAt) {
                    for (int i = 0; i < otherUsersList.size(); i++) {
                        if (d.getKey().equals(otherUsersList.get(i).getUID())) {
                            User u = otherUsersList.remove(i);
                            if (u.getState().equals(User.FOLLOWING)) {
                                // User unfollowed
                                mFirebaseRef.child(MainActivity.USERS)
                                        .child(uid)
                                        .child(MainActivity.ACCEPTEDUSERS)
                                        .child(u.getUID()).removeValue();
                            }
                            // TODO update adapter!!
                        }
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
                user.setSelfState(userState);

                // TODO USE thumbnails for small images

                if (dataSnapshot.hasChild(MainActivity.USER_IMAGE)) {
                    user.setImage(BitmapFactory.decodeByteArray(
                            ((String) dataSnapshot.child(
                                    MainActivity.USER_IMAGE).getValue())
                                    .getBytes(), 0,
                            ((String) dataSnapshot.child(MainActivity.USER_IMAGE)
                                    .getValue()).getBytes().length));
                }

                // Get state from otheruser FIXME could be checked in otherList??
                String state = User.NOSTATE;
                Iterable<DataSnapshot> otherUsers = dataSnapshot.child(MainActivity.OTHERUSERS).getChildren();
                for (DataSnapshot otherUser : otherUsers) {
                    Iterable<DataSnapshot> userInfo = otherUser.getChildren();
                    for (DataSnapshot u : userInfo) {
                        if (u.getKey().equals(uid)) {
                            // Self found in other user
                            state = (String) u.getValue();
                        }
                    }
                }
                user.setState(state);

                followerList.add(user);
                notifyFollowedUsersAdapterDataChanged();
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
                otherUsersList.add(0, user);
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
                SearchUsersFragment searchUsersFragment = new SearchUsersFragment();
                searchUsersFragment.setFireRef(mFirebaseRef, uid);
                transaction.replace(R.id.info_fragment, searchUsersFragment);
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

            newActivities = 0;
            activityButton.setText(""+newActivities);

            // Start activity fragment!!
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            mActivityFragment = new ActivityFragment();

            Collections.sort(otherUsersList);
            mActivityFragment.setOtherUsersList(otherUsersList);
            mActivityFragment.setFirebaseRef(mFirebaseRef, uid);
            transaction.replace(R.id.info_fragment, mActivityFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    };

    private View.OnClickListener homeButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.info_fragment, mHomeFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    };

    public void setFirebase(Firebase mFirebaseRef, String uid) {
        this.mFirebaseRef = mFirebaseRef;
        this.uid = uid;
    }

    public void setHomeFragment(HomeFragment mHomeFragment) {
        this.mHomeFragment = mHomeFragment;
    }
}
