package nu.larka.ambientpresence.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.philips.lighting.model.PHLight;

import java.util.ArrayList;
import java.util.List;

import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.listener.SearchItemListener;
import nu.larka.ambientpresence.model.Device;
import nu.larka.ambientpresence.model.User;

/**
 * Created by martin on 15-03-18.
 */
public class UserInfoDeviceAdapter extends BaseAdapter {
    private Context context;
    private List<PHLight> lights;
    private User user;

    public UserInfoDeviceAdapter(Context context, List<PHLight> lights, User user) {
        this.context = context;
        this.lights = lights;
        this.user = user;
    }

    @Override
    public int getCount() {
        return lights.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            // Render device view
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.hue_list_item, parent, false);
                TextView lightName = (TextView) convertView.findViewById(R.id.light_name);
                lightName.setText(lights.get(position).getName());

                Spinner environmentSpinner = (Spinner) convertView.findViewById(R.id.user_environment_spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, user.getEnvironments());
                environmentSpinner.setAdapter(adapter);
                environmentSpinner.setOnItemSelectedListener(new SelectedEnvironmentListener(position));
                environmentSpinner.setSelection(user.getSelectedEnvironment(position));
            }
        // Return the completed view to render on screen
        return convertView;
    }

    class SelectedEnvironmentListener implements AdapterView.OnItemSelectedListener {

        private int device;

        public SelectedEnvironmentListener(int device) {
            this.device = device;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // TODO register these environments to change the lights
            user.setSelectedEnvironment(device, position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
