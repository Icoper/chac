package com.appsx.childrensactivitycontrol.activity;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.appsx.childrensactivitycontrol.R;
import com.appsx.childrensactivitycontrol.fragment.AboutAppFragment;
import com.appsx.childrensactivitycontrol.fragment.AddDeviceFragment;
import com.appsx.childrensactivitycontrol.fragment.StatisticFragment;
import com.appsx.childrensactivitycontrol.util.GlobalNames;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarLayout appBarLayout;
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
            transaction = getFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_statistic:
                    transaction.replace(R.id.fragment_container, statisticFragment);
                    break;
                case R.id.navigation_device:
                    transaction.replace(R.id.fragment_container, addDeviceFragment);
                    break;
                case R.id.navigation_info:
                    transaction.replace(R.id.fragment_container, aboutAppFragment);
                    break;
            }
            transaction.addToBackStack(null);
            transaction.commit();
            return true;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.container);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
            appBarLayout.setElevation(0);
            appBarLayout.setOutlineProvider(null);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        transaction.add(R.id.fragment_container, statisticFragment);
        setStatusBarColor();
        transaction.commit();

    }

    public void setStatusBarColor() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlue));
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.container);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_rate) {
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } else if (id == R.id.nav_clean_data) {
            showMessageAlert();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.container);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showMessageAlert() {

        View view = LayoutInflater.from(this).inflate(R.layout.alert_permission_asker, null);
        AlertDialog.Builder builder = new AlertDialog
                .Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme));
        builder.setView(view);
        TextView textView = (TextView) view.findViewById(R.id.alert_perm_message_tv);

        textView.setText(this.getString(R.string.reset_all_data_msg));
        builder.setCancelable(false)
                .setPositiveButton(this.getString(R.string.confirm_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File shPref = new File("/data/data/" + GlobalNames.APP_PACKAGE_NAME + "/shared_prefs/com.appsx.childrensactivitycontrol_preferences.xml");
                        File db = new File("/data/data/" + GlobalNames.APP_PACKAGE_NAME + "/databases/chac_event");
                        db.delete();
                        shPref.delete();
                        System.exit(0);
                    }
                })
                .setNegativeButton(this.getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
