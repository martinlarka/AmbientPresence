package nu.larka.ambientpresence.nest;

/**
 * Created by martin on 15-04-15.
 */
public class NestEnvironment {
    private String name;
    private boolean enabled;
    private String fromId;


    public NestEnvironment(String name, boolean enabled, String fromId) {

        this.name = name;
        this.enabled = enabled;
        this.fromId = fromId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFromId() {
        return fromId;
    }
}
