package com.appsx.childrensactivitycontrol.util.app_name;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by dmitriysamoilov on 03.01.18.
 */

public class ToastShower {

    public ToastShower() {
    }

    public void showToast(Context context, String text){
        Toast.makeText(context,text, Toast.LENGTH_SHORT).show();
    }
}
