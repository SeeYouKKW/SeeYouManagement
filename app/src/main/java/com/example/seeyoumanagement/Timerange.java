package com.example.seeyoumanagement;

public class Timerange {

    int hourStart;
    int minuteStart;
    int hourEnd;
    int minuteEnd;

    public Timerange(){

    }

    public Timerange(int hourStart, int minuteStart, int hourEnd, int minuteEnd){
        this.hourStart = hourStart;
        this.minuteStart = minuteStart;
        this.hourEnd = hourEnd;
        this.minuteEnd = minuteEnd;

    }

    public int getHourStart() {
        return hourStart;
    }

    public int getMinuteStart() {
        return minuteStart;
    }

    public int getHourEnd() {
        return hourEnd;
    }

    public int getMinuteEnd() {
        return minuteEnd;
    }

}
