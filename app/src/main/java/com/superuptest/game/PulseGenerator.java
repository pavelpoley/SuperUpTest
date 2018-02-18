package com.superuptest.game;

import android.os.Handler;
import android.support.annotation.NonNull;

/**
 * Created by pavel on 18/02/2018.
 * This class is helper class to generate pulse every X time.
 * The class uses Handler.postDelayed() method and calls itself each time, to generate
 * callback every X time.
 */

public class PulseGenerator {

    /**
     * The logging tag of this Class.
     * */
    private static final String TAG = "PulseGenerator";

    /**
     * Constant that define the interval time.
     * */
    private static final int INTERVAL = 3000;

    /**
     * Handler instance
    * */
    private Handler mHandler = new Handler();

    /**
     * Interface instance used to send callbacks to Activity.
     * */
    private PulseGeneratorCallbacks mCallbacks;

    /**
     * Interface to send callbacks to activity.
     * */
    public interface PulseGeneratorCallbacks{
        void onPulse();
    }

    /**
     * Constructor
     * @param activity The Activity must implement PulseGeneratorCallbacks
     * that listen to callbacks.
     * */
    public PulseGenerator(PulseGeneratorCallbacks activity) {
        this.mCallbacks = activity;
    }


    /**
     * Start pulsing.
     * */
    public void start(){
        mHandler.postDelayed(getRunnable(),INTERVAL);
    }

    /**
     *Stop pulsing.
     * */
    public void stop(){
        mHandler.removeCallbacksAndMessages(null);
    }


    /**
     *Getter for Runnable object that uses by the handler.
     * @return Runnable object that uses by the handler.
     * */
    @NonNull
    private Runnable getRunnable() {
        return new Runnable() {
            @Override
            public void run() {

                if (mHandler!=null){

                    //callback to activity
                    mCallbacks.onPulse();

                    //call postDelayed with this Runnable again
                    mHandler.postDelayed(this,INTERVAL);

                }
            }
        };
    }
}
