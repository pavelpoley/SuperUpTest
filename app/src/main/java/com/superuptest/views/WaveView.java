package com.superuptest.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;


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

    private Paint mCirclePaint;
    private Paint mHolePaint;
    private PorterDuffXfermode mDuffXfermode;

    public WaveView(Context context) {
        super(context);
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

        //PorterDuff.Mode.CLEAR doesn't work with hardware acceleration so nee to add this line
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
    }

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
     * This function take height, width and radius of the view and return coordinates
     * that represent point on the circle
     *
     * @param radius The radius of the our circle
     * @param mWidth The width of entire view
     * @param mHeight The height of entire view
     *
     * @return float array where result[0] is 'x' and result[1] is 'y'
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
}
