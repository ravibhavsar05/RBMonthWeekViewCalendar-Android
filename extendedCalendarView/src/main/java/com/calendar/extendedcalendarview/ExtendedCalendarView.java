package com.calendar.extendedcalendarview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class ExtendedCalendarView extends RelativeLayout implements
        OnItemClickListener, OnClickListener {

    public ImageView extended_prev_arrow;
    public ImageView extended_next_arrow;
    public TextView extended_month;
    public GridView extended_day_name_grid_view;
    public GridView extended_day_grid_view;
    public RelativeLayout extended_month_arrow_relative;
    private Context context;
    private OnDayClickListener dayListener;
    private CalendarAdapter mAdapter;
    private Calendar cal;
    private int gestureType = 0;
    private final GestureDetector calendarGesture = new GestureDetector(
            context, new GestureListener());
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    boolean isWeekView = false;
    int currentWeekNumber;
    int currentYear;
    LayoutInflater mInflater;

    public interface OnDayClickListener {
        public void onDayClicked(AdapterView<?> adapter, View view,
                                 int position, long id, Day day);
    }

    public interface OnTochListener {
        public void onTouchClicked(View v, MotionEvent event);
    }

    public ExtendedCalendarView(Context context) {
        super(context);
        this.context = context;
        mInflater = LayoutInflater.from(context);
        init();
    }

    public ExtendedCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mInflater = LayoutInflater.from(context);
        init();
    }

    public ExtendedCalendarView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        mInflater = LayoutInflater.from(context);
        init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        cal = Calendar.getInstance();
        View v = mInflater.inflate(R.layout.extendedcalender_view_main_layout,
                null);
        this.addView(v);
        extended_prev_arrow = (ImageView) v
                .findViewById(R.id.extended_prev_arrow);
        extended_next_arrow = (ImageView) v
                .findViewById(R.id.extended_next_arrow);
        extended_prev_arrow.setOnClickListener(this);
        extended_next_arrow.setOnClickListener(this);
        extended_month = (TextView) v.findViewById(R.id.extended_month);
        extended_day_name_grid_view = (GridView) v
                .findViewById(R.id.extended_day_name_grid_view);
        extended_day_grid_view = (GridView) v
                .findViewById(R.id.extended_day_grid_view);
        extended_month_arrow_relative = (RelativeLayout) v
                .findViewById(R.id.extended_month_arrow_relative);
        currentWeekNumber = cal.get(Calendar.WEEK_OF_YEAR);
        currentYear = cal.get(Calendar.YEAR);
        // =============================================
        String[] data = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        extended_day_name_grid_view.setAdapter(new ArrayAdapter<String>(
                context, R.layout.extended_textview, R.id.abcd, data));
        mAdapter = new CalendarAdapter(context, cal);
        extended_day_grid_view.setAdapter(mAdapter);
        extended_day_grid_view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return calendarGesture.onTouchEvent(event);
            }
        });
        extended_month.setText(cal.getDisplayName(Calendar.MONTH,
                Calendar.LONG, Locale.getDefault())
                + " "
                + cal.get(Calendar.YEAR));
    }

    public void setCurrentWeek(int currentWeekNumber, int currentYear) {
        // this.cal = cal;
        this.currentWeekNumber = currentWeekNumber;
        this.currentYear = currentYear;

    }

    public void setWeekView(boolean isWeekView) {
        this.isWeekView = isWeekView;
        rebuildCalendar();
    }

    public Calendar getCalendar() {
        return this.cal;
    }

    private class GestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (isWeekView) {
                    nextWeek();
                } else {
                    nextMonth();
                }
                return true; // Right to left
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (isWeekView) {
                    previousWeek();
                } else {
                    previousMonth();
                }
                return true; // Left to right
            } else {
            }
            return false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (dayListener != null) {
            Day d = (Day) mAdapter.getItem(arg2);
            if (d.getDay() != 0) {
                dayListener.onDayClicked(arg0, arg1, arg2, arg3, d);
            }
        }
    }

    /**
     * @param listener Set a listener for when you press on a day in the month
     */
    public void setOnDayClickListener(OnDayClickListener listener) {
        if (extended_day_grid_view != null) {
            dayListener = listener;
            extended_day_grid_view.setOnItemClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.extended_prev_arrow) {
            if (isWeekView) {
                extended_month.setText(cal.getDisplayName(Calendar.MONTH,
                        Calendar.LONG, Locale.getDefault())
                        + " "
                        + cal.get(Calendar.YEAR));
                previousWeek();
            } else {
                extended_month.setText(cal.getDisplayName(Calendar.MONTH,
                        Calendar.LONG, Locale.getDefault())
                        + " "
                        + cal.get(Calendar.YEAR));
                previousMonth();
            }

        } else if (i == R.id.extended_next_arrow) {
            if (isWeekView) {
                extended_month.setText(cal.getDisplayName(Calendar.MONTH,
                        Calendar.LONG, Locale.getDefault())
                        + " "
                        + cal.get(Calendar.YEAR));
                nextWeek();
            } else {
                extended_month.setText(cal.getDisplayName(Calendar.MONTH,
                        Calendar.LONG, Locale.getDefault())
                        + " "
                        + cal.get(Calendar.YEAR));
                nextMonth();
            }

        } else {
        }
    }

    private void previousMonth() {
        if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
            cal.set((cal.get(Calendar.YEAR) - 1),
                    cal.getActualMaximum(Calendar.MONTH), 1);
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        }
        rebuildCalendar();
    }

    private void nextMonth() {
        if (cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
            cal.set((cal.get(Calendar.YEAR) + 1),
                    cal.getActualMinimum(Calendar.MONTH), 1);
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        }
        rebuildCalendar();
    }

    public void nextYear(int Year, int Month) {
        cal.set(Calendar.YEAR, Year);
        cal.set(Calendar.MONTH, Month - 1);
        rebuildCalendar();
    }

    public void nextWeek() {
        if (currentWeekNumber == 52) {
            currentWeekNumber = 1;
            currentYear += 1;
            cal.set(Calendar.WEEK_OF_YEAR, currentWeekNumber);
            cal.set(Calendar.YEAR, currentYear);

        } else {
            ++currentWeekNumber;
            cal.set(Calendar.WEEK_OF_YEAR, currentWeekNumber);
            cal.set(Calendar.YEAR, currentYear);

        }
        setCurrentWeek(currentWeekNumber, currentYear);
        setWeekView(isWeekView);
    }

    public void previousWeek() {
        if (currentWeekNumber == 0) {
            currentWeekNumber = 52;
            currentYear -= 1;
            cal.set(Calendar.WEEK_OF_YEAR, currentWeekNumber);
            cal.set(Calendar.YEAR, currentYear);

        } else {
            --currentWeekNumber;
            cal.set(Calendar.WEEK_OF_YEAR, currentWeekNumber);
            cal.set(Calendar.YEAR, currentYear);
        }
        setCurrentWeek(currentWeekNumber, currentYear);
        setWeekView(isWeekView);
    }

    private void rebuildCalendar() {
        if (extended_month != null) {
            extended_month.setText(cal.getDisplayName(Calendar.MONTH,
                    Calendar.LONG, Locale.getDefault())
                    + " "
                    + cal.get(Calendar.YEAR));
            refreshCalendar();
        }
    }

    /**
     * Refreshes the month
     */
    public void refreshCalendar() {
        mAdapter.setWeekView(isWeekView, currentWeekNumber, currentYear);
        mAdapter.refreshDays(cal);
        mAdapter.notifyDataSetChanged();

    }

    /**
     * @param color Sets the background color of the month bar
     */
    public void setMonthTextBackgroundColor(int color) {
        extended_month_arrow_relative.setBackgroundColor(color);
    }

    @SuppressLint("NewApi")
    /**
     *
     * @param drawable
     *
     * Sets the background color of the month bar. Requires at least API level 16
     */
    public void setMonthTextBackgroundDrawable(Drawable drawable) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            extended_month_arrow_relative.setBackgroundDrawable(drawable);
        }

    }

    /**
     * @param resource Sets the background color of the month bar
     */
    public void setMonehtTextBackgroundResource(int resource) {
        extended_month_arrow_relative.setBackgroundResource(resource);
    }

    /**
     * @param recource change the image of the previous month button
     */
    public void setPreviousMonthButtonImageResource(int recource) {
        extended_prev_arrow.setImageResource(recource);
    }

    /**
     * @param bitmap change the image of the previous month button
     */
    public void setPreviousMonthButtonImageBitmap(Bitmap bitmap) {
        extended_prev_arrow.setImageBitmap(bitmap);
    }

    /**
     * @param drawable change the image of the previous month button
     */
    public void setPreviousMonthButtonImageDrawable(Drawable drawable) {
        extended_prev_arrow.setImageDrawable(drawable);
    }

    /**
     * @param recource change the image of the next month button
     */
    public void setNextMonthButtonImageResource(int recource) {
        extended_next_arrow.setImageResource(recource);
    }

    /**
     * @param bitmap change the image of the next month button
     */
    public void setNextMonthButtonImageBitmap(Bitmap bitmap) {
        extended_next_arrow.setImageBitmap(bitmap);
    }

    /**
     * @param drawable change the image of the next month button
     */
    public void setNextMonthButtonImageDrawable(Drawable drawable) {
        extended_next_arrow.setImageDrawable(drawable);
    }

    /**
     * @param gestureType Allow swiping the calendar left/right or up/down to change the
     *                    month.
     *                    <p>
     *                    Default value no gesture
     */
    public void setGesture(int gestureType) {
        this.gestureType = gestureType;
    }

}
