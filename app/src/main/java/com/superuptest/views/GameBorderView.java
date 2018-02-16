package com.superuptest.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by pavel on 16/02/2018.
 */

public class GameBorderView extends View {

    private static final String TAG = "GameBorderView";

    private Paint mPaint;

    public GameBorderView(Context context) {
        super(context);
        init();
    }

    public GameBorderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){

        mPaint = getPaint();
        setDrawingCacheEnabled(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        //Draws the main circle border area of the game
        canvas.drawCircle(
                width/2,
                height/2
                ,width/2,
                mPaint);
    }

    private Paint getPaint(){
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(15);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }
}
