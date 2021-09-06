package com.example.seeyoumanagement;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.atomic.AtomicBoolean;

public class Login extends AppCompatActivity {

    Button login;
    CheckBox cb_staylogged;
    EditText txt_nutzername;
    EditText txt_passwort;
    ProgressBar pb_process;

    private FirebaseAuth fbAuth;
    private DatabaseReference db;
    private static final String PREFS_NAME = "docname";
    SharedPreferences settings;
    boolean registered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initComponents();

        fbAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        login.setOnClickListener(v -> login());

        settings = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String username = settings.getString("loginUsername", "DEFAULT");
        String passwort = settings.getString("loginPasswort", "DEFAULT");

        if (!username.equals("DEFAULT")){
            cb_staylogged.setChecked(true);
            txt_nutzername.setText(username);
            txt_passwort.setText(passwort);
            pb_process.setVisibility(View.VISIBLE);
            fbAuth.signInWithEmailAndPassword(username, passwort).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    checkForRestData();
      }
                pb_process.setVisibility(View.INVISIBLE);
            });



        }
    }


    private void checkForRestData() {


        db.child("users").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {

                DataSnapshot result = task.getResult();
                if (result.hasChild(fbAuth.getUid())){

                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
                else
                {
                    startActivity(new Intent(Login.this, RegisterPopup.class));
                }

            }


        });



    }


    private void initComponents() {
        login = findViewById(R.id.btn_login);
        txt_nutzername = findViewById(R.id.txt_nutzername);
        txt_passwort = findViewById(R.id.txt_passwort);
        cb_staylogged = findViewById(R.id.cb_staylogged);
        pb_process = findViewById(R.id.progressBar_cyclic);
    }

    /**
     * ver4sucht sich einzuloggen
     * wenn es klappt --> MainActivity
     */
    private void login() {
        String name = txt_nutzername.getText().toString().trim();
        String passwort = txt_passwort.getText().toString().trim();
        pb_process.setVisibility(View.VISIBLE);



        if (TextUtils.isEmpty(name)){
            txt_nutzername.setError("Nutername wird benötigt");
            return;
        }
        if (TextUtils.isEmpty(passwort)){
            txt_passwort.setError("Passwort wird benötigt");
            return;
        }
        fbAuth.signInWithEmailAndPassword(name, passwort).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (cb_staylogged.isChecked()){

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("loginUsername", name);
                    editor.putString("loginPasswort", passwort);
                    editor.apply();
                }
                checkForRestData();

            }
            pb_process.setVisibility(View.INVISIBLE);
        });



    }

}