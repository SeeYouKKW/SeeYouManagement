package com.example.seeyoumanagement;


import com.google.firebase.database.IgnoreExtraProperties;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class UserInfo {

    public String name;
    public Map<String, Timerange> workingTime;



    public UserInfo() {

        this.workingTime = new HashMap<>();

    }
    public void setName(String name) {
        this.name = name;
    }

    public void setTimerange(DayOfWeek day, Timerange timerange){
        workingTime.put(day.name(), timerange);
    }

    public void resetWorkingTIme(){
        workingTime.clear();
    }

    public String getName() {
        return name;
    }


    public boolean checkforData() {

        return name.length() > 2 && !workingTime.isEmpty();
    }
}
