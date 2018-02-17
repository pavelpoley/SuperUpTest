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

import java.util.Random;


/**
 * This class represent a wave.
 */

public class WaveView extends View {

    /**
     * Tha logging tag of this Class
     * */
    private static final String TAG = "WaveView";

    /**
     *Padding from circle to view edge.
    * */
    private static final int PADDING = 10;

    /**
     *Radius of the hole.
     * */
    private static final int HOLE_RADIUS = 60;

    /**
     *Paints stroke width.
     * */
    private static final int STROKE_WIDTH = 15;

    /**
     *The duration of animation in millisecond.
     * */
    private static final int ANIM_DURATION = 10000;

    /**
     *Device screen width used to check when the wave reached the border,
     *since the outer circle border width is match to the screen width.
     * */
    private int deviceScreenWidth = -1;

    /**
     * Triggers when the animation started.
     * */
    private boolean isAnimStarted = false;

    /**
    * Every item of the array represent a hole angel, angel will used to calculate x,y
    * coordinates for each hole on the circle.
    * */
    private int[] angelsArr;

    /**
     * Interface instance to send callbacks to Activity.
     * */
    private WaveViewCallbacks mCallbacks;


    /**
     * Interface to communicate with the Activity
     * */
    public interface WaveViewCallbacks {

        /**
         * Called when the wave reached the game border.
         * @param waveView provide a instance of itself, to let the layout container in the
         * Activity to remove the View.
         * */
        void onWaveReachedBorder(WaveView waveView);
    }


    private Paint mCirclePaint;
    private Paint mHolePaint;
    private ValueAnimator animator ;
    private Random mRand = new Random();



    /**
     * Default constructor
     * @param context The Context the view is running in (Activity)
     * @throws IllegalStateException if the Activity not implement WaveViewCallbacks interface
     * */
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
     * initialize Paints, holes, angels and sets View characteristics.
    * */
    private void init(){
        //get paints
        mCirclePaint = getCirclePaint();
        mHolePaint = getHolePaint();

        setDrawingCacheEnabled(true);

        //this line used to force PorterDuff.Mode.CLEAR work and for smooth animations
        setLayerType(View.LAYER_TYPE_HARDWARE,null);

        setupAngelsForHoles();

    }

    /**
     * Initialize angelsArr that will store random non-repeatable angels for each hole
     * angelsArr size is the number of holes generated randomly
     * */
    private void setupAngelsForHoles() {
        angelsArr = new int[generateNumOfHoles()];

        for (int i = 0; i < angelsArr.length; i++) {
            angelsArr[i] = generateRandomAngel();

            while (i > 0 && angelsArr[i]== angelsArr[i-1]){
                angelsArr[i] = generateRandomAngel();
            }
        }
    }

    /**
     * Override onMeasure and setup ValueAnimator related to screen width and start animation
     * @throws IllegalStateException if the screen width not defined (-1)
     * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (deviceScreenWidth != -1){

            animator = ValueAnimator.ofInt(10,deviceScreenWidth);

            if (!isAnimStarted){
                startAnimation();
            }
        }else {
            throw new IllegalStateException("Screen width not defined");
        }

    }


    /**
     * Override onDraw and draw the objects
     * */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int width = canvas.getWidth();
        int height = canvas.getHeight();


        //Draws the main circle
        canvas.drawCircle(
                (float)(width*0.5),
                (float)(height*0.5)
                ,(float)(width*0.5)-PADDING,
                mCirclePaint);

        //Draw the holes
        for (int i = 0; i< angelsArr.length; i++) {

            Log.d(TAG, "onDraw: " + angelsArr[i]);
            //compute coordinates on the circle to place a hole
            float[] floats = getPointOnCircle(width, height, width/2, angelsArr[i]);

            canvas.drawCircle(
                    floats[0],//x
                    floats[1],//y
                    HOLE_RADIUS, mHolePaint);
        }
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
    private float[] getPointOnCircle(int mWidth, int mHeight, final float radius, int angel) {
        float[] result = new float[2];

        result[0] = (float) ((radius-PADDING) * Math.cos(angel)) + (mWidth / 2);
        result[1] = (float) ((radius-PADDING) * Math.sin(angel)) + (mHeight / 2);

        return result;
    }


    /**
    * Return Paint that uses to draw circle
    * @return Paint that uses to draw circle
    * */
    private Paint getCirclePaint(){
       Paint paint =  new Paint();
       paint.setColor(Color.DKGRAY);
       paint.setStrokeWidth(STROKE_WIDTH);
       paint.setStyle(Paint.Style.STROKE);
       return paint;
    }

    /**
     * Return Paint that uses to draw hole in circle
     * @return Paint that uses to draw hole in circle
     * */
    private Paint getHolePaint(){

        //mDuffXfermode to erase part of the circle to create hole
        PorterDuffXfermode mDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

        Paint paint = new Paint();
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(mDuffXfermode);

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
     * Setter for device screen width
     * @param width is screen width
     * */
    public void setDeviceScreenWidth(int width) {
        this.deviceScreenWidth = width;
    }

    /**
     * Return random number from 1 to 3, represent number of holes in WaveView
     * @return random number from 1 to 3, represent number of holes in the WaveView
     */
    private int generateNumOfHoles(){

        int low = 1;
        int high = 4;

        return mRand.nextInt(high-low) + low;
    }


    /**
    * Return random number from (1 to 36) * 10 that represent angel
    * @return random number from (1 to 36) * 10 that represent angel
    */
    private int generateRandomAngel(){

        int low = 1;
        int high = 37;
        int result = mRand.nextInt(high-low) + low;

        return result*10;
    }
}
