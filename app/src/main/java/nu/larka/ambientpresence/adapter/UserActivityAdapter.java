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
public class UserActivityAdapter extends ArrayAdapter<User> {

    public UserActivityAdapter(Context context, ArrayList<User> otherUsers) {
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

        // Lookup view for data population
        TextView nameTextView = (TextView) convertView.findViewById(R.id.user_name);
        TextView usernameTextView = (TextView) convertView.findViewById(R.id.user_username);
        ImageView userImage = (ImageView) convertView.findViewById(R.id.office_image);

        // Populate the data into the template view using the data object
        nameTextView.setText(user.getName());
        usernameTextView.setText(user.getUsername());
        if (user.hasImage()) {
            userImage.setImageBitmap(user.getImage());
        } else {
            userImage.setImageResource(R.drawable.home250);
        }


        // Return the completed view to render on screen
        return convertView;
    }
}
