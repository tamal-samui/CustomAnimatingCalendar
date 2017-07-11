package com.tamalsamui.animatingcalendar.custom.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.tamalsamui.animatingcalendar.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Tamal samui on 9/19/2016.
 */
public class CustomCalendar extends View {
    float cell_width = getResources().getDimension(R.dimen.calendar_view_margin);
    float cell_height = getResources().getDimension(R.dimen.calendar_view_margin);
    private static final float NO_OF_DAYS = 7;
    private static final float NO_OF_WEEKS = 5;
    private static final int MAX_CLICK_DURATION = 400;

    private float FIXED_MARGIN = 0; //getResources().getDimension(R.dimen.calendar_view_margin);
    private String WEEK_DAYS[] = {"S","M","T","W","T","F","S"};
    private Map<String,String> datePostionMap = new HashMap<>();
    private Paint circlePaint, textPaint;

    private int touchedRow = -1;
    private int touchedColumn = -1;
    private int drawingYear = -1;
    private int drawingMonth = -1;
    private boolean isTouchListenerEnabled = true;
    private DateClickedListener parentAnimationListener;

    //animation fields
    private boolean hideClickedDate = false;
    private int clickedDate = 0;

    public CustomCalendar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCalendar(Context context) {
        super(context);
    }

    public void setDate(int year, int month){
        if(month<0 || month>11){
            return;
        }
        drawingMonth = month;
        drawingYear = year;
        invalidate();
    }

    public void setAnimationListener(DateClickedListener listener){
        parentAnimationListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        cell_width = (getWidth() - FIXED_MARGIN*2) / NO_OF_DAYS;
        cell_height = cell_width;

        //Use cell paint to color each cell background
        /*Paint cellPaint = new Paint();
        cellPaint.setStyle(Paint.Style.STROKE);
        cellPaint.setAntiAlias(true);
        cellPaint.setStrokeWidth(2);
        cellPaint.setColor(getResources().getColor(android.R.color.black));*/

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setTextSize(getResources().getDimension(R.dimen.text_size_15dp));
        textPaint.setColor(getResources().getColor(R.color.text_color));

        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeWidth(2);
        circlePaint.setColor(getResources().getColor(R.color.background));

        //draw days name.. i.e. Sun, Mon .... Sat
        drawDaysOfTheWeek(canvas, textPaint);

        //draw dates
        drawDates(canvas, textPaint, circlePaint);
    }

    private void drawDaysOfTheWeek(Canvas canvas, Paint textPaint){
        for(int i=0; i< NO_OF_DAYS; i++){
            float left = FIXED_MARGIN + i*cell_width;
            //draw cells
            //canvas.drawRect(left, 0, (FIXED_MARGIN + (i+1)*cell_width), cell_height, cellPaint);

            //draw text
            if(i < WEEK_DAYS.length) {
                Rect bounds = new Rect();
                textPaint.getTextBounds(WEEK_DAYS[i], 0, WEEK_DAYS[i].length(), bounds);


                canvas.drawText(WEEK_DAYS[i], left + cell_width/2 - (bounds.right - bounds.left)/2,
                        cell_height/2 + (bounds.bottom - bounds.top)/2,
                        textPaint);
            }
        }
    }

