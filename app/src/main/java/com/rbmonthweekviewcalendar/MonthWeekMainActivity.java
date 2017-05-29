package com.rbmonthweekviewcalendar;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.calendar.extendedcalendarview.CalendarProvider;
import com.calendar.extendedcalendarview.Day;
import com.calendar.extendedcalendarview.Event;
import com.calendar.extendedcalendarview.ExtendedCalendarView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.calendar.extendedcalendarview.CalendarProvider.CALENDAR_ID;
import static com.calendar.extendedcalendarview.CalendarProvider.DATEE;

public class MonthWeekMainActivity extends AppCompatActivity implements ExtendedCalendarView.OnDayClickListener {
    protected ExtendedCalendarView extendedCalendarView;
    protected int currentWeekNumber;
    protected int currentYear;
    protected ArrayList<CalBean> calendarArrayList;
    protected CalendarAdapter calendarAdapter;
    protected ListView calendar_event_listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setCalendarComponentInit();
    }

    protected void setCalendarComponentInit() {
        calendar_event_listView = (ListView) findViewById(R.id.calendar_event_listView);
        extendedCalendarView = (ExtendedCalendarView) findViewById(R.id.extendedCalendarView);
        extendedCalendarView.setOnDayClickListener(this);
        Calendar cal = Calendar.getInstance();
        currentWeekNumber = cal.get(Calendar.WEEK_OF_YEAR);
        currentYear = cal.get(Calendar.YEAR);
        loadJSONFromAsset();
        calendar_event_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                CalBean selectedRow = (CalBean) adapterView.getItemAtPosition(i);
                openDeleteConfirmDialog(selectedRow);
                return false;
            }
        });
    }

    /*Set data with event color to content provider from list of data
    * Event.COLOR_BLUE
    * Event.COLOR_BLUE
    * Event.COLOR_GREEN
    * Event.COLOR_PURPLE
    * Event.COLOR_YELLOW
    * */
    protected void setTicketToCalendar(ArrayList<CalBean> addValues) {
        int TicketColour = 0;
        DeleteAllTicketsFromCalender();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (addValues.size() > 0) {
            for (int i = 0; i < addValues.size(); i++) {
                if (addValues.get(i).getType().equalsIgnoreCase("Event")) {
                    TicketColour = Event.COLOR_BLUE;
                } else {
                    TicketColour = Event.COLOR_RED;
                }
                try {
                    date = sdf.parse(addValues.get(i).getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                addColorsDots(addValues.get(i), TicketColour, date);
            }
        }
        getAllTodayTicket();

    }

    /*Delete all the data from content provider*/
    protected void DeleteAllTicketsFromCalender() {
        MonthWeekMainActivity.this.getContentResolver().delete(
                CalendarProvider.CONTENT_URI, null, null);
    }

    /*Delete selected data from content provider*/
    protected void DeleteSingleTicketFromCalendar(String ticketId) {
        MonthWeekMainActivity.this.getContentResolver().delete(
                CalendarProvider.CONTENT_URI, "calendar_id=?",
                new String[]{ticketId});
    }

    /*Add data to content provider and that is placed to calendar*/
    protected void addColorsDots(CalBean calbean, int color, Date dt) {
        // Here you can add necessarily field that is used for display data
        ContentValues values = new ContentValues();
        values.put(CalendarProvider.COLOR, color);
        values.put(CalendarProvider.EVENT, calbean.getTitle()); // Title
        values.put(DATEE, calbean.getDate());
        values.put(CALENDAR_ID, calbean.getId()); // id
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        TimeZone tz = TimeZone.getDefault();
        int DayJulian = Time.getJulianDay(cal.getTimeInMillis(),
                TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal
                        .getTimeInMillis())));
        values.put(CalendarProvider.START, cal.getTimeInMillis());
        values.put(CalendarProvider.START_DAY, DayJulian);
        int endDayJulian = Time.getJulianDay(cal.getTimeInMillis(),
                TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal
                        .getTimeInMillis())));
        values.put(CalendarProvider.END, cal.getTimeInMillis());
        values.put(CalendarProvider.END_DAY, endDayJulian);
        values.put(CalendarProvider.TYPE, calbean.getType());
        getContentResolver().insert(CalendarProvider.CONTENT_URI, values);
    }

    /*Display data from selected date selection*/
    @Override
    public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, day.getDay());
        cal.set(Calendar.MONTH, day.getMonth());
        cal.set(Calendar.YEAR, day.getYear());
        currentWeekNumber = cal.get(Calendar.WEEK_OF_YEAR);
        currentYear = cal.get(Calendar.YEAR);
        calendarArrayList = new ArrayList<>();
        for (Event e : day.getEvents()) {
            HashMap<Integer, String> event = new HashMap<Integer, String>();
            event.put(1, e.getCalendar_id());// calendar_id
            event.put(2, e.getTitle()); // title
            event.put(3, e.getDate());
            event.put(4, e.getType());
            Log.e(event.get(1), event.get(2));
            calendarArrayList.add(new CalBean(event.get(1), event.get(2), event.get(3), event.get(4)));
        }
        calendarAdapter = new CalendarAdapter(MonthWeekMainActivity.this, calendarArrayList);
        calendar_event_listView.setAdapter(calendarAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*  Take appropriate action for each action item click*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_week_view:
                setAsWeekOrMonthView(true);
                return true;
            case R.id.action_month_view:
                setAsWeekOrMonthView(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Load all the ticket related data from asset*/
    protected void loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = MonthWeekMainActivity.this.getAssets().open(
                    "ticket_response.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Gson gson = new GsonBuilder().create();
        try {
            calendarArrayList = new ArrayList<>();
            JSONObject obj = new JSONObject(json.toString());
            JSONArray mainData = obj.getJSONArray("Tickets");
            calendarArrayList = gson.fromJson(mainData.toString(), new TypeToken<List<CalBean>>() {
            }.getType());
            setTicketToCalendar(calendarArrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /* Display all Ticket of today*/
    protected void getAllTodayTicket() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        if (calendarArrayList.size() > 0) {
            calendarArrayList.clear();
        }
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(CalendarProvider.CONTENT_URI, null,
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String type = cursor.getString(cursor
                        .getColumnIndex(CalendarProvider.TYPE));
                String id = cursor.getString(cursor
                        .getColumnIndex(CalendarProvider.CALENDAR_ID));
                String title = cursor.getString(cursor
                        .getColumnIndex(CalendarProvider.EVENT));
                String date = cursor.getString(cursor
                        .getColumnIndex(CalendarProvider.DATEE));
                if (date.contains(formattedDate)) {
                    calendarArrayList.add(new CalBean(id, title, date, type));

                }
            } while (cursor.moveToNext());
        }
        calendarAdapter = new CalendarAdapter(MonthWeekMainActivity.this, calendarArrayList);
        calendar_event_listView.setAdapter(calendarAdapter);
        calendarAdapter.notifyDataSetChanged();
    }

    /*Display list of Ticket which date you have pass as parameter*/
    protected void getAllSpecificDateTicket(String dateFormat) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            c.setTime(df.parse(dateFormat));// all done
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = df.format(c.getTime());
        if (calendarArrayList.size() > 0) {
            calendarArrayList.clear();
        }
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(CalendarProvider.CONTENT_URI, null,
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String type = cursor.getString(cursor
                        .getColumnIndex(CalendarProvider.TYPE));
                String id = cursor.getString(cursor
                        .getColumnIndex(CalendarProvider.CALENDAR_ID));
                String title = cursor.getString(cursor
                        .getColumnIndex(CalendarProvider.EVENT));
                String date = cursor.getString(cursor
                        .getColumnIndex(CalendarProvider.DATEE));
                if (date.contains(formattedDate)) {
                    calendarArrayList.add(new CalBean(id, title, date, type));

                }
            } while (cursor.moveToNext());
        }
        calendarAdapter = new CalendarAdapter(MonthWeekMainActivity.this, calendarArrayList);
        calendar_event_listView.setAdapter(calendarAdapter);
        calendarAdapter.notifyDataSetChanged();
    }

    /*set week view or month view week = true, month = false*/
    protected void setAsWeekOrMonthView(boolean weekOrMonth) {
        if (weekOrMonth) {
            extendedCalendarView.setCurrentWeek(currentWeekNumber,
                    currentYear);
            extendedCalendarView.setWeekView(weekOrMonth);
        } else {
            extendedCalendarView.setCurrentWeek(currentWeekNumber,
                    currentYear);
            extendedCalendarView.setWeekView(weekOrMonth);
        }

    }

    protected void openDeleteConfirmDialog(final CalBean bean) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MonthWeekMainActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Delete conformation");
        dialog.setMessage("Are you sure you want to delete this entry?");
        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Action for "Delete".
                DeleteSingleTicketFromCalendar(bean.getId());
                /*This is used when some operation being happen and need to affect on calendar */
                extendedCalendarView.refreshCalendar();
                /*Retrieve rest of event */
                getAllSpecificDateTicket(bean.getDate());
            }
        })
                .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Action for "Cancel".
                    }
                });
        final AlertDialog alert = dialog.create();
        alert.show();
    }


}
