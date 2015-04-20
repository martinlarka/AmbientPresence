package nu.larka.ambientpresence.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.connection.impl.PHBridgeInternal;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nu.larka.ambientpresence.activity.MainActivity;
import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.adapter.DeviceAdapter;
import nu.larka.ambientpresence.adapter.SetupHueLightAdapter;
import nu.larka.ambientpresence.adapter.SetupNestThermostatAdapter;
import nu.larka.ambientpresence.hue.HueSharedPreferences;
import nu.larka.ambientpresence.hue.PHPushlinkActivity;
import nu.larka.ambientpresence.model.Device;
import nu.larka.ambientpresence.model.HueBridgeDevice;
import nu.larka.ambientpresence.model.HueLightDevice;
import nu.larka.ambientpresence.model.NestThermostatDevice;
import nu.larka.ambientpresence.model.TestDevice;
import nu.larka.ambientpresence.model.User;
import nu.larka.ambientpresence.nest.NestSettings;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements ValueEventListener {

    private User homeUser = null;
    private Uri imageUri;
    private ImageView userImageView;
    private TextView titleView;
    private TextView nameTextview;
    private ListView deviceListView;
    private DeviceAdapter deviceAdapter;
    private ArrayList<Device> deviceArrayList;

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Firebase mFirebaseRef;
    private PHHueSDK phHueSDK;
    private HueSharedPreferences hueSharedPreferences;
    private SearchHueFragment searchHueFragment;
    private String phUsername = null;
    private ArrayList<HueLightDevice> hueLightArrayList;

    private NestThermostatDevice thermostatDevice;
    private Handler splashScreenHandler;

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        userImageView = (ImageView) v.findViewById(R.id.home_image_view);
        titleView = (TextView) v.findViewById(R.id.home_titel);
        nameTextview = (TextView) v.findViewById(R.id.home_user_name);
        deviceListView = (ListView) v.findViewById(R.id.device_list_view);

        if (homeUser != null) {
            titleView.setText(getResources().getString(R.string.office_home) + homeUser.getName());
            nameTextview.setText(homeUser.getUsername());

            if (!homeUser.hasImage()) {
                userImageView.setImageResource(R.drawable.home500);
            } else {
                userImageView.setImageBitmap(homeUser.getImage());
            }
        } else {
            mFirebaseRef.addListenerForSingleValueEvent(this);

            // Gets an instance of the Hue SDK.
            phHueSDK = PHHueSDK.create();

            // Set the Device Name (name of your app). This will be stored in your bridge whitelist entry.
            phHueSDK.setAppName("AmbientPresenceApp");
            phHueSDK.setDeviceName(android.os.Build.MODEL);

            // Register the PHSDKListener to receive callbacks from the bridge.
            phHueSDK.getNotificationManager().registerSDKListener(phsdkListener);
        }
        userImageView.setOnLongClickListener(imageLongClickListener);
        deviceAdapter = new DeviceAdapter(v.getContext(), deviceArrayList);
        deviceListView.setAdapter(deviceAdapter);
        deviceListView.setOnItemClickListener(deviceClickListener);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        Context context = getActivity().getApplicationContext();
                        ensurePhotoNotRotated(context, imageUri);
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

                        homeUser.setImage(bitmap);
                        userImageView.setImageBitmap(bitmap);
                        new UploadImageToFirebase().execute(bitmap);

                    } catch (IOException e) {
                    }
                }
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        homeUser = new User(dataSnapshot.getKey());
        homeUser.setName((String)dataSnapshot.child(MainActivity.NAME).getValue());
        homeUser.setUsername((String) dataSnapshot.child(MainActivity.USERNAME).getValue());

        titleView.setText(getResources().getString(R.string.office_home) + homeUser.getName());
        nameTextview.setText(homeUser.getUsername());

        String str = (String) dataSnapshot.child(MainActivity.USER_IMAGE).getValue();
        if (str != null) {
            byte[] imageAsBytes = com.firebase.tubesock.Base64.decode(str.getBytes());
            Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            homeUser.setImage(bmp);
            userImageView.setImageBitmap(bmp);
        } else {
            userImageView.setImageResource(R.drawable.home500);
        }

        populateDeviceList();

        // Remove splash screen
        splashScreenHandler.sendEmptyMessage(MainActivity.HOMEFRAGMENTLOADED);
    }

    private void populateDeviceList() {
        // Try to automatically connect to the last known bridge.  For first time use this will be empty so a bridge search is automatically started.
        hueSharedPreferences = HueSharedPreferences.getInstance(getActivity().getApplicationContext());
        String lastIpAddress   = hueSharedPreferences.getLastConnectedIPAddress();
        String lastUsername    = hueSharedPreferences.getUsername();
        if (lastIpAddress !=null && !lastIpAddress.equals("")) {
            HueBridgeDevice hueDevice = new HueBridgeDevice(getString(R.string.hue_light) + lastIpAddress);
            hueDevice.setHueUsername(lastUsername);
            hueDevice.setLastConnectedIPAddress(lastIpAddress);
            hueDevice.setEnabled(false);

            hueDevice.connect(phHueSDK);
            deviceArrayList.add(hueDevice);
        }

        if (NestSettings.hasAuthToken(getActivity().getApplicationContext())) {
            thermostatDevice = new NestThermostatDevice(getResources().getStringArray(R.array.supported_devices)[1], getActivity(), mFirebaseRef);
            deviceArrayList.add(thermostatDevice);
        }

        updateDeviceList();
    }

    private void updateDeviceList() {
        deviceAdapter.notifyDataSetChanged();
        deviceListView.invalidateViews();
        deviceListView.setAdapter(deviceAdapter);
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    private View.OnLongClickListener imageLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            // Upload new image
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "AMBIENTPRESENCE_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File image = null;
            try {
                image = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );
                imageUri = Uri.fromFile(image);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    };

    public void setFirebaseRef(Firebase firebaseRef) {
        this.mFirebaseRef = firebaseRef;
    }

    public void setDeviceArrayList(ArrayList<Device> deviceArrayList) {
        this.deviceArrayList = deviceArrayList;
    }

    public void setHueDeviceArrayList(ArrayList<HueLightDevice> hueLightArrayList) {
        this.hueLightArrayList = hueLightArrayList;
    }

    public void nestTokenObtained(int requestCode, int resultCode, Intent data) {
        thermostatDevice.nestTokenObtained(requestCode, resultCode, data);
    }

    public void setSplashScreenHandler(Handler splashScreenHandler) {
        this.splashScreenHandler = splashScreenHandler;
    }

    private class UploadImageToFirebase extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected Void doInBackground(Bitmap... bmps) {
            Bitmap bmp;
            bmp = bmps[0];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            //bmp.recycle();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);

            mFirebaseRef.child(MainActivity.USER_IMAGE).setValue(imageFile);
            return null;
        }
    }

    private static void ensurePhotoNotRotated(Context context, Uri imgUri) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(imgUri.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int angle = 0;
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            angle = 90;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            angle = 180;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            angle = 270;
        }

        InputStream is;
        try {
            is = context.getContentResolver().openInputStream(imgUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        Bitmap bmp;

        if (angle != 0) {

            Matrix mat = new Matrix();
            mat.postRotate(angle);

            bmp = BitmapFactory.decodeStream(is);
            bmp = Bitmap.createScaledBitmap(bmp,400, 400*bmp.getHeight()/bmp.getWidth(), false);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);

        } else {
            bmp = BitmapFactory.decodeStream(is);
            bmp = Bitmap.createScaledBitmap(bmp,400, 400*bmp.getHeight()/bmp.getWidth(), false);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight());
        }

        OutputStream os;
        try {
            os = context.getContentResolver().openOutputStream(imgUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);

        try {
            exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_NORMAL));
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AdapterView.OnItemClickListener deviceClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == deviceArrayList.size()) {
                // Add device
                addDeviceDialog.show(getFragmentManager(), "add_devices");
            } else {
                // Setup device
                SetupDeviceDialog setupDialog = new SetupDeviceDialog();
                setupDialog.setDevice(deviceArrayList.get(position));
                setupDialog.show(getFragmentManager(), "setup_device");

            }
        }
    };


    public class SetupDeviceDialog extends DialogFragment {

        private Device device;
        private ListView deviceSetupListView;
        private ArrayList<HueLightDevice> lights;

        public void setDevice(Device device) {
            this.device = device;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            if (device instanceof NestThermostatDevice) {
                ((NestThermostatDevice)device).updateEnvironments(getActivity().getApplicationContext());

            } else if (device instanceof HueBridgeDevice) {
                hueSharedPreferences.setThemes(lights);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_setup_device, container, false);
            getDialog().setTitle(device.getDeviceName());

            Button removeDeviceButton = (Button) view.findViewById(R.id.remove_device_button);
            removeDeviceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    disconnectDevice(device);
                    updateDeviceList();
                    getDialog().cancel();
                }
            });

            deviceSetupListView = (ListView) view.findViewById(R.id.device_list_view);

            if (device instanceof HueBridgeDevice) { // Setup for Hue Bridge
                setupForHueBridge(view);
            } else if (device instanceof NestThermostatDevice) {
                setupForNestThermostat((NestThermostatDevice)device, view);
            }

            return view;
        }


        private void disconnectDevice(Device device) {
            if (device instanceof HueBridgeDevice) {
                ((HueBridgeDevice) device).disconnect(phHueSDK);
                hueSharedPreferences.setLastConnectedIPAddress(null);
                ArrayList<HueLightDevice> removeLights = new ArrayList<>();
                for (HueLightDevice l : hueLightArrayList) {
                    if (l.getBridge().equals(((HueBridgeDevice) device).getBridge())) {
                        removeLights.add(l);
                        hueSharedPreferences.removeTheme(l.getName());
                    }
                }
                hueLightArrayList.removeAll(removeLights);

            } else if (device instanceof TestDevice) {
                ((TestDevice) device).disconnect();
                mFirebaseRef.child(MainActivity.ENVIRONMENTS).child(((TestDevice) device).getEnvironment()).removeValue();
            } else if (device instanceof NestThermostatDevice) {
                ((NestThermostatDevice) device).disconnect();
                NestSettings.removeAuthToken(getActivity().getApplicationContext());
            }
            deviceArrayList.remove(device);
        }

        private void setupForHueBridge(View view) {
            lights = new ArrayList<>();
            for (HueLightDevice l : hueLightArrayList) {
                if (l.getBridge().equals(((HueBridgeDevice)device).getBridge())) {
                    lights.add(l);
                }
            }
            SetupHueLightAdapter deviceAdapter = new SetupHueLightAdapter(view.getContext(), lights);
            deviceSetupListView.setAdapter(deviceAdapter);
        }

        private void setupForNestThermostat(NestThermostatDevice device, View view) {
            SetupNestThermostatAdapter deviceAdapter = new SetupNestThermostatAdapter(view.getContext(), device.getEnvironments());
            deviceSetupListView.setAdapter(deviceAdapter);
        }
    }

    private DialogFragment addDeviceDialog = new DialogFragment() {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.add_new_device)
                    .setItems(R.array.supported_devices, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String[] supportedDevices = getResources().getStringArray(R.array.supported_devices);
                            switch (which) {
                                case 0:
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    searchHueFragment = new SearchHueFragment();
                                    searchHueFragment.setOnItemClickListener(onHueSearchItemClickListener);
                                    // Replace whatever is in the fragment_container view with this fragment,
                                    // and add the transaction to the back stack so the user can navigate back
                                    transaction.replace(R.id.info_fragment, searchHueFragment);
                                    transaction.addToBackStack(null);

                                    // Commit the transaction
                                    transaction.commit();
                                    break;
                                case 1:
                                    thermostatDevice = new NestThermostatDevice(supportedDevices[which], getActivity() , mFirebaseRef);
                                    deviceArrayList.add(thermostatDevice);
                                    updateDeviceList();
                                    break;
                                case 2:
                                    TestDevice td = new TestDevice(supportedDevices[which]);
                                    td.registerEnvironments(mFirebaseRef);
                                    deviceArrayList.add(td);
                                    updateDeviceList();
                                    break;
                            }
                        }
                    });
            return builder.create();
        }
    };

    // Local SDK Listener
    private PHSDKListener phsdkListener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
            // Handle your bridge search results here.  Typically if multiple results are returned you will want to display them in a list
            // and let the user select their bridge.   If one is found you may opt to connect automatically to that bridge.
            Log.i("HUE", "AccessPointFound: " + accessPoint.size());

            if (accessPoint.size() > 0) {
                phHueSDK.getAccessPointsFound().clear();
                phHueSDK.getAccessPointsFound().addAll(accessPoint);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchHueFragment.adapterUpdateData(phHueSDK.getAccessPointsFound());
                    }
                });

            }
        }

        @Override
        public void onCacheUpdated(List cacheNotificationsList, PHBridge bridge) {
            // Here you receive notifications that the BridgeResource Cache was updated. Use the PHMessageType to
            // check which cache was updated, e.g.
            if (cacheNotificationsList.contains(PHMessageType.LIGHTS_CACHE_UPDATED)) {
                Log.i("HUE", "Lights Cache Updated ");
            }
        }

        @Override
        public void onBridgeConnected(PHBridge b) {
            phHueSDK.setSelectedBridge(b);
            phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            phHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
            // Here it is recommended to set your connected bridge in your sdk object (as above) and start the heartbeat.
            // At this point you are connected to a bridge so you should pass control to your main program/activity.
            // Also it is recommended you store the connected IP Address/ Username in your app here.  This will allow easy automatic connection on subsequent use.

            boolean hueFound = false;
            for (Device device : deviceArrayList) {
                if (device.getClass() == HueBridgeDevice.class &&
                        ((HueBridgeDevice)device).getLastConnectedIPAddress().equals(b.getResourceCache().getBridgeConfiguration().getIpAddress())) {
                    hueFound = true;
                    device.setEnabled(true);
                    ((HueBridgeDevice)device).setPHBridge(b);
                    addHueLightsToArray(b);
                }
            }
            if (!hueFound) {
                HueBridgeDevice hue = new HueBridgeDevice(getString(R.string.hue_light) + b.getResourceCache().getBridgeConfiguration().getIpAddress());
                hue.setPHBridge(b);
                hue.setHueUsername(phUsername);
                hue.setLastConnectedIPAddress(b.getResourceCache().getBridgeConfiguration().getIpAddress());
                hue.setEnabled(true);
                deviceArrayList.add(hue);
                addHueLightsToArray(b);
                saveHueInPreferences(hue);
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateDeviceList();
                }
            });

            Log.i("HUE", "Bridge connected");
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            Log.w("HUE", "Authentication Required.");
            phHueSDK.startPushlinkAuthentication(accessPoint);
            startActivity(new Intent(getActivity(), PHPushlinkActivity.class));
        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {

        }

        @Override
        public void onConnectionLost(PHAccessPoint accessPoint) {
            // Here you would handle the loss of connection to your bridge.
        }

        @Override
        public void onError(int code, final String message) {
            // Here you can handle events such as Bridge Not Responding, Authentication Failed and Bridge Not Found
        }

        @Override
        public void onParsingErrors(List parsingErrorsList) {
            // Any JSON parsing errors are returned here.  Typically your program should never return these.
        }
    };

    private void saveHueInPreferences(HueBridgeDevice hue) {
        hueSharedPreferences.setLastConnectedIPAddress(hue.getLastConnectedIPAddress());
        hueSharedPreferences.setUsername(hue.getHueUsername());
    }

    private void addHueLightsToArray(PHBridge b) {
        List<PHLight> lights = b.getResourceCache().getAllLights();
            for (PHLight l : lights) {
                hueLightArrayList.add(new HueLightDevice(l,b, hueSharedPreferences.getTheme(l.getName())));
            }
    }

    private AdapterView.OnItemClickListener onHueSearchItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PHAccessPoint accessPoint = searchHueFragment.getAccessPoint(position);
            if (phUsername == null) {
                phUsername = PHBridgeInternal.generateUniqueKey();
            }
            accessPoint.setUsername(phUsername);
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
    };
}
