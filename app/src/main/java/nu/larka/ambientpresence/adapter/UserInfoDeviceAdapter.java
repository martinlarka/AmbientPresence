package nu.larka.ambientpresence.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.model.HueLightDevice;
import nu.larka.ambientpresence.model.User;

/**
 * Created by martin on 15-03-18.
 */
public class UserInfoDeviceAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HueLightDevice> lights;
    private User user;

    public UserInfoDeviceAdapter(Context context, ArrayList<HueLightDevice> lights, User user) {
        this.context = context;
        this.lights = lights;
        this.user = user;
    }

    @Override
    public int getCount() {
        if (lights.size() != 0)
            return lights.size();
        return 1;
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
                if (lights.size() != 0) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.hue_list_item, parent, false);
                    TextView lightName = (TextView) convertView.findViewById(R.id.light_name);
                    lightName.setText(lights.get(position).getName());

                    Spinner environmentSpinner = (Spinner) convertView.findViewById(R.id.hue_light_spinner);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, user.getEnvironmentNames());
                    environmentSpinner.setAdapter(adapter);
                    environmentSpinner.setOnItemSelectedListener(new SelectedEnvironmentListener(lights.get(position)));
                    environmentSpinner.setSelection(lights.get(position).getEnvironmentPos());
                } else {
                    convertView = LayoutInflater.from(context).inflate(R.layout.hue_list_item, parent, false);
                    TextView lightName = (TextView) convertView.findViewById(R.id.light_name);
                    lightName.setText(user.getUsername() + " " + context.getString(R.string.user_has_no_environments_registered));
                    lightName.setTypeface(null, Typeface.ITALIC);
                    convertView.findViewById(R.id.hue_light_spinner).setVisibility(View.INVISIBLE);
                }
            }
        // Return the completed view to render on screen
        return convertView;
    }

    class SelectedEnvironmentListener implements AdapterView.OnItemSelectedListener {

        private HueLightDevice light;

        public SelectedEnvironmentListener(HueLightDevice light) {
            this.light = light;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            light.setUser(user);
            light.setEnvironment(user.getEnvironmentNames().get(position));
            light.setEnvironmentPos(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
