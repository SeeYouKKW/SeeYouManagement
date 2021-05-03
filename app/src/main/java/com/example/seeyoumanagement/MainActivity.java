package com.example.seeyoumanagement;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements RangeTimePickerDialog.ISelectedTime{


    final SimpleDateFormat monthformat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());


    private DatabaseReference db;

    ActionBar actionBar;
    CompactCalendarView compactCalendarView;
    FloatingActionButton btn_add;
    FloatingActionButton btn_sub;
    ListView listview;
    ArrayAdapter adapter;


    Calendar calendar;
    ArrayList<String> termine;
    Date selectedDate;
    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                selectedDate = dateClicked;
                Toast.makeText(MainActivity.this, String.format("%s", dateClicked), Toast.LENGTH_SHORT).show();

                termine.clear();
                for (Event event : events) {
                    Termin t = (Termin) event.getData();
                    calendar.setTime(t.dateStart);
                    String hour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
                    String minute = Integer.toString(calendar.get(Calendar.MINUTE));

                    calendar.setTime(t.dateEnd);
                    String hour2 = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
                    String minute2 = Integer.toString(calendar.get(Calendar.MINUTE));



                    termine.add(hour + ":" + minute + " - " + hour2 + ":" + minute2);
                }


                adapter.notifyDataSetChanged();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {



            updateUi(firstDayOfNewMonth);

            }
        });

        btn_add.setOnClickListener(v -> {
            addEvent();
        });

        btn_sub.setOnClickListener(v -> {
            removeEvents();
        });
    }

    private void initComponents() {
        btn_add = findViewById(R.id.btn_add);
        btn_sub = findViewById(R.id.btn_remove);

        //btn_add.setEnabled(false);

        listview = findViewById(R.id.listview);


        compactCalendarView = (CompactCalendarView) findViewById(R.id.calendar);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setLocale(TimeZone.getDefault(), Locale.getDefault());
        compactCalendarView.setUseThreeLetterAbbreviation(true);

        selectedDate = new Date();
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(monthformat.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        termine = new ArrayList<String>();
        adapter = new ArrayAdapter(this,  android.R.layout.simple_list_item_1, termine);
        listview.setAdapter(adapter);
        fbAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        calendar = Calendar.getInstance();


    }

    @Override
    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd) {


        String user = fbAuth.getUid();

        calendar.setTime(selectedDate);
        calendar.set(Calendar.HOUR_OF_DAY, hourStart);
        calendar.set(Calendar.MINUTE, minuteStart);

        Date dateStart = calendar.getTime();


        calendar.set(Calendar.HOUR_OF_DAY, hourEnd);
        calendar.set(Calendar.MINUTE, minuteEnd);

        Date dateEnd = calendar.getTime();



        Rooms room = Rooms.Room1;



        Termin termin = new Termin(user, room, dateStart, dateEnd);


        db.child("bookings").orderByChild("room").equalTo(String.valueOf(room)).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {

                DataSnapshot bookings = task.getResult();
                if(checkData(termin,bookings)){

                    System.out.println("works");
                    db.child("bookings").push().setValue(termin).addOnCompleteListener(task2 -> {
                        updateUi(selectedDate);
                    });
                }
                else {
                    System.out.println("fail");
                }


            }});



        





    }

    private boolean checkData(Termin termin1, DataSnapshot bookings) {

        for (DataSnapshot book : bookings.getChildren()) {
            Termin termin2 = book.getValue(Termin.class);
            if (isOverlapping(termin1.dateStart, termin1.dateEnd, termin2.dateStart, termin2.dateEnd)){
                Log.d("checkData", termin1.dateStart.toString() +  termin1.dateEnd.toString());
                Log.d("checkData", termin2.dateStart.toString() +  termin2.dateEnd.toString());
                return false;
            }
        }
        return true;
    }


    private void removeEvents() {


        String user = fbAuth.getUid();

        calendar.setTime(selectedDate);
        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));

        db.child("bookings").orderByChild("dateStart").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {

                DataSnapshot bookings = task.getResult();
                Log.d("f", bookings.toString());
                for (DataSnapshot book : bookings.getChildren()) {
                    Termin buchung = book.getValue(Termin.class);
                    if (buchung.userID.equals(user)){
                        book.getRef().removeValue().addOnCompleteListener(task1 -> {
                            updateUi(selectedDate);
                        });
                    }
                }

            }

        });




    }
    private void addEvent() {

        // Create an instance of the dialog fragment and show it
        RangeTimePickerDialog dialog = new RangeTimePickerDialog();
        dialog.newInstance(R.color.CyanWater, R.color.White, R.color.Yellow, R.color.CyanWater, false);
        dialog.setRadiusDialog(20); // Set radius of dialog (default is 50)
        dialog.setColorBackgroundHeader(R.color.CyanWater); // Set Color of Background header dialog
        dialog.setColorTextButton(R.color.White); // Set Text color of button
        FragmentManager fragmentManager = getFragmentManager();
        dialog.show(fragmentManager, "");


    }

    private void updateUi(Date date){

        calendar.setTime(date);
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String month = Integer.toString(calendar.get(Calendar.MONTH));

        actionBar.setTitle(monthformat.format(date));


        db.child("bookings").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {


                compactCalendarView.removeAllEvents();
                DataSnapshot bookings = task.getResult();



                for (DataSnapshot book : bookings.getChildren()) {

                    Termin buchung = book.getValue(Termin.class);


                    calendar.setTime(buchung.dateStart);


                    Event ev1 = new Event(Color.GREEN, calendar.getTimeInMillis(), buchung);
                    compactCalendarView.addEvent(ev1);

                }



            }
        });



    }

    public boolean isOverlapping(Date start1, Date end1, Date start2, Date end2) {

        Date firstEnd;
        Date secondEnd;

        if (start1.before(start2)){

            firstEnd = end1;
            secondEnd = end2;
        }
        else
        {
            firstEnd = end2;
            secondEnd = end1;
        }
        return !firstEnd.before(secondEnd);
    }
}
