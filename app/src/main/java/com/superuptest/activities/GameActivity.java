package com.superuptest.activities;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.superuptest.R;
import com.superuptest.game.PulseGenerator;
import com.superuptest.game.SamplingGenerator;
import com.superuptest.views.WaveView;

    /**
    * This Activity holds all the logic the game.
    *
    * WaveView - represent a pulsing circle with holes.
    *
    * GameBorderView - represent the border of the game.
    *
    * PulseGenerator - used to generate WaveView every X time (3 sec in our case).
    *
    * SamplingGenerator - generate fast sampling rate.
    * Why i used it?
    * Because onTouch(View v, MotionEvent event) not called when the user touch the scree
    * and not move the finger, so i hold the last coordinates separately to check if
    * the WaveView reached the touched position.
    *
    * */

public class GameActivity extends AppCompatActivity implements
        WaveView.WaveViewCallbacks,
        PulseGenerator.PulseGeneratorCallbacks,
        View.OnTouchListener, SamplingGenerator.SamplingGeneratorCallbacks {

    /**
     * The logging tag of this Class.
     * */
    private static final String TAG = "GameActivity";

    /**
     * TextView that show points.
     * */
    private TextView tvCount;

    /**
     * ConstraintLayout hold all the views, include generated WaveViews.
     * */
    private ConstraintLayout mContainer;

    /**
     * Count of points - how much circles got outside of the border.
     * */
    private int mCount = 0;

    /**
     * PulseGenerator instance to generate pulse rate of WaveView.
     * */
    private PulseGenerator mPulseGenerator;

    /**
     * PulseGenerator instance to generate sample rate.
     * */
    private SamplingGenerator mSampleGenerator;

    /**
     * Holds the last X coordinate that was touched.
     * */
    private int mLastX = 0;

    /**
     * Holds the last Y coordinate that was touched.
     * */
    private int mLastY = 0;

    /**
     * Value of white color (not of Color.WHITE).
     * */
    private static final int COLOR_WHITE = 0;

    /**
     * ConstraintSet instance.
     * */
    private ConstraintSet constraintSet = new ConstraintSet();

    /**
     * Holds the value of screen width, used to create WaveViews.
     * */
    private int screenWidth;



        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        screenWidth = getScreenWidth();

        mContainer = findViewById(R.id.cc_container);
        tvCount = findViewById(R.id.tv_count);

        mContainer.setDrawingCacheEnabled(true);
        mContainer.setOnTouchListener(this);

        mPulseGenerator = new PulseGenerator(this);
        mSampleGenerator = new SamplingGenerator(this);

        mPulseGenerator.start();
        mSampleGenerator.start();

    }

    /**
     * Return screen width.
     * @return screen width.
     * */
    private int getScreenWidth(){
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        return point.x;
    }

    /**
     * This method define if the user touched touch the border or the WaveView,
     * it take the last coordinates and check the color.
     * If the color is NOT white it is mean that the user touch the border or the WaveView.
     * */
    private void checkColor() {

        try {

            int color = mContainer.getDrawingCache().getPixel(mLastX, mLastY);

            if (color != COLOR_WHITE){
                gameOver();
            }

        } catch (Exception e) {
            Log.w(TAG, "checkColor: outside of view");
        }
    }

    /**
     * This callback called when a WaveView reach the border.
     * @param wave the instance of the WaveView to let the layout remove the view.
     * */
    @Override
    public void onWaveReachedBorder(WaveView wave) {

        //if the values is 0 this is mean the user not touched the screen,
        //game not started, no need to count points.
        if (mLastX!=0 && mLastY!=0)
        mCount++;

        if (tvCount!=null)
            tvCount.setText("Points: " + mCount);

        if (mContainer!=null) {
            mContainer.removeView(wave);
        }
    }

    /**
     * This callback called when need to create new WaveView.
     * */
    @Override
    public void onPulse() {
        createWave();
    }


    /**
     * Create new WaveView and add to layout.
     * */
    private void createWave(){
        final WaveView wave = new WaveView(this);
        wave.setLayoutParams(getParams());
        wave.setDeviceScreenWidth(screenWidth);

        mContainer.addView(wave);

        constraintSet.clone(mContainer);
        constraintSet.connect(wave.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        constraintSet.connect(wave.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
        constraintSet.connect(wave.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
        constraintSet.connect(wave.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        constraintSet.applyTo(mContainer);

    }

    /**
     * Return LayoutParams for the WaveView.
     * */
    @NonNull
    private FrameLayout.LayoutParams getParams() {
            return new FrameLayout.LayoutParams(10,10);
    }

        /**
     * Remove callbacks and finish the activity, return back to MainActivity.
     * */
    private void gameOver(){
        mPulseGenerator.stop();
        mSampleGenerator.stop();
        mContainer.setOnTouchListener(null);
        finish();
    }


    /**
     * Handle MotionEvent event from mContainer layout.
     * */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        mLastX = (int)event.getX();
        mLastY = (int)event.getY();


        if (event.getAction() == MotionEvent.ACTION_UP){
            //user untouched the screen end game
            gameOver();
        }

        return true;
    }

    /**
     * When onSample() called need to check the color.
     * */
    @Override
    public void onSample() {
        checkColor();
    }

        /**
         * Override onStop() and finish the game, for example when the user pressed the HOME
         * button while the game is running.
         * */
        @Override
        protected void onStop() {
            super.onStop();
            gameOver();
        }

        /**
         * Override onDestroy() and remove callbacks.
         * */
        @Override
        protected void onDestroy() {
            super.onDestroy();
            mPulseGenerator.stop();
            mSampleGenerator.stop();
            mContainer.setOnTouchListener(null);
        }
}
