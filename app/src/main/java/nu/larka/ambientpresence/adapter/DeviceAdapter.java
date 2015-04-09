package nu.larka.ambientpresence.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.model.Device;
import nu.larka.ambientpresence.model.User;

/**
 * Created by martin on 15-03-18.
 */
public class DeviceAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Device> deviceArrayList;

    public DeviceAdapter(Context context, ArrayList<Device> deviceArrayList) {
        this.context = context;
        this.deviceArrayList = deviceArrayList;
    }

    @Override
    public int getCount() {
        return deviceArrayList.size()+1;
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
        if (position == deviceArrayList.size()) {
            // Render add device view
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.add_device_list_item, parent, false);
            }

        } else {
            // Render device view
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.device_list_item, parent, false);
                TextView deviceName = (TextView) convertView.findViewById(R.id.device_name);
                deviceName.setText(deviceArrayList.get(position).getDeviceName());
                if (!deviceArrayList.get(position).isEnabled()) {
                    deviceName.setTextColor(convertView.getResources().getColor(R.color.device_not_enabled));
                } else {
                    deviceName.setTextColor(convertView.getResources().getColor(R.color.device_enabled));
                }
            }
        }
        // Return the completed view to render on screen
        return convertView;
    }

}
