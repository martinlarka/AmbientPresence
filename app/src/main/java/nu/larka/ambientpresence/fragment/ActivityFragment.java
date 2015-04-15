package nu.larka.ambientpresence.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import nu.larka.ambientpresence.activity.MainActivity;
import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.adapter.UserActivityAdapter;
import nu.larka.ambientpresence.model.HueLightDevice;
import nu.larka.ambientpresence.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActivityFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayList<User> otherUserList = new ArrayList<>();
    private Firebase mFirebaseRef;
    private UserActivityAdapter userActivityAdapter;
    private String uid;
    private ListView searchResultList;
    private ArrayList<HueLightDevice> hueLightArrayList;

    public ActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        userActivityAdapter = new UserActivityAdapter(view.getContext(), otherUserList);
        searchResultList = (ListView) view.findViewById(R.id.home_activity_list);
        searchResultList.setOnItemClickListener(this);
        searchResultList.setAdapter(userActivityAdapter);
        // Inflate the layout for this fragment
        return view;
    }


    public void setOtherUsersList(ArrayList<User> otherUsersList) {
        this.otherUserList = otherUsersList;
    }

    public void setFirebaseRef(Firebase mFirebaseRef, String uid) {
        this.mFirebaseRef = mFirebaseRef;
        this.uid = uid;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Start user info fragment
        final User user = otherUserList.get(position);

        mFirebaseRef.child(MainActivity.USERS)
                    .child(uid)
                    .child(MainActivity.FOLLOWING_USERS)
                    .child(user.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfoFragment userInfoFragment = new UserInfoFragment();
                if (dataSnapshot.getValue() != null) {
                    user.setSelfState((String)dataSnapshot.getValue());
                } else {
                    user.setSelfState(User.NOSTATE);
                }
                userInfoFragment.setUser(user);
                userInfoFragment.setFirebaseRef(mFirebaseRef, uid);
                userInfoFragment.setHueDeviceArrayList(hueLightArrayList);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.info_fragment, userInfoFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void notifyUserActivityAdapter() {
        userActivityAdapter.notifyDataSetChanged();
        searchResultList.invalidateViews();
        searchResultList.setAdapter(userActivityAdapter);
    }

    public void setDeviceArrayList(ArrayList<HueLightDevice> hueLightArrayList) {
        this.hueLightArrayList = hueLightArrayList;
    }
}