    private void drawDates(Canvas canvas, Paint textPaint, Paint circlePaint){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int currentDate = calendar.get(Calendar.DATE);
        boolean isDrawingCurrentMonth = false;

        //calculate the "day" of 1st day of the month
        if(drawingMonth >= 0  && drawingYear > 0){
            //check whether current month
            if(calendar.get(Calendar.YEAR) == drawingYear && calendar.get(Calendar.MONTH) == drawingMonth){
                isDrawingCurrentMonth = true;
            } else {
                calendar.set(Calendar.YEAR, drawingYear);
                calendar.set(Calendar.MONTH, drawingMonth);
            }
        }else {
            isDrawingCurrentMonth = true;
        }
        calendar.set(Calendar.DATE, 1);
        int start_day_of_week = calendar.get(Calendar.DAY_OF_WEEK); // sunday is 1 and saturday is 7

        int dayToPrint = 1;

        //calculate the last date of the month
        int last_day_of_the_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int week_no = 1; week_no <= NO_OF_WEEKS && dayToPrint <= last_day_of_the_month; week_no++) {
            for(int day_of_week=0; day_of_week < NO_OF_DAYS; day_of_week++){

                float left = FIXED_MARGIN + day_of_week*cell_width;
                float right = FIXED_MARGIN + (day_of_week + 1)*cell_width;
                float top = week_no * cell_height;

                //draw cells
                /*canvas.drawRect(left, top, right,(top + cell_height), cellPaint);*/

                //check whether text needs to drawn...
                if(week_no == 1 && day_of_week < start_day_of_week-1){ // before the 1st day of the month
                    continue;
                }else if(dayToPrint > last_day_of_the_month){ // after the last day of the month
                    continue;
                }else if(hideClickedDate && dayToPrint == clickedDate){ // before animation hide the date
                    dayToPrint ++;
                    continue;
                }

                //draw background circle if touched
                if (touchedRow == week_no && touchedColumn == day_of_week) {
                    int radius = 0;
                    if(cell_height<cell_width){
                        radius = (int)cell_height/2;
                    } else{
                        radius = (int)cell_width/2;
                    }
                    canvas.drawCircle(left + cell_width/2, top + cell_height/2, radius, circlePaint);
                }

                //draw text
                String drawingDate = String.valueOf(dayToPrint);
                if(isDrawingCurrentMonth && dayToPrint == currentDate){
                    textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                }else {
                    textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                }
                Rect bounds = new Rect();
                textPaint.getTextBounds(drawingDate, 0, drawingDate.length(), bounds);

                canvas.drawText(drawingDate, left + cell_width/2 - (bounds.right - bounds.left)/2,
                        top + cell_height/2 + (bounds.bottom - bounds.top)/2,
                        textPaint);
                datePostionMap.put(String.valueOf(week_no)+","+ String.valueOf(day_of_week), String.valueOf(dayToPrint));
                dayToPrint ++;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, resolveSize((int)((cell_height * 10) + (FIXED_MARGIN*2)), heightMeasureSpec));
    }

    private long startClickTime;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isTouchListenerEnabled){
            return true;
        }

        int eventAction = event.getAction();

        // you may need the x/y location
        int x = (int)event.getX();
        int y = (int)event.getY();

        // put your code in here to handle the event
        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                //Log.d("Tamal", "touched X= "+x);
                touchedRow = (y/(int)cell_height);
                touchedColumn = (x/(int)cell_width);
                startClickTime = Calendar.getInstance().getTimeInMillis();
                break;
            case MotionEvent.ACTION_UP:
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                String date = datePostionMap.get(String.valueOf(touchedRow)+","+ String.valueOf(touchedColumn));
                if(clickDuration < MAX_CLICK_DURATION && !TextUtils.isEmpty(date)) {
                    //Toast.makeText(getContext(),"Clicked "+date, Toast.LENGTH_SHORT).show();
                    doActionOnClick(date);
                    clickedDate = Integer.parseInt(date);
                }
                touchedColumn = -1;
                touchedRow = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }

        // tell the View to redraw the Canvas
        invalidate();

        // tell the View that we handled the event
        return true;
    }

    private void doActionOnClick(String date){
        if(parentAnimationListener != null){
            float left = FIXED_MARGIN + touchedColumn * cell_width;
            float top = touchedRow * cell_height;
            int radius = 0;
            if(cell_height<cell_width){
                radius = (int)cell_height/2;
            } else{
                radius = (int)cell_width/2;
            }
            parentAnimationListener.startDateAnimation(left, top, cell_height, cell_width,
                    radius, circlePaint, textPaint, date);
        }
    }

    //Animation
    public interface DateClickedListener {
        public void startDateAnimation(float left, float top, float cell_height, float cell_width,
                                       float radius, Paint circlePaint, Paint textPaint, String dayToPrint);
    }

    public void hideClickedDate(){
        hideClickedDate = true;
    }

    public void showHiddenDates(){
        hideClickedDate = false;
    }

    public void enableTouchListener(){
        isTouchListenerEnabled = true;
    }

    public void disableTouchListener(){
        isTouchListenerEnabled = false;
    }
}
