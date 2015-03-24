package nu.larka.ambientpresence.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nu.larka.ambientpresence.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private String displayName;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        TextView titleView = (TextView) v.findViewById(R.id.home_name);
        titleView.setText(displayName);

        // Inflate the layout for this fragment
        return v;
    }


    public void setHomeName(String displayName) {
        this.displayName = displayName;
    }
}
