package com.appsx.childrensactivitycontrol.util.screen;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by dmitriysamoilov on 08.01.18.
 */

public class ScreenStateHelper {
    private Context context;
    private   PowerManager powerManager;

    public ScreenStateHelper(Context context) {
        this.context = context;
    }

    public boolean isScreenOn(){

        if (powerManager == null) {
            powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        }
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.LOLLIPOP){
            return powerManager.isScreenOn();
        }else {
            return powerManager.isInteractive();
        }
    }
}
