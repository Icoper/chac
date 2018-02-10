package com.appsx.childrensactivitycontrol.model;

import java.util.ArrayList;

public class AppInfoListModel {
    String appPackage;
    ArrayList<AppInfo> appInfos;

    public AppInfoListModel() {
    }

    public AppInfoListModel(String appPackage, ArrayList<AppInfo> appInfos) {
        this.appPackage = appPackage;
        this.appInfos = appInfos;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public ArrayList<AppInfo> getAppInfos() {
        return appInfos;
    }

    public void setAppInfos(ArrayList<AppInfo> appInfos) {
        this.appInfos = appInfos;
    }

    public static class AppInfo {
        String date;
        ArrayList<String> runningEvents;

        public AppInfo(String date, ArrayList<String> runningEvents) {
            this.date = date;
            this.runningEvents = runningEvents;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public ArrayList<String> getRunningEvents() {
            return runningEvents;
        }

        public void setRunningEvents(ArrayList<String> runningEvents) {
            this.runningEvents = runningEvents;
        }
    }
}
