package com.superuptest.game;

import android.os.Handler;
import android.support.annotation.NonNull;

/**
 * Created by pavel on 18/02/2018.
 * This class similar to PulseGenerator but with more faster rate.
 */

public class SamplingGenerator {

    /**
     * The logging tag of this Class.
     * */
    private static final String TAG = "SamplingGenerator";

    /**
     * Constant that define the onSample rate in milliseconds.
     * 0 = most fastest.
     * */
    private static final int SAMPLE_RATE = 0;

    /**
     * Handler instance.
     * */
    private Handler mHandler = new Handler();

    /**
     * Interface instance used to send callbacks to Activity.
     * */
    private SamplingGeneratorCallbacks mCallbacks;

    /**
     * Interface to send callbacks to activity.
     * */
    public interface SamplingGeneratorCallbacks{
        void onSample();
    }

    /**
     * Constructor
     * @param activity The Activity must implement SamplingGeneratorCallbacks
     * that listen to callbacks.
     * */
    public SamplingGenerator(SamplingGeneratorCallbacks activity) {
        this.mCallbacks = activity;
    }


    /**
     * Start sampling.
     * */
    public void start(){
        mHandler.postDelayed(getRunnable(), SAMPLE_RATE);
    }

    /**
     *Stop sampling.
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
                    mCallbacks.onSample();

                    //call postDelayed with this Runnable again
                    mHandler.postDelayed(this, SAMPLE_RATE);

                }
            }
        };
    }
}
