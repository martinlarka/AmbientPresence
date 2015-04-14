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

import java.util.ArrayList;

import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.model.HueLightDevice;

/**
 * Created by martin on 15-03-18.
 */
public class SetupHueLightAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HueLightDevice> lights;

    public SetupHueLightAdapter(Context context, ArrayList<HueLightDevice> lights) {
        this.context = context;
        this.lights = lights;
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

                Spinner themeSpinner = (Spinner) convertView.findViewById(R.id.hue_light_spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, new String[]{"NONE"});
                themeSpinner.setAdapter(adapter);
                themeSpinner.setOnItemSelectedListener(new SelectedThemeListener(lights.get(position)));
                themeSpinner.setSelection(lights.get(position).getEnvironmentPos());
            }
        // Return the completed view to render on screen
        return convertView;
    }

    class SelectedThemeListener implements AdapterView.OnItemSelectedListener {

        private HueLightDevice light;

        public SelectedThemeListener(HueLightDevice light) {
            this.light = light;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //light.setTheme();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
