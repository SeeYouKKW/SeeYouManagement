package com.example.seeyoumanagement;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements RangeTimePickerDialog.ISelectedTime{


    final SimpleDateFormat monthformat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    final SimpleDateFormat dayformat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

    private DatabaseReference db;

    ActionBar actionBar;
    CompactCalendarView compactCalendarView;
    FloatingActionButton btn_add;
    FloatingActionButton btn_sub;
    ListView listview;
    ArrayAdapter adapter;
    HashMap<Rooms,Integer> befuellung;

    GridLayout gridlayout;


    Calendar calendar;
    ArrayList<Termin> termine;
    Date selectedDate;
    private FirebaseAuth fbAuth;

    Rooms selectedRoom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                updateListview(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {



            updateUi(firstDayOfNewMonth);
            updateListview(selectedDate);

            }
        });

        btn_add.setOnClickListener(v -> {
            addEvent();
        });

        btn_sub.setOnClickListener(v -> {
            removeEvents();
        });
    }

    private void updateListview(Date dateClicked) {
        List<Event> events = compactCalendarView.getEvents(dateClicked);
        selectedDate = dateClicked;


        termine.clear();
        for (Event event : events) {
            Termin t = (Termin) event.getData();
            termine.add(t);
        }

        sortList();
        adapter.notifyDataSetChanged();
    }

    private void initComponents() {
        btn_add = findViewById(R.id.btn_add);
        btn_sub = findViewById(R.id.btn_remove);
        gridlayout = findViewById(R.id.gridLayout);
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

        termine = new ArrayList<Termin>();
        adapter = new TerminAdapter(this, termine);
        listview.setAdapter(adapter);
        fbAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        selectedRoom = Rooms.Room1;



        befuellung = new HashMap<Rooms, Integer>();
        db.child("rooms").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {

                DataSnapshot rooms = task.getResult();
                for (DataSnapshot room: rooms.getChildren()) {
                    Rooms r = Rooms.valueOf(room.getKey());
                    befuellung.put(r, (int) (long) room.getValue());
                }
            }
        });



        db.child("bookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateUi(selectedDate);
                updateListview(selectedDate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        calendar = Calendar.getInstance();

        updateUi(selectedDate);


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

        String day = dayformat.format(selectedDate);


        Termin termin = new Termin(user, selectedRoom, dateStart, dateEnd, user + "_" + day);


        db.child("bookings").orderByChild("room").equalTo(String.valueOf(selectedRoom)).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {

                DataSnapshot bookings = task.getResult();
                if(checkData(termin, bookings, befuellung.get(selectedRoom))){

                    Toast.makeText(MainActivity.this, "Termin hinzugefügt!", Toast.LENGTH_SHORT).show();
                    db.child("bookings").push().setValue(termin).addOnCompleteListener(task2 -> {
                        updateUi(selectedDate);
                        updateListview(selectedDate);
                    });
                }
                else {
                    Toast.makeText(MainActivity.this, "Termin kann aufgrund von überfüllten Räumen nicht nizugefügt werden", Toast.LENGTH_SHORT).show();
                }
            }});





        





    }

    public void onClick(View v) {
        ToggleButton selectedButton = (ToggleButton) v;
        int count = gridlayout.getChildCount();
        for(int i = 0 ; i <count ; i++){
            ToggleButton child = (ToggleButton) gridlayout.getChildAt(i);
            if (child != selectedButton){
                child.setChecked(false);
            }
        }

        selectedRoom = Rooms.valueOf((String) selectedButton.getText());



    }


    private boolean checkData(Termin termin1, DataSnapshot bookings, Integer fuelle) {

        int count = 0;
        for (DataSnapshot book : bookings.getChildren()) {
            Termin termin2 = book.getValue(Termin.class);
            if (isOverlapping(termin1.dateStart, termin1.dateEnd, termin2.dateStart, termin2.dateEnd)){
                if (termin1.userID.equals(termin2.userID)){
                    return false;
                }
                else {
                    count++;
                }

            }
        }
        return count<=fuelle;
    }


    private void removeEvents() {


        String user = fbAuth.getUid();

        calendar.setTime(selectedDate);
        String day = dayformat.format(selectedDate);



        db.child("bookings").orderByChild("userID_date").equalTo(user + "_" + day).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                boolean deleted = false;
                DataSnapshot bookings = task.getResult();


                for (DataSnapshot book : bookings.getChildren()) {
                    Termin buchung = book.getValue(Termin.class);
                    deleted = true;
                    book.getRef().removeValue().addOnCompleteListener(task1 -> {
                        updateUi(selectedDate);
                        updateListview(selectedDate);

                    });

                }
                if (deleted)
                {
                    Toast.makeText(MainActivity.this, "Termin(e) gelöscht!", Toast.LENGTH_SHORT).show();
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
    /**
    Alle Events löschen und neu herunterladen und dem Calender einführen
     */
    private void updateUi(Date date){


        actionBar.setTitle(monthformat.format(date));


        db.child("bookings").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {


                compactCalendarView.removeAllEvents();
                DataSnapshot bookings = task.getResult();


                HashMap<Integer, Integer> busycount = new HashMap<>();
                for (DataSnapshot book : bookings.getChildren()) {

                    Termin buchung = book.getValue(Termin.class);
                    calendar.setTime(buchung.dateStart);

                    if (busycount.containsKey(calendar.get(Calendar.DAY_OF_YEAR)))
                    {
                        busycount.put(calendar.get(Calendar.DAY_OF_YEAR), busycount.get(calendar.get(Calendar.DAY_OF_YEAR)) + 1);
                    }
                    else{
                        busycount.put(calendar.get(Calendar.DAY_OF_YEAR), 1);
                    }

                }

                System.out.println(busycount);

                for (DataSnapshot book : bookings.getChildren()) {

                    Termin buchung = book.getValue(Termin.class);
                    calendar.setTime(buchung.dateStart);

                    Event ev1 = new Event(trafficLightColors(busycount.get(calendar.get(Calendar.DAY_OF_YEAR))), calendar.getTimeInMillis(), buchung);
                    compactCalendarView.addEvent(ev1);


                }



            }
        });



    }
    private int trafficLightColors(int size){
        if (size < 2)
        {
            return Color.argb(100, 0, 255, 100);
        }
        else if (size > 2)
        {
            return Color.argb(100, 255, 0, 0);
        }
        else
        {
            return Color.argb(100, 255, 255, 0);
        }
    }

    private void sortList(){
        Collections.sort(termine, new Comparator<Termin>() {
            @Override
            public int compare(Termin o1, Termin o2) {
                return o1.getDateStart().compareTo(o2.getDateStart());
            }
        });
    }

    public boolean isOverlapping(Date start1, Date end1, Date start2, Date end2) {


        return start1.before(end2) && end1.after(start2);
    }
}
