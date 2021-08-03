package com.example.seeyoumanagement;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TerminAdapter extends ArrayAdapter<Termin> {

    final SimpleDateFormat hourformat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private DatabaseReference db;
    HashMap<String, UserInfo> userRef;



    public TerminAdapter(@NonNull Context context,  @NonNull List<Termin> termine) {
        super(context, 0, termine);
        db = FirebaseDatabase.getInstance().getReference();
        userRef = new HashMap<String, UserInfo>();
        db.child("users").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {

                DataSnapshot rooms = task.getResult();
                for (DataSnapshot user: rooms.getChildren()) {
                    String id = user.getKey();
                    UserInfo buchung = user.getValue(UserInfo.class);
                    userRef.put(id, buchung);
                }
            }
        });



    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.termin, parent, false
            );
        }

        TextView txt_user = convertView.findViewById(R.id.txt_user);
        TextView txt_room = convertView.findViewById(R.id.txt_room);
        TextView txt_timeBegin = convertView.findViewById(R.id.txt_timebegin);
        TextView txt_timeEnd = convertView.findViewById(R.id.txt_timeend);



        Termin termin = getItem(position);

        String name = userRef.get(termin.getUserID()).getName();
        if (termin != null) {
            if (name != null){
                txt_user.setText(name);
            }
            else {
                txt_user.setText(termin.getUserID());
            }

            txt_room.setText(String.valueOf(termin.getRoom()));
            txt_timeBegin.setText(hourformat.format(termin.getDateStart()));
            txt_timeEnd.setText(hourformat.format(termin.getDateEnd()));
        }

        return convertView;
    }

}
