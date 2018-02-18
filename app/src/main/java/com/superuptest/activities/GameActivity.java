package com.superuptest.activities;

import android.os.Bundle;
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

public class GameActivity extends AppCompatActivity implements
        WaveView.WaveViewCallbacks,
        PulseGenerator.PulseGeneratorCallbacks,
        View.OnTouchListener, SamplingGenerator.SamplingGeneratorCallbacks {

    private static final String TAG = "GameActivity";

    private TextView tvCount;
    private ConstraintLayout mContainer;
    private int mCount = 0;

    private PulseGenerator mPulseGenerator;
    private SamplingGenerator mSampleGenerator;

    private int mLastX = 0;
    private int mLastY = 0;

    private static final int COLOR_WHITE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mContainer = findViewById(R.id.cc_container);
        tvCount = findViewById(R.id.tv_count);

        mContainer.setDrawingCacheEnabled(true);
        mContainer.setOnTouchListener(this);

        mPulseGenerator = new PulseGenerator(this);
        mSampleGenerator = new SamplingGenerator(this);

        mPulseGenerator.start();
        mSampleGenerator.start();

    }

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

    @Override
    public void onWaveReachedBorder(WaveView wave) {
        mCount++;

        if (tvCount!=null)
            tvCount.setText("Points: " + mCount);

        if (mContainer!=null) {
            mContainer.removeView(wave);
        }
    }

    @Override
    public void onPulse() {
        createWave();
    }


    private void createWave(){
        final WaveView wave = new WaveView(this);
        wave.setLayoutParams(new FrameLayout.LayoutParams(10,10));
        wave.setDeviceScreenWidth(getWindowManager().getDefaultDisplay().getWidth());
        wave.getLayoutParams().width =10;
        wave.getLayoutParams().height = 10;

        mContainer.addView(wave);

        ConstraintSet c = new ConstraintSet();
        c.clone(mContainer);
        c.connect(wave.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        c.connect(wave.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
        c.connect(wave.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
        c.connect(wave.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        c.applyTo(mContainer);

    }


    private void gameOver(){
        mPulseGenerator.stop();
        mSampleGenerator.stop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPulseGenerator.stop();
        mSampleGenerator.stop();
    }

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

    @Override
    public void onSample() {
        checkColor();
    }
}
