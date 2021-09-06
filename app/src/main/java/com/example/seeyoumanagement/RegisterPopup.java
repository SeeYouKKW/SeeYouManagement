package com.example.seeyoumanagement;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;

import java.time.DayOfWeek;

public class RegisterPopup extends Activity implements RangeTimePickerDialog.ISelectedTime{

    Button btn_submit;
    Button[] btn_week;
    TextView[] txt_week;
    EditText txt_name;
    DayOfWeek[] ref_week;
    int selectedPos;

    UserInfo info;


    private DatabaseReference db;
    private FirebaseAuth fbAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_popup);

        db = FirebaseDatabase.getInstance().getReference();
        fbAuth = FirebaseAuth.getInstance();
        info = new UserInfo();
        txt_name = findViewById(R.id.editTextTextPersonName);
       Button btn_mo = findViewById(R.id.button6);
       Button btn_di = findViewById(R.id.button5);
       Button btn_mi = findViewById(R.id.button4);
       Button btn_do = findViewById(R.id.btn_new);
       Button btn_fr = findViewById(R.id.button3);

        TextView txt_mo = findViewById(R.id.textView3);
        TextView txt_di = findViewById(R.id.textView4);
        TextView txt_mi = findViewById(R.id.textView5);
        TextView txt_do = findViewById(R.id.textView6);
        TextView txt_fr = findViewById(R.id.textView7);

        ref_week = DayOfWeek.values();


       btn_week = new Button[]{btn_mo, btn_di, btn_mi, btn_do, btn_fr};
       txt_week = new TextView[]{txt_mo, txt_di, txt_mi, txt_do, txt_fr};
       btn_submit = findViewById(R.id.button);

        for (int i = 0; i<btn_week.length; i++) {
            final int pos = i;
            btn_week[i].setOnClickListener(v -> {
                addEvent();
                selectedPos = pos;
            });
        }

        btn_submit.setOnClickListener(v -> {
            info.setName(txt_name.getText().toString());
            if (info.checkforData())
            {
                db.child("users").child(fbAuth.getUid()).setValue(info).addOnCompleteListener(task2 -> {
                    finish();
                });
            }

        });


    }

    @Override
    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd) {

        info.setTimerange(ref_week[selectedPos], new Timerange(hourStart, minuteStart, hourEnd, minuteEnd));
        txt_week[selectedPos].setText(hourStart + ":" + minuteStart + " - " + hourEnd + ":" + minuteEnd);

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
}
