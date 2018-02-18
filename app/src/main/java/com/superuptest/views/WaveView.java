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
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.Random;


/**
 * This class represent a wave.
 */

public class WaveView extends View {

    /**
     * The logging tag of this Class
     * */
    private static final String TAG = "WaveView";

    /**
     *Padding from circle to view edge.
    * */
    private static final int PADDING = 10;


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
     *since the outer circle width is match to the screen width.
     * */
    private int deviceScreenWidth = -1;

    /**
     * Flag that help us to start animation only once.
     * */
    private boolean isAnimStarted = false;

    /**
    * Every item of the array represent a hole angel, angel will used to calculate x,y
    * coordinates for each hole on the circle.
    * */
    private int[] holeAngelsArr;


    /**
     * Every item of the array represent a hole size radius, will used by Paint to draw hole.
     * */
    private int[] holesSizeArr;


    //Other properties
    private Paint mCirclePaint;
    private Paint mHolePaint;
    private ValueAnimator mAnimator;
    private Random mRand = new Random();


    /**
     * int that represent number of holes in the wave.
     * */
    private final int numOfHoles = generateNumOfHoles();



    /**
     * Interface instance used to send callbacks to Activity.
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
     * initialize Paints, holes, angels, hole sizes and sets View characteristics.
    * */
    private void init(){
        //get paints
        mCirclePaint = getCirclePaint();
        mHolePaint = getHolePaint();

        setDrawingCacheEnabled(true);

        //this line used to force PorterDuff.Mode.CLEAR work and for smooth animations
        setLayerType(View.LAYER_TYPE_HARDWARE,null);

        setupHoleAngels();
        setupHolesSizes();
    }

    /**
     * Initialize holeAngelsArr that store random non-repeatable angels for each hole
     * */
    private void setupHoleAngels() {
        holeAngelsArr = new int[numOfHoles];

        for (int i = 0; i < holeAngelsArr.length; i++) {
            holeAngelsArr[i] = getRandAngel();

            //while loop to generate new number if the number already exist
            while (i > 0 && holeAngelsArr[i]== holeAngelsArr[i-1]){
                holeAngelsArr[i] = getRandAngel();
            }
        }
    }


    /**
     * Initialize holesSizeArr that store random hole size radius for each hole.
     * */
    private void setupHolesSizes() {
        holesSizeArr = new int[numOfHoles];

        for (int i = 0; i < holesSizeArr.length; i++) {
            holesSizeArr[i] = getRandHoleSize();
        }
    }


    /**
     * Override onMeasure and setup ValueAnimator increase from 10 to screen width
     * and start animation.
     * @throws IllegalStateException if the screen width not defined (-1)
     * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (deviceScreenWidth != -1){

            mAnimator = ValueAnimator.ofInt(10,deviceScreenWidth);

            //start animation
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

        //get width and height sizes
        int width = canvas.getWidth();
        int height = canvas.getHeight();


        //Draws the main circle
        canvas.drawCircle(
                (float)(width*0.5),
                (float)(height*0.5)
                ,(float)(width*0.5)-PADDING,
                mCirclePaint);

        //Draw the holes
        for (int i = 0; i< holeAngelsArr.length; i++) {

            //compute coordinates on the circle to place a hole
            float[] floats = getPointOnCircle(width, height, width/2, holeAngelsArr[i]);

            canvas.drawCircle(
                    floats[0],//x
                    floats[1],//y
                    holesSizeArr[i], mHolePaint);

            //because we using animation we must call invalidate in onDraw()
            //otherwise we won't able to getPixel() correctly
            invalidate();
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

                    //redraw the view
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
                //Animation end = wave reached the border, send callback to Activity.
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
        mAnimator.setDuration(ANIM_DURATION);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(getAnimUpdateListener());
        mAnimator.addListener(getAnimListener());
        mAnimator.start();
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
     * Return random number from 1 to 3, represent number of holes in WaveView.
     * @return random number from 1 to 3, represent number of holes in the WaveView.
     */
    private int generateNumOfHoles(){

        int low = 1;
        int high = 4;

        return mRand.nextInt(high-low) + low;
    }


    /**
    * Return random number from (1 to 36) * 10 that represent angel.
    * @return random number from (1 to 36) * 10 that represent angel from 10 to 360 degrees,
    * jumps by 10.
    */
    private int getRandAngel(){

        int low = 1;
        int high = 37;
        int result = mRand.nextInt(high-low) + low;

        return result*10;
    }


    /**
     * Return random number from 50 to 100 that represent hole size.
     * @return random number from 50 to 100 that represent hole size
     */
    private int getRandHoleSize(){

        int low = 5;
        int high = 11;

        int result = mRand.nextInt(high-low) + low;
        return result*10;
    }


}
