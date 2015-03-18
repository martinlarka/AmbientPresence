package nu.larka.ambientpresence;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nu.larka.ambientpresence.model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class FollowNewFragment extends Fragment {

    private Firebase mFireRef;
    private EditText searchText;
    private ArrayList<User> searchResults = new ArrayList<>();
    private ArrayList<User> fireBaseUsers = new ArrayList<>();
    private ArrayList<String> bannedBy = new ArrayList<>();
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
        getBannedByUsers();

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


        officeSearchAdapter = new OfficeSearchAdapter(view.getContext(), searchResults);
        ListView searchResultList = (ListView) view.findViewById(R.id.search_result_list);
        searchResultList.setAdapter(officeSearchAdapter);

        return view;
    }

    private void getBannedByUsers() {
        mFireRef.child(MainActivity.USERS+uid+MainActivity.BANNEDBYUSERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.getValue().equals("")) {
                    bannedBy.add((String) dataSnapshot.getValue());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                bannedBy.remove(dataSnapshot.getValue());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
                if (!bannedBy.contains(dataSnapshot.getKey()) && !dataSnapshot.getKey().equals(uid))
                    fireBaseUsers.add(userFromDataSnapshot(dataSnapshot));
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

    private User userFromDataSnapshot(DataSnapshot dataSnapshot) {
        return new User(dataSnapshot.getKey(), (String)dataSnapshot.child(MainActivity.USERNAME).getValue());
    }

}
