package com.tamalsamui.animatingcalendar;

import android.app.Fragment;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tamalsamui.animatingcalendar.custom.views.CircleView;
import com.tamalsamui.animatingcalendar.custom.views.CustomCalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Tamal samui on 9/20/2016.
 */
public class CalendarFragment extends Fragment implements CustomCalendar.DateClickedListener {

    private CustomCalendar customCalendar;
    private TextView monthName, actionBarHeader;
    private ImageView nextMonth, prevMonth;
    private RelativeLayout parentLayout;
    private ListView listView;

    private Calendar calendar;
    private CircleView circleView;
    private LinearLayout calendar_header, action_bar, list_layout;
    private float circleViewLeftMargin = 0;
    private float circleViewTopMargin = 0;
    private float circleViewYdelta = 0;
    private float actionBarHeaderXdelta = 0;
    private String month_year_name = "";

    private boolean backPressHandleNeeded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
//        monthToDraw = calendar.get(Calendar.MONTH);
//        yearToDraw = calendar.get(Calendar.YEAR);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_widget_layout, container, false);

        customCalendar = (CustomCalendar) view.findViewById(R.id.custom_calendar);
        customCalendar.setAnimationListener(this);
        monthName = (TextView) view.findViewById(R.id.tv_month_name);
        nextMonth = (ImageView) view.findViewById(R.id.iv_next);
        prevMonth = (ImageView) view.findViewById(R.id.iv_prev);
        parentLayout = (RelativeLayout) view.findViewById(R.id.parent_layout);
        calendar_header = (LinearLayout) view.findViewById(R.id.calendar_head);
        action_bar = (LinearLayout) view.findViewById(R.id.action_bar1);
        actionBarHeader = (TextView) view.findViewById(R.id.tv_action_bar_header);
        list_layout = (LinearLayout) view.findViewById(R.id.list_layout);
        listView = (ListView) view.findViewById(R.id.day_list);

        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                setDate();
            }
        });

        prevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                setDate();
            }
        });

        setDate();
        return view;
    }

    private void setDate(){
        customCalendar.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy");
        month_year_name = dateFormat.format(calendar.getTime());
        monthName.setText(month_year_name);
    }


    @Override
    public void startDateAnimation(float left, float top, float cell_height, float cell_width,
                                   float radius, Paint circlePaint, Paint textPaint, String dayToPrint) {

        backPressHandleNeeded = true;

        //draw circle..
        if(circleView != null){
            parentLayout.removeView(circleView);
        }
        circleView = new CircleView(getActivity(), cell_height, cell_width,
                     radius, circlePaint, textPaint, dayToPrint);

        circleViewTopMargin = top + action_bar.getHeight() + calendar_header.getHeight();
        circleViewLeftMargin = left;
        circleViewYdelta = circleViewTopMargin - (action_bar.getHeight()/2) + radius;
        actionBarHeaderXdelta = cell_width - actionBarHeader.getPaddingLeft();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((int)circleViewLeftMargin,(int)circleViewTopMargin,0,0);
        parentLayout.addView(circleView, layoutParams);

        //hide the date from the calendar
        customCalendar.hideClickedDate();

        //start animation
        animateViewToActionBar();
    }

    private void animateViewToActionBar(){
        //Animate Circle View;
        TranslateAnimation viewToActionBarAnimation = new TranslateAnimation(0, -circleViewLeftMargin, 0, -circleViewYdelta);
        viewToActionBarAnimation.setDuration(300);
        viewToActionBarAnimation.setFillAfter(true);
        circleView.startAnimation(viewToActionBarAnimation);
        customCalendar.disableTouchListener();

        //set action bar text
        actionBarHeader.setTypeface(Typeface.create(actionBarHeader.getTypeface(), Typeface.NORMAL));
        actionBarHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_15dp));
        actionBarHeader.setText(month_year_name);

        viewToActionBarAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                showAndPopulateList();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                list_layout.setVisibility(View.VISIBLE);
                customCalendar.enableTouchListener();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        //Animate Action bar header
        TranslateAnimation actionBarHeaderAnimation = new TranslateAnimation(0, actionBarHeaderXdelta, 0, 0);
        actionBarHeaderAnimation.setDuration(200);
        actionBarHeaderAnimation.setFillAfter(true);
        actionBarHeader.startAnimation(actionBarHeaderAnimation);
    }

    public void startReverseDateAnimation(){
        animateActionBarToCalendar();
    }

    private void animateActionBarToCalendar(){
        TranslateAnimation viewToActionBarAnimation = new TranslateAnimation(-circleViewLeftMargin, 0, -circleViewYdelta, 0);
        viewToActionBarAnimation.setDuration(200);
        viewToActionBarAnimation.setFillAfter(true);
        circleView.startAnimation(viewToActionBarAnimation);
        viewToActionBarAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                list_layout.setVisibility(View.GONE);
                actionBarHeader.setTypeface(Typeface.create(actionBarHeader.getTypeface(), Typeface.BOLD));
                actionBarHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_18dp));
                actionBarHeader.setText("Calendar");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //show the hidden date from the calendar
                customCalendar.showHiddenDates();
                customCalendar.invalidate();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        parentLayout.removeView(circleView);
                    }
                },200);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        //Animate Action bar header
        TranslateAnimation actionBarHeaderAnimation = new TranslateAnimation(actionBarHeaderXdelta, 0, 0, 0);
        actionBarHeaderAnimation.setDuration(200);
        actionBarHeaderAnimation.setFillAfter(true);
        actionBarHeader.startAnimation(actionBarHeaderAnimation);
    }

    private void showAndPopulateList(){
        listView.setAdapter(new ListAdapter());
    }

    class ListAdapter extends BaseAdapter {
        int hour = 0;
        @Override
        public int getCount() {
            return 24;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.hour = (TextView) convertView.findViewById(R.id.textView_hour);
                convertView.setTag(viewHolder);
            } else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.hour.setText(position + " Hour");
            return convertView;
        }
    }

    class ViewHolder{
        TextView hour;
    }

    public boolean handleBackPressed(){
        if(backPressHandleNeeded){
            startReverseDateAnimation();
            backPressHandleNeeded = false;
            customCalendar.enableTouchListener();
            return true;
        }
        return false;
    }
}
