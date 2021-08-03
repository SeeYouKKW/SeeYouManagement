package com.example.seeyoumanagement;


import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class UserInfo {

    String name;



    public UserInfo(){

    }
    public UserInfo(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }
}
