package nu.larka.ambientpresence.model;

/**
 * Created by martin on 15-04-08.
 */
public class HueDevice extends Device {
    private String lastConnectedIPAddress;
    private String hueUsername;

    public HueDevice(String deviceName) {
        super(deviceName);
    }

    public void setLastConnectedIPAddress(String lastConnectedIPAddress) {
        this.lastConnectedIPAddress = lastConnectedIPAddress;
    }

    public String getLastConnectedIPAddress() {
        return lastConnectedIPAddress;
    }

    public String getHueUsername() {
        return hueUsername;
    }

    public void setHueUsername(String hueUsername) {
        this.hueUsername = hueUsername;
    }
}
