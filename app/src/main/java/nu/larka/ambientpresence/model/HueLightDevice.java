package nu.larka.ambientpresence.model;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

/**
 * Created by martin on 15-04-13.
 */
public class HueLightDevice extends PHLight {

    private User user;
    private String environment;
    private PHBridge bridge;
    private PHLight light;
    private int environmentPos;

    public HueLightDevice(PHLight light, PHBridge bridge) {
        super(light);
        this.light = light;
        this.bridge = bridge;
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

    public void updateLight(double value) {
        PHLightState lightState = new PHLightState();

        lightState.setHue((int) (18360 * value)+46920);
        lightState.setBrightness(254);
        lightState.setSaturation(254);

        getBridge().updateLightState(light, lightState);
    }
}