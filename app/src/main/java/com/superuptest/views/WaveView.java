package com.superuptest.views;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;


/**
 * This class represent a wave.
 */

public class WaveView extends View {

    private static final String TAG = "WaveView";

    /**
     *Padding from circle to view edge.
    * */
    private static final int PADDING = 10;

    /**
     *Radius of the hole.
     * */
    private static final int HOLE_RADIUS = 50;

    /**
     *Paints stroke width.
     * */
    private static final int STROKE_WIDTH = 15;

    /**
     *The duration of animation.
     * */
    private static final int ANIM_DURATION = 5000;

    /**
     *Device screen width used to check when the wave reached the border,
     *since the outer circle border width is match to the screen width.
     * */
    private int deviceScreenWidth = -1;

    /**
     * Triggers when the animation started.
     * */
    private boolean isAnimStarted = false;


    private Paint mCirclePaint;
    private Paint mHolePaint;
    private PorterDuffXfermode mDuffXfermode;
    private ValueAnimator animator ;

    /**
     * Interface instance to send callbacks to Activity.
     * */
    private WaveViewCallbacks mCallbacks;


    /**
     * Interface to communicate with the Activity
     * */
    public interface WaveViewCallbacks {

        /**
         * Called when the wave reached the border.
         * @param waveView provide a instance of itself, to let the layout container in the
         * Activity to remove the View.
         * */
        void onWaveReachedBorder(WaveView waveView);
    }

    public WaveView(Context context) {
        super(context);

        if (context instanceof WaveViewCallbacks){
            mCallbacks = (WaveViewCallbacks) context;
        }else {
            throw new IllegalStateException("Must implement WaveViewCallbacks interface");
        }

        init();
    }



    /**
     * initialize Paints and sets View characteristics.
    * */
    private void init(){
        //get paints
        mCirclePaint = getCirclePaint();
        mHolePaint = getHolePaint();

        //used to erase part of the circle to create hole
        mDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

        setDrawingCacheEnabled(true);

        //add this line to force PorterDuff.Mode.CLEAR work
        setLayerType(View.LAYER_TYPE_HARDWARE,null);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (deviceScreenWidth != -1){

            Log.d(TAG, "onMeasure: "+deviceScreenWidth);

            animator = ValueAnimator.ofInt(10,deviceScreenWidth);

            if (!isAnimStarted){
                startAnimation();
            }
        }else {
            throw new IllegalStateException("Screen width not defined");
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int width = canvas.getWidth();
        int height = canvas.getHeight();

        Log.d(TAG, "onDraw: "+width);


        //Draws the main circle
        canvas.drawCircle(
                (float)(width*0.5),
                (float)(height*0.5)
                ,(float)(width*0.5)-PADDING,
                mCirclePaint);

        //get coordinates on the circle to place hole
        float[] floats = getPointOnCircle(width, height, (float) (width * 0.5));

        //set paints clear mode
        mHolePaint.setXfermode(mDuffXfermode);

        //create new circle that erase part of the main circle
        canvas.drawCircle(
                floats[0],
                floats[1],
                HOLE_RADIUS, mHolePaint);
    }

    /**
     * Getter for AnimatorUpdateListener
     * */
    @NonNull
    private ValueAnimator.AnimatorUpdateListener getAnimUpdateListener() {
        return new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {

                    //increase the view width and height
                    getLayoutParams().width = (int) animation.getAnimatedValue();
                    getLayoutParams().height = (int) animation.getAnimatedValue();
                    requestLayout();

            }
        };
    }

    /**
     * Getter for AnimatorListener
     * */
    @NonNull
    private Animator.AnimatorListener getAnimListener() {
        return new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCallbacks.onWaveReachedBorder(WaveView.this);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    /**
     * This function take height, width and radius of the view and return coordinates
     * that represent point on the circle
     *
     * @param radius The radius of the our circle
     * @param mWidth The width of entire view
     * @param mHeight The height of entire view
     *
     * @return float 2-element array where result[0] is 'x' and result[1] is 'y'
     * that represent point on the circle,
     * this coordinates uses to place a hole
     * */
    private float[] getPointOnCircle(int mWidth, int mHeight, final float radius) {
        float[] result = new float[2];

        result[0] = (float) ((radius-PADDING) * Math.cos(100)) + (mWidth / 2);
        result[1] = (float) ((radius-PADDING) * Math.sin(100)) + (mHeight / 2);

        return result;
    }


    /**
    * Return Paint that uses to draw circle
    * @return Paint that uses to draw circle
    * */
    private Paint getCirclePaint(){
       Paint paint =  new Paint();
       paint.setColor(Color.BLACK);
       paint.setStrokeWidth(STROKE_WIDTH);
       paint.setStyle(Paint.Style.STROKE);
       return paint;
    }

    /**
     * Return Paint that uses to draw hole in circle
     * @return Paint that uses to draw hole in circle
     * */
    private Paint getHolePaint(){
        Paint paint =  new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }


    /**
     * Initialize the ValueAnimator and start animation as wave
     * */
    void startAnimation(){
        animator.setDuration(ANIM_DURATION);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(getAnimUpdateListener());
        animator.addListener(getAnimListener());
        animator.start();
        isAnimStarted = true;
    }

    /**
     * Set device screen width
     * @param width is screen width
     * */
    public void setDeviceScreenWidth(int width) {
        this.deviceScreenWidth = width;
    }
}
