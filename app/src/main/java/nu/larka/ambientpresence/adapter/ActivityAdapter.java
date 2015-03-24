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
public class ActivityAdapter extends ArrayAdapter<User> {

    public ActivityAdapter(Context context, ArrayList<User> otherUsers) {
        super(context, 0, otherUsers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_list_item, parent, false);
        }

        ImageView officeImage = (ImageView) convertView.findViewById(R.id.office_image);
        //TODO Add image to office

        // Lookup view for data population
        TextView officeName = (TextView) convertView.findViewById(R.id.offce_name);
        TextView officeUID = (TextView) convertView.findViewById(R.id.office_uid);
        // Populate the data into the template view using the data object
        officeName.setText(user.getName());
        officeUID.setText(user.getUID());

        // Return the completed view to render on screen
        return convertView;
    }

    private Drawable getUserStateImage(String state) {
        switch (state) {
            case MainActivity.BANNED:
                Drawable d = getContext().getResources().getDrawable(R.drawable.follow);
                d.setAlpha(100);
                return d;
            case MainActivity.FOLLOWING:
                return getContext().getResources().getDrawable(R.drawable.following);
            case MainActivity.PENDING:
                return getContext().getResources().getDrawable(R.drawable.pending);
            default:
                return getContext().getResources().getDrawable(R.drawable.follow);
        }
    }
}
