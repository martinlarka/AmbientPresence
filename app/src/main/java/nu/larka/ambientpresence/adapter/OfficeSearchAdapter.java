package nu.larka.ambientpresence.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;

import nu.larka.ambientpresence.MainActivity;
import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.listener.SearchItemListener;
import nu.larka.ambientpresence.model.User;

/**
 * Created by martin on 15-03-18.
 */
public class OfficeSearchAdapter extends ArrayAdapter<User> {
    private Firebase mFireref;
    private String uid;

    public OfficeSearchAdapter(Context context, ArrayList<User> users, Firebase fireRef, String uid) {
        super(context, 0, users);
        this.mFireref = fireRef;
        this.uid = uid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_result_item, parent, false);
        }

        ImageView officeImage = (ImageView) convertView.findViewById(R.id.office_image);
        //TODO Add image to office

        // Lookup view for data population
        TextView officeName = (TextView) convertView.findViewById(R.id.offce_name);
        TextView officeUID = (TextView) convertView.findViewById(R.id.office_uid);
        // Populate the data into the template view using the data object
        officeName.setText(user.getName());
        officeUID.setText(user.getUID());

        Button followButton = (Button) convertView.findViewById(R.id.follow_button);
        followButton.setBackground(getUserStateImage(user.getState()));
        followButton.setOnClickListener(new SearchItemListener(mFireref, user, uid));

        // Return the completed view to render on screen
        return convertView;
    }

    private Drawable getUserStateImage(String state) {
        switch (state) {
            case MainActivity.FOLLOWING:
                return getContext().getResources().getDrawable(R.drawable.following);
            case MainActivity.PENDING:
                return getContext().getResources().getDrawable(R.drawable.pending);
            default:
                return getContext().getResources().getDrawable(R.drawable.follow);
        }
    }
}
