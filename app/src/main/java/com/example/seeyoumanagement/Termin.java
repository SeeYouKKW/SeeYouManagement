package com.example.seeyoumanagement;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Termin {


    public Rooms room;
    public String userID, day;
        public int hourStart,  minuteStart, hourEnd,  minuteEnd;


    public Termin() {

    }

        public Termin(String uid, Rooms room, String day, int hourStart, int minuteStart, int hourEnd, int minuteEnd) {
            this.userID = uid;
            this.room = room;
            this.day = day;
            this.hourStart = hourStart;
            this.minuteStart = minuteStart;
            this.hourEnd = hourEnd;
            this.minuteEnd = minuteEnd;
        }




}
