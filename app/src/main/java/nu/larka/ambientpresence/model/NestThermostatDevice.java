package nu.larka.ambientpresence.model;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.client.Firebase;
import com.nestapi.lib.API.AccessToken;
import com.nestapi.lib.API.Listener;
import com.nestapi.lib.API.NestAPI;
import com.nestapi.lib.API.Structure;
import com.nestapi.lib.API.Thermostat;
import com.nestapi.lib.AuthManager;
import com.nestapi.lib.ClientMetadata;

import java.util.ArrayList;

import nu.larka.ambientpresence.fragment.HomeFragment;
import nu.larka.ambientpresence.nest.Constants;
import nu.larka.ambientpresence.nest.NestEnvironment;
import nu.larka.ambientpresence.nest.Settings;

/**
 * Created by martin on 15-04-15.
 */
public class NestThermostatDevice extends Device implements NestAPI.AuthenticationListener,
        Listener.StructureListener,
        Listener.ThermostatListener {

    private static final String TAG = NestThermostatDevice.class.getSimpleName();
    private static final String THERMOSTAT_KEY = "thermostat_key";
    private static final String STRUCTURE_KEY = "structure_key";
    private Activity activity;
    private Firebase firebase;

    private Listener mUpdateListener;
    private NestAPI mNestApi;
    private AccessToken mToken;
    private Thermostat mThermostat;
    private Structure mStructure;

    private static final int AUTH_TOKEN_REQUEST_CODE = 101;
    private ArrayList<NestEnvironment> environments = new ArrayList<>();
    private ArrayList<String> registeredEnvironmentDevices = new ArrayList<>();

    public NestThermostatDevice(String deviceName, Activity activity, Firebase firebase) {
        super(deviceName);
        this.activity = activity;
        this.firebase = firebase;
        mNestApi = NestAPI.getInstance();
        mToken = Settings.loadAuthToken(activity.getApplicationContext());
        if (mToken != null) {
            authenticate(mToken);
        } else {
            obtainAccessToken();
        }
    }

    public boolean isEnabled() {
        return true;
    }

    private void authenticate(AccessToken token) {
        Log.v(TAG, "Authenticating...");
        NestAPI.getInstance().authenticate(token, this);
    }

    private void obtainAccessToken() {
        Log.v(TAG, "starting auth flow...");
        final ClientMetadata metadata = new ClientMetadata.Builder()
                .setClientID(Constants.CLIENT_ID)
                .setClientSecret(Constants.CLIENT_SECRET)
                .setRedirectURL(Constants.REDIRECT_URL)
                .build();
        AuthManager.launchAuthFlow(activity, AUTH_TOKEN_REQUEST_CODE, metadata);
    }



    private void fetchData(){
        Log.v(TAG, "Fetching data...");

        mUpdateListener = new Listener.Builder()
                .setStructureListener(this)
                .setThermostatListener(this)
                .build();

        mNestApi.addUpdateListener(mUpdateListener);
    }

    @Override
    public void onAuthenticationSuccess() {
        Log.v(TAG, "Authentication succeeded.");
        fetchData();
    }

    @Override
    public void onAuthenticationFailure(int errorCode) {
        Log.v(TAG, "Authentication failed with error: " + errorCode);
    }

    @Override
    public void onStructureUpdated(@NonNull Structure structure) {
        if (!registeredEnvironmentDevices.contains(structure.getStructureID())) {
            registerEnvironments(structure);
        }
    }


    @Override
    public void onThermostatUpdated(@NonNull Thermostat thermostat) {
        if (!registeredEnvironmentDevices.contains(thermostat.getDeviceID())) {
            registerEnvironments(thermostat);
        }
    }

    private void registerEnvironments(Thermostat thermostat) {
        environments.add(new NestEnvironment(thermostat.getName() + " - Temperature", true, thermostat.getDeviceID()));
        registeredEnvironmentDevices.add(thermostat.getDeviceID());
    }

    private void registerEnvironments(Structure structure) {
        environments.add(new NestEnvironment(structure.getName() + " - ETA", true, structure.getStructureID()));
        environments.add(new NestEnvironment(structure.getName() + " - Away", true, structure.getStructureID()));
        registeredEnvironmentDevices.add(structure.getStructureID());
    }

    public void disconnect() {
        mNestApi.removeUpdateListener(mUpdateListener);
        // TODO Remove environments from firebase
    }

    public ArrayList<NestEnvironment> getEnvironments() {
        return environments;
    }

    public void nestTokenObtained(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || requestCode != AUTH_TOKEN_REQUEST_CODE) {
            return;
        }

        if (AuthManager.hasAccessToken(data)) {
            mToken = AuthManager.getAccessToken(data);
            Settings.saveAuthToken(activity, mToken);
            Log.v(TAG, "Main Activity parsed auth token: " + mToken.getToken() + " expires: " + mToken.getExpiresIn());
            authenticate(mToken);
        } else {
            Log.e(TAG, "Unable to resolve access token from payload.");
        }
    }
}
