package com.superuptest.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
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
import com.superuptest.views.WaveView;

public class GameActivity extends AppCompatActivity implements WaveView.WaveViewCallbacks, PulseGenerator.PulseGeneratorCallbacks {

    private static final String TAG = "GameActivity";

    private TextView tvCount;
    private ConstraintLayout mContainer;
    private int mCount = 0;
    private PulseGenerator pulseGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mContainer = findViewById(R.id.cc_container);
        tvCount = findViewById(R.id.tv_count);

        mContainer.setDrawingCacheEnabled(true);

        pulseGenerator = new PulseGenerator(this);

        pulseGenerator.start();



        mContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int)event.getX();
                int y = (int)event.getY();


                // x and y are the location of the touch event in Bitmap space
                int color = mContainer.getDrawingCache().getPixel(x,y);

                //int alpha = Color.alpha(color);
                //boolean isTransparent = (alpha==0);

                Log.d(TAG, "onTouchEvent: " + color);
                return true;
            }
        });
    }

    @Override
    public void onWaveReachedBorder(WaveView wave) {
        mCount++;

        if (tvCount!=null)
            tvCount.setText("Count: " + mCount);

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

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        pulseGenerator.stop();
    }
}
