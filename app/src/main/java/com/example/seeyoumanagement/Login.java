package com.example.seeyoumanagement;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    Button login;
    CheckBox cb_staylogged;

    EditText txt_nutzername;
    EditText txt_passwort;

    private FirebaseAuth fbAuth;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initComponents();

        fbAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        login.setOnClickListener(v -> login());
    }


    private void initComponents() {
        login = findViewById(R.id.btn_login);
        txt_nutzername = findViewById(R.id.txt_nutzername);
        txt_passwort = findViewById(R.id.txt_passwort);
        cb_staylogged = findViewById(R.id.cb_staylogged);
    }


    private void login() {
        String name = txt_nutzername.getText().toString().trim();
        String passwort = txt_passwort.getText().toString().trim();



        if (TextUtils.isEmpty(name)){
            txt_nutzername.setError("Nutername wird benötigt");
            return;
        }
        if (TextUtils.isEmpty(passwort)){
            txt_passwort.setError("Passwort wird benötigt");
            return;
        }
        fbAuth.signInWithEmailAndPassword(name, passwort).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
            }
        });
        // User user = new User(name, passwort);
        //db.child("users").child(name).setValue(user);

        /*

        db.child("users").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
            }
        });

         */
    }

}