package nu.larka.ambientpresence.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import nu.larka.ambientpresence.MainActivity;
import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.adapter.OfficeSearchAdapter;
import nu.larka.ambientpresence.model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class FollowNewFragment extends Fragment {

    private Firebase mFireRef;
    private EditText searchText;
    private ArrayList<User> searchResults = new ArrayList<>();
    private ArrayList<User> followedUsers = new ArrayList<>();
    private ArrayList<User> fireBaseUsers = new ArrayList<>();
    private OfficeSearchAdapter officeSearchAdapter;
    private String uid;

    public FollowNewFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_follow_new, container, false);

        getFireBaseUsers();
        getFollowedUsers();

        searchText = (EditText) view.findViewById(R.id.office_search);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 3) {
                    for (User u : fireBaseUsers) {
                        if (u.getUID().startsWith(s.toString()) && !searchResults.contains(u)) {
                            officeSearchAdapter.add(u);
                        } else if (!u.getUID().startsWith(s.toString()) && searchResults.contains(u)){
                            officeSearchAdapter.remove(u);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        officeSearchAdapter = new OfficeSearchAdapter(view.getContext(), searchResults, mFireRef, uid);
        ListView searchResultList = (ListView) view.findViewById(R.id.search_result_list);
        searchResultList.setAdapter(officeSearchAdapter);

        return view;
    }

    private void getFollowedUsers() {
        mFireRef.child(MainActivity.USERS)
                .child(uid)
                .child(MainActivity.FOLLOWERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getKey().equals(uid)) {
                    followedUsers.add(userFromDataSnapshot(dataSnapshot, MainActivity.FOLLOWER));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getFireBaseUsers() {
        mFireRef.child(MainActivity.USERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // If user exist in other user's list
                if (dataSnapshot.child(MainActivity.OTHERUSERS).hasChild(uid)) {
                    // Get state
                    String state = (String) dataSnapshot.child(MainActivity.OTHERUSERS + uid).getValue();
                    switch (state) {
                        case MainActivity.FOLLOWING:
                            fireBaseUsers.add(userFromDataSnapshot(dataSnapshot, MainActivity.FOLLOWING));
                            break;
                        case MainActivity.PENDING:
                            fireBaseUsers.add(userFromDataSnapshot(dataSnapshot, MainActivity.PENDING));
                            break;
                        case MainActivity.NOSTATE:
                            fireBaseUsers.add(userFromDataSnapshot(dataSnapshot, MainActivity.NOSTATE));
                    }
                } else {
                    fireBaseUsers.add(userFromDataSnapshot(dataSnapshot, MainActivity.NOSTATE));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void setFireRef(Firebase fireRef, String uid) {
        this.mFireRef = fireRef;
        this.uid = uid;
    }

    private User userFromDataSnapshot(DataSnapshot dataSnapshot, String state) {
        User user = new User(dataSnapshot.getKey(), (String)dataSnapshot.child(MainActivity.USERNAME).getValue());
        user.setState(state);
        return user;
    }

}
