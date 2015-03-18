package nu.larka.ambientpresence.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.model.User;

/**
 * Created by martin on 15-03-16.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<User> followers = new ArrayList<>();

    public ImageAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return followers.size()+1;
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
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        if (position == followers.size()) {
            imageView.setImageResource(R.drawable.addhome);
        } else {
            imageView.setImageResource(R.drawable.home250);
        }

        return imageView;
    }

    public void setFollowers(ArrayList<User> followers) {
        this.followers = followers;
    }
}
