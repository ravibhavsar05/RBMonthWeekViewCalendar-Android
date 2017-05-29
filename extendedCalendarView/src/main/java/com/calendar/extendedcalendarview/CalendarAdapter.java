package com.calendar.extendedcalendarview;

import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class CalendarAdapter extends BaseAdapter {

    static final int FIRST_DAY_OF_WEEK = 0;
    Context context;
    Calendar cal;
    Calendar calTemp;

    int iCheck = 0;
    public String[] days;
    // OnAddNewEventClick mAddEvent;
    ArrayList<Day> dayList = new ArrayList<Day>();

    boolean isWeekView = false;
    int currentWeekNumber;
    int currentYear;
    private static final String TAG = CalendarAdapter.class.getName();

    public CalendarAdapter(Context context, Calendar cal) {
        this.cal = cal;
        this.context = context;
        refreshDays(cal);
    }

    public void setWeekView(boolean isWeekView, int weekNumber, int year) {
        this.isWeekView = isWeekView;
        this.currentWeekNumber = weekNumber;
        this.currentYear = year;
    }

    @Override
    public int getCount() {
        return days.length;
    }

    @Override
    public Object getItem(int position) {
        return dayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public int getPrevMonth() {
        if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR - 1));
        } else {
        }
        int month = cal.get(Calendar.MONTH);
        if (month == 0) {
            return month = 11;
        }
        return month - 1;
    }

    public int getMonth() {
        return cal.get(Calendar.MONTH);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.modified_day_view, null);
        FrameLayout today = (FrameLayout) v.findViewById(R.id.today_frame);
        Calendar cal = Calendar.getInstance(TimeZone.getDefault(),
                Locale.getDefault());
        Day d = dayList.get(position);
        if (d.getYear() == cal.get(Calendar.YEAR)
                && d.getMonth() == cal.get(Calendar.MONTH)
                && d.getDay() == cal.get(Calendar.DAY_OF_MONTH)) {
            today.setVisibility(View.VISIBLE);
        } else {
            today.setVisibility(View.GONE);
        }
        TextView dayTV = (TextView) v.findViewById(R.id.textView1);
        RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.rl);
        ImageView iv = (ImageView) v.findViewById(R.id.imageView1);
        ImageView blue = (ImageView) v.findViewById(R.id.imageView2);
        ImageView purple = (ImageView) v.findViewById(R.id.imageView3);
        ImageView green = (ImageView) v.findViewById(R.id.imageView4);
        ImageView orange = (ImageView) v.findViewById(R.id.imageView5);
        ImageView red = (ImageView) v.findViewById(R.id.imageView6);
        blue.setVisibility(View.VISIBLE);
        purple.setVisibility(View.VISIBLE);
        green.setVisibility(View.VISIBLE);
        purple.setVisibility(View.VISIBLE);
        orange.setVisibility(View.VISIBLE);
        red.setVisibility(View.VISIBLE);
        iv.setVisibility(View.VISIBLE);
        dayTV.setVisibility(View.VISIBLE);
        rl.setVisibility(View.VISIBLE);
        Day day = dayList.get(position);
        if (day.getNumOfEvenets() >= 0) {
            Set<Integer> colors = day.getColors();
            iv.setVisibility(View.INVISIBLE);
            blue.setVisibility(View.INVISIBLE);
            purple.setVisibility(View.INVISIBLE);
            green.setVisibility(View.INVISIBLE);
            purple.setVisibility(View.INVISIBLE);
            orange.setVisibility(View.INVISIBLE);
            red.setVisibility(View.INVISIBLE);
            if (colors.contains(0)) {
                iv.setVisibility(View.VISIBLE);
            }
            if (colors.contains(2)) {
                blue.setVisibility(View.VISIBLE);
            }
            if (colors.contains(4)) {
                purple.setVisibility(View.VISIBLE);
            }
            if (colors.contains(5)) {
                green.setVisibility(View.VISIBLE);
            }
            if (colors.contains(3)) {
                orange.setVisibility(View.VISIBLE);
            }
            if (colors.contains(1)) {
                red.setVisibility(View.VISIBLE);
            }

        } else {
            iv.setVisibility(View.INVISIBLE);
            blue.setVisibility(View.INVISIBLE);
            purple.setVisibility(View.INVISIBLE);
            green.setVisibility(View.INVISIBLE);
            purple.setVisibility(View.INVISIBLE);
            orange.setVisibility(View.INVISIBLE);
            red.setVisibility(View.INVISIBLE);
        }
        if (day.getDay() == 0) {
            rl.setVisibility(View.GONE);
        } else {
            dayTV.setVisibility(View.VISIBLE);
            dayTV.setText(String.valueOf(day.getDay()));
        }
        return v;
    }


    public void refreshDays(Calendar cal) {
        // clear items
        this.cal = cal;
        cal.set(Calendar.DAY_OF_MONTH, 1);
        dayList.clear();
        if (isWeekView) {
            refreshWeekByCal();
        } else {
            refreshMonth();
        }
    }

    public void refreshMonth() {
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH) + 7;
        int firstDay = (int) cal.get(Calendar.DAY_OF_WEEK);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        TimeZone tz = TimeZone.getDefault();
        // figure size of the array
        if (firstDay == 1) {
            days = new String[lastDay + (FIRST_DAY_OF_WEEK * 6)];

        } else {
            days = new String[lastDay + firstDay - (FIRST_DAY_OF_WEEK + 1)];

        }
        int j = FIRST_DAY_OF_WEEK;
        // populate empty days before first real day
        if (firstDay > 1) {
            for (j = 0; j < (firstDay - FIRST_DAY_OF_WEEK) + 7; j++) {
                days[j] = "";
                Day d = new Day(context, 0, 0, 0);
                dayList.add(d);
            }
        } else {
            for (j = 0; j < (FIRST_DAY_OF_WEEK * 6) + 7; j++) {
                days[j] = "";
                Day d = new Day(context, 0, 0, 0);
                dayList.add(d);
            }
            j = FIRST_DAY_OF_WEEK * 6 + 1; // Sunday => 1, Monday => 7
        }
        // populate days
        int dayNumber = 1;
        if (j > 0 && dayList.size() > 0 && j != 1) {
            dayList.remove(j - 1);
        }
        for (int i = j - 1; i < days.length; i++) {
            Day d = new Day(context, dayNumber, year, month);
            Calendar cTemp = Calendar.getInstance();
            cTemp.set(year, month, dayNumber);
            int startDay = Time.getJulianDay(cTemp.getTimeInMillis(),
                    TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cTemp
                            .getTimeInMillis())));
            d.setAdapter(this);
            d.setStartDay(startDay);
            days[i] = "" + dayNumber;
            dayNumber++;
            dayList.add(d);
        }

    }

    int dayNumber = 1;

    public void refreshWeek() {
        // dayList.clear();
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_WEEK) + 7;
        int firstDay = (int) cal.get(Calendar.DAY_OF_WEEK);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        TimeZone tz = TimeZone.getDefault();
        // figure size of the array
        if (firstDay == 1) {
            days = new String[lastDay + (FIRST_DAY_OF_WEEK * 6)];
        } else {
            days = new String[lastDay + firstDay - (FIRST_DAY_OF_WEEK + 1)];
        }
        int j = FIRST_DAY_OF_WEEK;
        // populate empty days before first real day
        if (firstDay > 1) {
            for (j = 0; j < (firstDay - FIRST_DAY_OF_WEEK) + 7; j++) {
                days[j] = "";
                Day d = new Day(context, 0, 0, 0);
                dayList.add(d);
            }
        } else {
            for (j = 0; j < (FIRST_DAY_OF_WEEK * 6) + 7; j++) {
                days[j] = "";
                Day d = new Day(context, 0, 0, 0);
                dayList.add(d);
            }
            j = FIRST_DAY_OF_WEEK * 6 + 1; // Sunday => 1, Monday => 7
        }
        // populate days
        if (j > 0 && dayList.size() > 0 && j != 1) {
            dayList.remove(j - 1);
        }
        for (int i = 1; i < 8; i++) {
            if (dayNumber > 30) {
                dayNumber = 1;

            }
            Day d = new Day(context, dayNumber, year, month);
            Calendar cTemp = Calendar.getInstance();
            cTemp.set(year, month, dayNumber);
            int startDay = Time.getJulianDay(cTemp.getTimeInMillis(),
                    TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cTemp
                            .getTimeInMillis())));
            d.setAdapter(this);
            d.setStartDay(startDay);
            days[i] = "" + dayNumber;
            dayNumber++;
            dayList.add(d);
        }
    }

    public void refreshWeekByCal() {
        getStartEndOFWeek(currentWeekNumber, currentYear);
        int lastDay = 7;
        int firstDay = (int) calTemp.get(Calendar.DAY_OF_WEEK);
        int year = calTemp.get(Calendar.YEAR);
        int month = calTemp.get(Calendar.MONTH);
        TimeZone tz = TimeZone.getDefault();
        // figure size of the array
        if (firstDay == 1) {
            days = new String[lastDay];
        } else {
            days = new String[lastDay];
        }
        if (firstDay > 1) {
            for (int j = 0; j < (firstDay - 1); j++) {
                days[j] = "";
                Day d = new Day(context, 0, 0, 0);
                dayList.add(d);
            }
        }
        int dayNumber = 1;
        for (int i = 0; i < days.length; i++) {
            dayNumber = calTemp.get(Calendar.DAY_OF_MONTH);
            month = calTemp.get(Calendar.MONTH);
            year = calTemp.get(Calendar.YEAR);
            Day d = new Day(context, dayNumber, year, month);
            Calendar cTemp = Calendar.getInstance();
            cTemp.set(year, month, dayNumber);
            int startDay = Time.getJulianDay(cTemp.getTimeInMillis(),
                    TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cTemp
                            .getTimeInMillis())));
            d.setAdapter(this);
            d.setStartDay(startDay);
            days[i] = "" + dayNumber;
            // dayNumber++;
            calTemp.add(Calendar.DATE, 1);
            dayList.add(d);
        }

    }

    public void getStartEndOFWeek(int enterWeek, int enterYear) {
        // enterWeek is week number
        // enterYear is year
        calTemp = cal;
        calTemp.clear();
        calTemp.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calTemp.set(Calendar.WEEK_OF_YEAR, enterWeek);
        calTemp.set(Calendar.YEAR, enterYear);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); // PST`
        Date startDate = calTemp.getTime();
        String startDateInStr = formatter.format(startDate);
    }
}
