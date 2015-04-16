package nu.larka.ambientpresence.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.nest.NestEnvironment;

/**
 * Created by martin on 15-04-15.
 */
public class SetupNestThermostatAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NestEnvironment> environments;

    public SetupNestThermostatAdapter(Context context, ArrayList<NestEnvironment> environments) {
        this.environments = environments;
        this.context = context;
    }

    @Override
    public int getCount() {
        return environments.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Render device view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.nest_environment_list_item, parent, false);
            TextView nestEnvironmentName = (TextView) convertView.findViewById(R.id.nest_environment_name);
            CheckBox nestEnvironmentEnabled = (CheckBox) convertView.findViewById(R.id.nest_environment_enabled);

            nestEnvironmentName.setText(environments.get(position).getName());
            nestEnvironmentEnabled.setChecked(environments.get(position).isEnabled());
            nestEnvironmentEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    environments.get(position).setEnabled(isChecked);
                }
            });

        }
        // Return the completed view to render on screen
        return convertView;
    }
}
