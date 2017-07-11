package com.tamalsamui.animatingcalendar.custom.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Tamal samui on 9/21/2016.
 */
public class CircleView extends View {

    private float cell_height, cell_width, radius;
    private Paint circlePaint,textPaint;
    private String dayToPrint;

    public CircleView(Context context, float cell_height, float cell_width,
                      float radius, Paint circlePaint, Paint textPaint, String dayToPrint) {
        super(context);
        this.cell_height = cell_height;
        this.cell_width = cell_width;
        this.radius = radius;
        this.circlePaint = circlePaint;
        this.textPaint = textPaint;
        this.dayToPrint = dayToPrint;
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw circle
        canvas.drawCircle(cell_width/2, cell_height/2, radius, circlePaint);

        //draw text
        String drawingDate = String.valueOf(dayToPrint);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        Rect bounds = new Rect();
        textPaint.getTextBounds(drawingDate, 0, drawingDate.length(), bounds);

        canvas.drawText(drawingDate, cell_width/2 - (bounds.right - bounds.left)/2,
                cell_height/2 + (bounds.bottom - bounds.top)/2,
                textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, resolveSize((int)((cell_height * 10)), heightMeasureSpec));
    }
}
