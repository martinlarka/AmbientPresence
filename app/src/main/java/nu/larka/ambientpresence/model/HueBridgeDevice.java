package nu.larka.ambientpresence.model;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;

/**
 * Created by martin on 15-04-08.
 */
public class HueBridgeDevice extends Device {
    private String lastConnectedIPAddress;
    private String hueUsername;
    private PHBridge bridge;

    public HueBridgeDevice(String deviceName) {
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

    public void connect(PHHueSDK phHueSDK) {
        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(lastConnectedIPAddress);
        accessPoint.setUsername(hueUsername);
        phHueSDK.connect(accessPoint);
    }

    public void setPHBridge(PHBridge b) {
        this.bridge = b;
    }

    public PHBridge getBridge() {
        return bridge;
    }
}
