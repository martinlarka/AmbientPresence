package nu.larka.ambientpresence.fragment;

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

import com.firebase.client.Firebase;

import java.util.ArrayList;

import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.adapter.ImageAdapter;
import nu.larka.ambientpresence.model.User;


public class RemoteOfficesFragment extends Fragment implements View.OnClickListener {

    private ImageAdapter mAdapter;
    private AdapterView.OnItemClickListener itemClickListener;
    private ArrayList<User> followerList = new ArrayList<>();
    private ArrayList<User> otherUsersList = new ArrayList<>();
    private Button activityButton;
    private Firebase mFirebaseRef;
    private String uid;
    private GridView gridview;

    public RemoteOfficesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_remote_offices, container, false);
        // Inflate the layout for this fragment
        gridview = (GridView) view.findViewById(R.id.gridview);
        mAdapter = new ImageAdapter(view.getContext());
        mAdapter.setFollowers(followerList);
        gridview.setAdapter(mAdapter);

        activityButton = (Button) view.findViewById(R.id.activity_button);
        activityButton.setOnClickListener(this);

        // TODO How to populate grid with office imgs

        gridview.setOnItemClickListener(itemClickListener);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setFollowerList(ArrayList<User> followerList) {
        this.followerList = followerList;
    }
    
    public void setOtherUsersList(ArrayList<User> otherUsersList) {
        this.otherUsersList = otherUsersList;
    }

    public void notifyAdapterDataChanged() {
        mAdapter.notifyDataSetChanged();
        gridview.invalidateViews();
        gridview.setAdapter(mAdapter);
    }


    public void updateActivityButton(String str) {
        activityButton.setText(str);
        final Animation animation = new AlphaAnimation(1, 0.5f);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        activityButton.setAnimation(animation);
    }

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

    public void setFirebase(Firebase mFirebaseRef, String uid) {
        this.mFirebaseRef = mFirebaseRef;
        this.uid = uid;
    }
}
