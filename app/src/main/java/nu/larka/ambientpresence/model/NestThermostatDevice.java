package nu.larka.ambientpresence.model;

import android.app.Activity;
import android.content.Context;
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

import nu.larka.ambientpresence.activity.MainActivity;
import nu.larka.ambientpresence.nest.Constants;
import nu.larka.ambientpresence.nest.NestEnvironment;
import nu.larka.ambientpresence.nest.NestSettings;

/**
 * Created by martin on 15-04-15.
 */
public class NestThermostatDevice extends Device implements NestAPI.AuthenticationListener,
        Listener.StructureListener,
        Listener.ThermostatListener {

    private static final String TAG = NestThermostatDevice.class.getSimpleName();
    private Activity activity;
    private Firebase firebase;

    private Listener mUpdateListener;
    private NestAPI mNestApi;
    private AccessToken mToken;

    public static final int AUTH_TOKEN_REQUEST_CODE = 101;
    private ArrayList<NestEnvironment> environments = new ArrayList<>();
    private ArrayList<String> registeredEnvironmentDevices = new ArrayList<>();

    public NestThermostatDevice(String deviceName, Activity activity, Firebase firebase) {
        super(deviceName);
        this.activity = activity;
        this.firebase = firebase;
        mNestApi = NestAPI.getInstance();
        mToken = NestSettings.loadAuthToken(activity.getApplicationContext());
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
        for (NestEnvironment env : environments) {
            if (env.getFromId().equals(structure.getStructureID()) && env.isEnabled()) {
                firebase.child(MainActivity.ENVIRONMENTS).child(env.getName()).setValue(env.getValue(structure));
            }
        }
    }


    @Override
    public void onThermostatUpdated(@NonNull Thermostat thermostat) {
        if (!registeredEnvironmentDevices.contains(thermostat.getDeviceID())) {
            registerEnvironments(thermostat);
        }
        for (NestEnvironment env : environments) {
            if (env.getFromId().equals(thermostat.getDeviceID()) && env.isEnabled()) {
                firebase.child(MainActivity.ENVIRONMENTS).child(env.getName()).setValue(env.getValue(thermostat));
            }
        }
    }

    private void registerEnvironments(Thermostat thermostat) {
        NestEnvironment env = new NestEnvironment(thermostat.getName(), thermostat.getDeviceID(), NestEnvironment.EnvironmentType.TEMPERATURE, activity);
        env.setEnabled(NestSettings.getNestEnvironment(activity.getApplicationContext(), env.getName()));
        environments.add(env);
        registeredEnvironmentDevices.add(thermostat.getDeviceID());
    }

    private void registerEnvironments(Structure structure) {
        NestEnvironment env = new NestEnvironment(structure.getName(), structure.getStructureID(), NestEnvironment.EnvironmentType.ETA, activity);
        env.setEnabled(NestSettings.getNestEnvironment(activity.getApplicationContext(), env.getName()));
        environments.add(env);
        env = new NestEnvironment(structure.getName(), structure.getStructureID(), NestEnvironment.EnvironmentType.AWAY, activity);
        env.setEnabled(NestSettings.getNestEnvironment(activity.getApplicationContext(), env.getName()));
        environments.add(env);
        registeredEnvironmentDevices.add(structure.getStructureID());
    }

    public void disconnect() {
        mNestApi.removeUpdateListener(mUpdateListener);
        for (NestEnvironment env : environments) {
            env.setEnabled(false);
        }
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
            NestSettings.saveAuthToken(activity, mToken);
            Log.v(TAG, "Main Activity parsed auth token: " + mToken.getToken() + " expires: " + mToken.getExpiresIn());
            authenticate(mToken);
        } else {
            Log.e(TAG, "Unable to resolve access token from payload.");
        }
    }

    public void updateEnvironments(Context applicationContext) {
        for (NestEnvironment env : environments) {
            if (!env.isEnabled()) {
                firebase.child(MainActivity.ENVIRONMENTS).child(env.getName()).removeValue();
            } else {
                firebase.child(MainActivity.ENVIRONMENTS).child(env.getName()).setValue(0.5);
            }
            NestSettings.saveNestEnvironment(applicationContext, env);
        }
    }
}
