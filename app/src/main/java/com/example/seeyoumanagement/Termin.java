package com.example.seeyoumanagement;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Termin {



        public String userID;
        public int hourStart,  minuteStart, hourEnd,  minuteEnd;


        public Termin() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Termin(String userID, int hourStart, int minuteStart, int hourEnd, int minuteEnd) {
            this.userID = userID;
            this.hourStart = hourStart;
            this.minuteStart = minuteStart;
            this.hourEnd = hourEnd;
            this.minuteEnd = minuteEnd;
        }


}
