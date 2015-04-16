package nu.larka.ambientpresence.nest;

import android.app.Activity;

import com.nestapi.lib.API.Structure;
import com.nestapi.lib.API.Thermostat;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import nu.larka.ambientpresence.R;

/**
 * Created by martin on 15-04-15.
 */
public class NestEnvironment {
    private static final String HOME = "home";
    private static final double SPAN = 5;
    private static final long MAXETA = TimeUnit.MINUTES.toMillis(120);
    private Activity activity;
    private String name;
    private boolean enabled;
    private String fromId;
    private EnvironmentType type;

    public enum EnvironmentType {
        TEMPERATURE, ETA, AWAY
    }

    public NestEnvironment(String name, boolean enabled, String fromId, EnvironmentType type, Activity activity) {

        this.name = name;
        this.enabled = enabled;
        this.fromId = fromId;
        this.type = type;
        this.activity = activity;
    }

    public String getName() {
        return name + getType();
    }

    private String getType() {
        switch (type) {
            case TEMPERATURE:
                return " - " + activity.getString(R.string.temperature_environment);
            case ETA:
                return " - " + activity.getString(R.string.eta_environment);
            case AWAY:
                return " - " + activity.getString(R.string.away_environment);
        }
        return "";
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

    public double getValue(Structure structure) {
        switch (type) {
            case ETA:
                Structure.ETA eta = structure.getETA();
                Timestamp begin = Timestamp.valueOf(eta.getEstimatedArrivalWindowBegin());
                if (begin.getTime() - System.currentTimeMillis() < MAXETA)
                    return 1.0 - (begin.getTime() - System.currentTimeMillis())/MAXETA;
            case AWAY:
                Structure.AwayState away = structure.getAwayState();
                if (away.getKey().equals(HOME)) {
                    return 1;
                } else {
                    return 0;
                }
        }
        return 0;
    }

    public double getValue(Thermostat thermostat) {
        double ambientT = thermostat.getAmbientTemperatureC();
        double targetT = thermostat.getTargetTemperatureC();
        if (targetT < ambientT - SPAN) return 0;
        if (targetT > ambientT + SPAN) return 1;

        double x = ((targetT + SPAN) - ambientT)/SPAN;
        return (Math.atan(10*x)/Math.PI)+0.5;
    }
}