package nu.larka.ambientpresence.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;

import java.util.List;

import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.adapter.HueDeviceAdapter;
import nu.larka.ambientpresence.preferences.HueSharedPreferences;

/**
 * Created by martin on 15-04-08.
 */
public class SearchHueFragment extends Fragment implements AdapterView.OnItemClickListener {

    private PHSDKListener listener;
    private PHHueSDK phHueSDK;
    private HueDeviceAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_hue, container, false);

        // Gets an instance of the Hue SDK.
        phHueSDK = PHHueSDK.getInstance();

        // Register the PHSDKListener to receive callbacks from the bridge.
        phHueSDK.getNotificationManager().registerSDKListener(this.listener);

        adapter = new HueDeviceAdapter(getActivity(), phHueSDK.getAccessPointsFound());
        ListView hueListView = (ListView) v.findViewById(R.id.hue_list_view);
        hueListView.setAdapter(adapter);
        hueListView.setOnItemClickListener(this);

        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        // Start the UPNP Searching of local bridges.
        sm.search(true, true);

        return v;
    }

    public void adapterUpdateData(List<PHAccessPoint> accessPoint) {
        adapter.updateData(accessPoint);
    }

    public void setListener(PHSDKListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HueSharedPreferences prefs = HueSharedPreferences.getInstance(getActivity());
        PHAccessPoint accessPoint = (PHAccessPoint) adapter.getItem(position);
        accessPoint.setUsername(prefs.getUsername());
        PHBridge connectedBridge = phHueSDK.getSelectedBridge();

        if (connectedBridge != null) {
            String connectedIP = connectedBridge.getResourceCache().getBridgeConfiguration().getIpAddress();
            if (connectedIP != null) {   // We are already connected here:-
                phHueSDK.disableHeartbeat(connectedBridge);
                phHueSDK.disconnect(connectedBridge);
            }
        }
        phHueSDK.connect(accessPoint);
    }
}
