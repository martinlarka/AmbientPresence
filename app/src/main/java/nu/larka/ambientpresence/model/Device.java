package nu.larka.ambientpresence.model;

/**
 * Created by martin on 15-04-08.
 */
public class Device {
    private String deviceName;
    private boolean enabled;

    public Device(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
