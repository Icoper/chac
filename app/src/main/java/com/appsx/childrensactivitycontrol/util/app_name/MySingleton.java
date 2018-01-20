package com.appsx.childrensactivitycontrol.util.app_name;

/**
 * Created by dmitriysamoilov on 04.01.18.
 */

public class MySingleton {
    private static MySingleton ourInstance = new MySingleton();

    public static MySingleton getInstance() {
        return ourInstance;
    }

    public MySingleton() {
    }

    private boolean serviceIsStop;

    public boolean isServiceIsStop() {
        return serviceIsStop;
    }

    public void setServiceIsStop(boolean serviceIsStop) {
        this.serviceIsStop = serviceIsStop;
    }
}
