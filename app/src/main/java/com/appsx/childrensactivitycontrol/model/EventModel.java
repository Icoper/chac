package com.appsx.childrensactivitycontrol.model;

/**
 * Created by dmitriysamoilov on 22.12.17.
 */

public class EventModel {
    String appName;
    String timeStart;
    String timeEnd;

    public EventModel(String appName, String timeStart, String timeEnd) {
        this.appName = appName;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public EventModel() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
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
