package com.example.seeyoumanagement;


import com.google.firebase.database.IgnoreExtraProperties;

import java.time.DayOfWeek;
import java.util.HashMap;

@IgnoreExtraProperties
public class UserInfo {

    String name;
    HashMap<DayOfWeek, Timerange> workingTime;



    public UserInfo(){

    }
    public UserInfo(String name) {
        this.name = name;
        this.workingTime = new HashMap<>();

    }
    public void setTimerange(DayOfWeek day, Timerange timerange){
        workingTime.put(day, timerange);
    }

    public void resetWorkingTIme(){
        workingTime.clear();
    }

    public String getName() {
        return name;
    }
}
