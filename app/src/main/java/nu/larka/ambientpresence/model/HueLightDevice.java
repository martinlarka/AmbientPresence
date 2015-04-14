package nu.larka.ambientpresence.model;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.ArrayList;

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

    public HueLightDevice(PHLight light, PHBridge bridge) {
        super(light);
        this.light = light;
        this.bridge = bridge;
        this.theme = 1;
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
            case 0:
                blueToRed(lightState,value);
                break;
            default:
            case 1:
                whiteBrightness(lightState,value);
                break;
        }

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
        lightState.setBrightness((int) (254*value));
    }

    public static ArrayList<String> getHueThemes() {
        ArrayList<String> themes = new ArrayList<>();
        themes.add("Blue to Red");
        themes.add("White brightness");
        return themes;
    }
}