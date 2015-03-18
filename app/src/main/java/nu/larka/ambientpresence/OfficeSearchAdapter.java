package nu.larka.ambientpresence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import nu.larka.ambientpresence.model.User;

/**
 * Created by martin on 15-03-18.
 */
public class OfficeSearchAdapter extends ArrayAdapter<User> {

    public OfficeSearchAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
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
        // Return the completed view to render on screen
        return convertView;
    }
}
