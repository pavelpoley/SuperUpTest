package com.superuptest.activities;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.superuptest.R;
import com.superuptest.views.WaveView;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";

    private ConstraintLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mContainer = findViewById(R.id.cc_container);
        mContainer.setDrawingCacheEnabled(true);


        /*
        * just fot testing
        * */


        final WaveView wave = new WaveView(this);
        wave.setLayoutParams(new FrameLayout.LayoutParams(10,10));
        wave.getLayoutParams().width =10;
        wave.getLayoutParams().height = 10;

        mContainer.addView(wave);

        ConstraintSet c = new ConstraintSet();
        c.clone(mContainer);
        c.connect(wave.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        c.connect(wave.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
        c.connect(wave.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
        c.connect(wave.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);        c.applyTo(mContainer);
        c.applyTo(mContainer);

        ValueAnimator animator = ValueAnimator.ofInt(0,800);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int newRadius = (int) animation.getAnimatedValue();
                //Log.d(TAG, "onAnimationUpdate: " + newRadius);
                wave.getLayoutParams().width = (int) animation.getAnimatedValue();
                wave.getLayoutParams().height = (int) animation.getAnimatedValue();
                wave.requestLayout();
            }
        });
        animator.start();

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

}
