package com.appsx.childrensactivitycontrol;

import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.appsx.childrensactivitycontrol.fragment.AboutAppFragment;
import com.appsx.childrensactivitycontrol.fragment.AddDeviceFragment;
import com.appsx.childrensactivitycontrol.fragment.StatisticFragment;
import com.willme.topactivity.R;

public class MainActivity extends AppCompatActivity {

    // view
    private FragmentTransaction transaction;
    private StatisticFragment statisticFragment;
    private AddDeviceFragment addDeviceFragment;
    private AboutAppFragment aboutAppFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_statistic:
                    transaction.replace(R.id.fragment_container,statisticFragment);
                    return true;
                case R.id.navigation_device:
                    transaction.replace(R.id.fragment_container,addDeviceFragment);
                    return true;
                case R.id.navigation_info:
                    transaction.replace(R.id.fragment_container,aboutAppFragment);
                    return true;
            }
            return false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initialize();

    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void initialize() {
        statisticFragment = new StatisticFragment();
        aboutAppFragment = new AboutAppFragment();
        addDeviceFragment = new AddDeviceFragment();

        transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container,statisticFragment);
        transaction.commit();
    }

}
