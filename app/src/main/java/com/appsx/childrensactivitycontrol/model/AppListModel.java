package com.appsx.childrensactivitycontrol.model;

/**
 * Created by dmitriysamoilov on 09.01.18.
 */

public class AppListModel {
    private String name;
    private String appPackage;
    private String time;
    private String dataStart;
    private String dataEnd;

    public AppListModel(String name, String appPackage, String time) {
        this.name = name;
        this.appPackage = appPackage;
        this.time = time;
    }

    public AppListModel(String name, String appPackage, String time, String dataStart, String dataEnd) {
        this.name = name;
        this.appPackage = appPackage;
        this.time = time;
        this.dataStart = dataStart;
        this.dataEnd = dataEnd;
    }

    public String getDataStart() {

        return dataStart;
    }

    public void setDataStart(String dataStart) {
        this.dataStart = dataStart;
    }

    public String getDataEnd() {
        return dataEnd;
    }

    public void setDataEnd(String dataEnd) {
        this.dataEnd = dataEnd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
