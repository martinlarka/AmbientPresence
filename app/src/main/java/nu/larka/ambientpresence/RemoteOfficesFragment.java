package nu.larka.ambientpresence;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import nu.larka.ambientpresence.model.User;


public class RemoteOfficesFragment extends Fragment {

    private ImageAdapter mAdapter;
    private AdapterView.OnItemClickListener itemClickListener;
    private ArrayList<User> followerList;

    public RemoteOfficesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_remote_offices, container, false);
        // Inflate the layout for this fragment
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        mAdapter = new ImageAdapter(view.getContext());
        mAdapter.setFollowers(followerList);
        gridview.setAdapter(mAdapter);

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

    public void notifyAdapterDataChanged() {
        mAdapter.notifyDataSetChanged();
    }
}
