package com.appsx.childrensactivitycontrol.fragment;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appsx.childrensactivitycontrol.R;

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class AddDeviceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_device_fragment, container, false);
        return view;
    }
}
