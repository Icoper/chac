package com.appsx.childrensactivitycontrol.model;

/**
 * Created by dmitriysamoilov on 22.12.17.
 */

public class EventModel {
    String appPackage;
    String timeStart;
    String timeEnd;

    public EventModel(String appPackage, String timeStart, String timeEnd) {
        this.appPackage = appPackage;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public EventModel() {
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

}
