package nu.larka.ambientpresence.model;

import android.graphics.drawable.Drawable;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.ArrayList;

import nu.larka.ambientpresence.R;

/**
 * Created by martin on 15-04-13.
 */
public class HueLightDevice extends PHLight {

    private User user;
    private String environment;
    private PHBridge bridge;
    private PHLight light;
    private int environmentPos;
    private int theme;

    public HueLightDevice(PHLight light, PHBridge bridge, int theme) {
        super(light);
        this.light = light;
        this.bridge = bridge;
        this.theme = theme;
    }

    public PHBridge getBridge() {
        return bridge;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public void setEnvironmentPos(int environmentPos) {
        this.environmentPos = environmentPos;
    }

    public int getEnvironmentPos() {
        return environmentPos;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public void updateLight(double value) {
        PHLightState lightState = new PHLightState();

        switch (theme) {
            default:
            case 0:
                blueToRed(lightState,value);
                break;
            case 1:
                whiteBrightness(lightState,value);
                break;
            case 2:
                redGreenBlue(lightState,value);
                break;
        }
        lightState.setTransitionTime(1000);
        getBridge().updateLightState(light, lightState);
    }

    private void blueToRed(PHLightState lightState, double value) {
        lightState.setHue((int) (18360 * value)+46920);
        lightState.setBrightness(254);
        lightState.setSaturation(254);
    }

    private void whiteBrightness(PHLightState lightState, double value) {
        lightState.setHue(0);
        lightState.setSaturation(0);
        lightState.setBrightness((int) (254 * value));
    }

    private void redGreenBlue(PHLightState lightState, double value) {
        lightState.setHue((int) (46920 * value));
        lightState.setBrightness(254);
        lightState.setSaturation(254);
    }

    public static ArrayList<String> getHueThemes() {
        ArrayList<String> themes = new ArrayList<>();
        themes.add("Blue to Red");
        themes.add("White brightness");
        themes.add("Red green blue");
        return themes;
    }

    public static int numberOfThemes() {
        return 3;
    }

    public static int getThemeImage(int position) {
        switch (position) {
            case 0:
                return R.drawable.blue_to_red;
            case 1:
                return R.drawable.white_brightness;
            case 2:
                return R.drawable.red_green_blue;
        }
        return 0;
    }
}