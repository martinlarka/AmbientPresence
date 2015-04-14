package nu.larka.ambientpresence.model;

import android.os.Handler;

import com.firebase.client.Firebase;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import nu.larka.ambientpresence.activity.MainActivity;

/**
 * Created by martin on 15-04-13.
 *
 * Device for testing environment changes
 * Register environments on user, and change value on intervalls
 *
 */
public class TestDevice extends Device {
    private String environment = "Environment " + getDeviceName();
    private double value = 0.5;
    Timer timer;
    TimerTask timerTask;

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();

    public TestDevice(String deviceName) {
        super(deviceName);
    }

    public boolean isEnabled() {
        return true;
    }

    public String getEnvironment() {

        return environment;
    }

    public void registerEnvironments(Firebase mFirebaseRef) {
        initializeTimerTask(mFirebaseRef);
        startTimer();
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 1000, 1000); //
    }


    public void initializeTimerTask(final Firebase mFirebaseRef) {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        double change = (new Random().nextDouble()-0.5)/10;
                        value += change;
                        value = (value <= 0 || value >= 1) ? 0.5 : value;
                        mFirebaseRef.child(MainActivity.ENVIRONMENTS).child(getEnvironment()).setValue(value);
                    }
                });
            }
        };
    }
}
