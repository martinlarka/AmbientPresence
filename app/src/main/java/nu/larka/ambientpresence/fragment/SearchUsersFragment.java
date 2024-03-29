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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import nu.larka.ambientpresence.activity.MainActivity;
import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.adapter.OfficeSearchAdapter;
import nu.larka.ambientpresence.model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchUsersFragment extends Fragment {

    private Firebase mFireRef;
    private EditText searchText;
    private ArrayList<User> searchResults = new ArrayList<>();
    private ArrayList<User> fireBaseUsers = new ArrayList<>();
    private OfficeSearchAdapter officeSearchAdapter;
    private String uid;

    public SearchUsersFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_follow_new, container, false);

        getFireBaseUsers();

        searchText = (EditText) view.findViewById(R.id.office_search);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    for (User u : fireBaseUsers) {
                        if ((u.getUsername().toLowerCase().startsWith(s.toString().toLowerCase()) ||
                                u.getName().toLowerCase().startsWith(s.toString().toLowerCase()))
                                && !searchResults.contains(u)) {
                            officeSearchAdapter.add(u);
                        } else if ((!u.getUsername().toLowerCase().startsWith(s.toString().toLowerCase()) &&
                                !u.getName().toLowerCase().startsWith(s.toString().toLowerCase()))) {
                            officeSearchAdapter.remove(u);
                        }
                    }
                } else {
                    officeSearchAdapter.clear();
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

    private void getFireBaseUsers() {
        mFireRef.child(MainActivity.USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                for (DataSnapshot user : users) {
                    if (!user.getKey().equals(uid)) { // If not self, add user
                        String state = User.NOSTATE;
                        Iterable<DataSnapshot> otherUsers = user.child(MainActivity.OTHERUSERS).getChildren();
                        for (DataSnapshot otherUser : otherUsers) {
                            if (otherUser.getKey().equals(uid)) {
                                // Self found in other user
                                state = (String) otherUser.getValue();
                            }
                        }
                        fireBaseUsers.add(userFromDataSnapshot(user, state));
                    }
                }
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
        User user = new User(dataSnapshot.getKey(), (String)dataSnapshot.child(MainActivity.NAME).getValue());
        user.setState(state);
        user.setUsername((String) dataSnapshot.child(MainActivity.USERNAME).getValue());
        user.setImage((String) dataSnapshot.child(MainActivity.USER_IMAGE).getValue());
        return user;
    }

}
