package nu.larka.ambientpresence.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.model.HueLightDevice;

/**
 * Created by martin on 15-04-17.
 */
public class HueThemeAdapter extends BaseAdapter {

    private Context context;

    public HueThemeAdapter(Context context) {
        this.context = context;
    }
    @Override
    public int getCount() {
       return HueLightDevice.numberOfThemes();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.hue_theme_item, parent, false);
            convertView.findViewById(R.id.hue_theme_image).setBackground(context.getResources().getDrawable(HueLightDevice.getThemeImage(position)));
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
