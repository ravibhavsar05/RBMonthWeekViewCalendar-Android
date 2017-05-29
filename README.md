This project is enhanced from ExtendedCalendarView (https://github.com/tyczj/ExtendedCalendarView).
In ExtendedCalendarView only support month view and in this project is also support Weekview as well.
 

Initially screen will display current month calendar view with event(Red or Blue dots.)
This event is saved in content provider using live data . 
			 Currently it is comes from json file that is placed in assest folder.
       
 Preview
----------------------------------------------------     
![](http://i.imgur.com/JCimhJP.gif)

Usage
----------------------------------------------------
  You can declare calendar view in xml like below  

        <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
              android:layout_width="match_parent"
              android:layout_height="match_parent" >
              
        <com.calendar.extendedcalendarview.ExtendedCalendarView
              android:id="@+id/calendar"
              android:layout_height="match_parent"
              android:layout_width="match_parent"/>
       </RelativeLayout>
-----------------------------------------------------

 Make sure you have mentioned content provider in manifest file like below:
 
        <provider
            android:name="com.calendar.extendedcalendarview.CalendarProvider"
            android:authorities="com.calendar.extendedcalendarview.calendarprovider" />
 
--------------------------------------------------------------------------
  You can findviewbyid as normally in java like below 
  
      ExtendedCalendarView calendar = (ExtendedCalendarView)findViewById(R.id.calendar);

	
-----------------------------------------------------------------------------
[How can add event in calendar?](https://github.com/ravibhavsar05/RBMonthWeekViewCalendar-Android/blob/master/app/src/main/java/com/rbmonthweekviewcalendar/MonthWeekMainActivity.java)

---------------------------------------------------------------------- 
How can access data on click of perticular date ? 

        "implements ExtendedCalendarView.OnDayClickListener"
 
----------------------------------------------------------------
How can access data on click of perticular date ? 

     @Override
    public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day day) {
        Calendar cal = Calendar.getInstance();
        ArrayList<CalBean> calendarArrayList = new ArrayList<>(); 
        for (Event e : day.getEvents()) {
            HashMap<Integer, String> event = new HashMap<Integer, String>();
            event.put(1, e.getCalendar_id());
            event.put(2, e.getTitle());
            event.put(3, e.getDate());
            event.put(4, e.getType());
            calendarArrayList.add(new CalBean(event.get(1), event.get(2), event.get(3), event.get(4)));

        }
        // Pass this calendarArrayList to List view  or Recycle view
    }
--------------------------------------------------------------------- 
How can refresh calendar view after some CRUD opertaion is fired?
 

    ExtendedCalendarView calendar = (ExtendedCalendarView)findViewById(R.id.calendar);
    calendar.refreshCalendar();


---------------------------------------------------------------
How can delete perticular event from calendar ?
 

    protected void DeleteSingleTicketFromCalendar(String ticketId) {
        MonthWeekMainActivity.this.getContentResolver().delete(
                CalendarProvider.CONTENT_URI, "calendar_id=?",
                new String[]{ticketId});
    }
	
-------------------------------------------------
How can delete all event from calendar ?
 
 
      protected void DeleteAllTicketsFromCalender() {
        MonthWeekMainActivity.this.getContentResolver().delete(
                CalendarProvider.CONTENT_URI, null, null);
      }
	

 
	
