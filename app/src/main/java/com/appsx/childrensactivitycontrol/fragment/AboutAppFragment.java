package com.appsx.childrensactivitycontrol.fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appsx.childrensactivitycontrol.R;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AboutAppFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_app_fragment, container, false);

        return view;
    }
}
