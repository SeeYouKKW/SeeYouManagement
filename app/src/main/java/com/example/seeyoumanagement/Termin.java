package com.example.seeyoumanagement;




import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Termin {


    public Rooms room;
    public String userID, userID_date;
    public Date dateStart, dateEnd;



    public Termin() {


    }

        public Termin(String uid, Rooms room, Date dateStart, Date dateEnd, String uid_date) {
            this.userID = uid;
            this.room = room;
            this.dateStart = dateStart;
            this.dateEnd = dateEnd;
            this.userID_date = uid_date;
        }


    public Date getDateStart() {
        return dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public String getUserID() {
        return userID;
    }

    public Rooms getRoom() {
        return room;
    }
}
