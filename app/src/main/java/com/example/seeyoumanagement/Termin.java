package com.example.seeyoumanagement;


import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Termin {


    public Rooms room;
    public String userID;
    public Date dateStart, dateEnd;


    public Termin() {

    }

        public Termin(String uid, Rooms room, Date dateStart, Date dateEnd) {
            this.userID = uid;
            this.room = room;
            this.dateStart = dateStart;
            this.dateEnd = dateEnd;
        }




}
