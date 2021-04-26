package com.example.seeyoumanagement;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;

import java.text.SimpleDateFormat;
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
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {


                Calendar cal = Calendar.getInstance();
                cal.setTime(firstDayOfNewMonth);
                String year = Integer.toString(cal.get(Calendar.YEAR));
                String month = Integer.toString(cal.get(Calendar.MONTH));

                actionBar.setTitle(monthformat.format(firstDayOfNewMonth));
                db.child(year).child(month).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    }
                });

            }
        });

        btn_add.setOnClickListener(v -> {
            // Create an instance of the dialog fragment and show it
            RangeTimePickerDialog dialog = new RangeTimePickerDialog();
            dialog.newInstance(R.color.CyanWater, R.color.White, R.color.Yellow, R.color.CyanWater, false);
            dialog.setRadiusDialog(20); // Set radius of dialog (default is 50)
            dialog.setColorBackgroundHeader(R.color.CyanWater); // Set Color of Background header dialog
            dialog.setColorTextButton(R.color.White); // Set Text color of button
            FragmentManager fragmentManager = getFragmentManager();
            dialog.show(fragmentManager, "");
        });

        btn_sub.setOnClickListener(v -> {
            removeEvents(selectedDate);
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

        fbAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();


    }

    @Override
    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd) {

        Log.d("aa",fbAuth.getUid());

        String user = fbAuth.getUid();
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        String year = Integer.toString(cal.get(Calendar.YEAR));
        String month = Integer.toString(cal.get(Calendar.MONTH));
        String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));

        Termin termin = new Termin(user, Rooms.Room1, day, hourStart, minuteStart, hourEnd, minuteEnd);

        db.child("bookings").child(year).child(month).push().setValue(termin);




    }


    private void removeEvents(Date selectedDate) {

    }
    private void addEvent(Date selectedDate) {



    }
}
