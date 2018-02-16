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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.superuptest.R;
import com.superuptest.views.WaveView;

public class GameActivity extends AppCompatActivity implements WaveView.WaveViewCallbacks {

    private static final String TAG = "GameActivity";

    private TextView tvCount;
    private ConstraintLayout mContainer;
    private int mCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mContainer = findViewById(R.id.cc_container);
        mContainer.setDrawingCacheEnabled(true);

        tvCount = findViewById(R.id.tv_count);
        /*
        * just fot testing
        * */


        final WaveView wave = new WaveView(this);
        wave.setLayoutParams(new FrameLayout.LayoutParams(10,10));
        wave.setDeviceScreenWidth(getWindowManager().getDefaultDisplay().getWidth());
        wave.getLayoutParams().width =10;
        wave.getLayoutParams().height = 10;

        mContainer.addView(wave,1);

        //Log.d(TAG, "onCreate: width"+getWindowManager().getDefaultDisplay().getWidth());

        ConstraintSet c = new ConstraintSet();
        c.clone(mContainer);
        c.connect(wave.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        c.connect(wave.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
        c.connect(wave.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
        c.connect(wave.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);        c.applyTo(mContainer);
        c.applyTo(mContainer);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                final WaveView wave2 = new WaveView(GameActivity.this);
                wave2.setLayoutParams(new FrameLayout.LayoutParams(10,10));
                wave2.setDeviceScreenWidth(getWindowManager().getDefaultDisplay().getWidth());
                wave2.getLayoutParams().width =10;
                wave2.getLayoutParams().height = 10;

                mContainer.addView(wave2,2);

                //Log.d(TAG, "onCreate: width"+getWindowManager().getDefaultDisplay().getWidth());

                ConstraintSet c = new ConstraintSet();
                c.clone(mContainer);
                c.connect(wave2.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                c.connect(wave2.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                c.connect(wave2.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                c.connect(wave2.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);        c.applyTo(mContainer);
                c.applyTo(mContainer);


            }
        },1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                final WaveView wave2 = new WaveView(GameActivity.this);
                wave2.setLayoutParams(new FrameLayout.LayoutParams(10,10));
                wave2.setDeviceScreenWidth(getWindowManager().getDefaultDisplay().getWidth());
                wave2.getLayoutParams().width =10;
                wave2.getLayoutParams().height = 10;

                mContainer.addView(wave2,2);

                //Log.d(TAG, "onCreate: width"+getWindowManager().getDefaultDisplay().getWidth());

                ConstraintSet c = new ConstraintSet();
                c.clone(mContainer);
                c.connect(wave2.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                c.connect(wave2.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                c.connect(wave2.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                c.connect(wave2.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);        c.applyTo(mContainer);
                c.applyTo(mContainer);


            }
        },2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                final WaveView wave2 = new WaveView(GameActivity.this);
                wave2.setLayoutParams(new FrameLayout.LayoutParams(10,10));
                wave2.setDeviceScreenWidth(getWindowManager().getDefaultDisplay().getWidth());
                wave2.getLayoutParams().width =10;
                wave2.getLayoutParams().height = 10;

                mContainer.addView(wave2,2);

                //Log.d(TAG, "onCreate: width"+getWindowManager().getDefaultDisplay().getWidth());

                ConstraintSet c = new ConstraintSet();
                c.clone(mContainer);
                c.connect(wave2.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                c.connect(wave2.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                c.connect(wave2.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                c.connect(wave2.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);        c.applyTo(mContainer);
                c.applyTo(mContainer);


            }
        },3000);


        mContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int)event.getX();
                int y = (int)event.getY();


                Bitmap bitmap = mContainer.getDrawingCache();

                // x and y are the location of the touch event in Bitmap space
                int color = bitmap.getPixel(x,y);

                //int alpha = Color.alpha(color);
                //boolean isTransparent = (alpha==0);

                Log.d(TAG, "onTouchEvent: " + color);
                return true;
            }
        });
    }

    @Override
    public void onWaveReachedBorder(WaveView wave) {
        Log.d(TAG, "onWaveReachedBorder: " + mContainer.getChildCount());

        mContainer.removeView(wave);

        mCount++;

        if (tvCount!=null)
            tvCount.setText("Count: " + mCount);
    }
}
