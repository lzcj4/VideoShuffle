package com.nero.videoshuffle.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nero.videoshuffle.R;
import com.nero.videoshuffle.fragment.UserFragment;
import com.nero.videoshuffle.model.GitHubService;
import com.nero.videoshuffle.model.GitHubServiceImpl;
import com.nero.videoshuffle.model.MediaItem;
import com.nero.videoshuffle.model.User;
import com.nero.videoshuffle.provider.Apple;
import com.nero.videoshuffle.provider.FruitColumn;
import com.nero.videoshuffle.receiver.ScheduleReceiver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnNavigateNewFragment {
    @Override
    protected void onResume() {
        super.onResume();
        // AppEventsLogger.activateApp(this);
        //testWifi();
        //testAlarm();
        //testContentProvider();
//        testRetrofit();
    }


    @Override
    protected void onPause() {
        super.onPause();
        // AppEventsLogger.deactivateApp(this);
    }

    Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DROP:
                        ClipData cd = event.getClipData();
                        MediaItem item = (MediaItem) event.getLocalState();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        mCurrentFragment = new VideoViewFragment_().builder().build();
//        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.add(R.id.viewContent, mCurrentFragment).addToBackStack(null).commit();

        UserFragment fragment=new UserFragment();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.viewContent, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCurrentFragment.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void testWifi() {
        WifiManager wm = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> list = wm.getConfiguredNetworks();
        // wm.addNetwork()
        for (WifiConfiguration item : list) {
            Log.i("wifi", item.toString());
            String name = item.SSID;

            try {
                Field pwField = WifiConfiguration.class.getField("password");
                if (pwField != null) {

                    try {
                        Object pwd = pwField.get(item);
                        Log.i("Wifi", pwd.toString());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

        }
    }

    private final String PREF_NAME = "pref_app";
    private final String PREF_SCHEDULE_ENABLED = "pref_schedule_enabled";

    private void testAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Date dtNow = new Date();

        Intent intent = new Intent(this, ScheduleReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        SharedPreferences sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isScheduleEnabled = sp.getBoolean(PREF_SCHEDULE_ENABLED, false);
        if (isScheduleEnabled) {
            am.cancel(pi);
        } else {
            Date dt = new Date();
            long interval = 5 * 1000;
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, dt.getTime() + interval, interval, pi);
        }

        isScheduleEnabled = !isScheduleEnabled;
        sp.edit().putBoolean(PREF_SCHEDULE_ENABLED, isScheduleEnabled).commit();

    }

    private void testContentProvider() {
        ContentResolver cr = getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(FruitColumn.Apple.COL_NAME, "Apple 1");
        cv.put(FruitColumn.Apple.COL_PRICE, 25);
        cv.put(FruitColumn.Apple.COL_AMOUNT, 14);
        Uri uri = cr.insert(FruitColumn.Apple.CONTENT_URI, cv);
        Log.i("Content provide", uri.toString());

        Cursor cursor = cr.query(FruitColumn.Apple.CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();
        List<Apple> list = new ArrayList<>();
        try {
            do {

                Apple item = new Apple();
                item.Id = cursor.getLong(cursor.getColumnIndex(FruitColumn.Apple._ID));
                item.Name = cursor.getString(cursor.getColumnIndex(FruitColumn.Apple.COL_NAME));
                item.Price = cursor.getDouble(cursor.getColumnIndex(FruitColumn.Apple.COL_PRICE));
                item.Amount = cursor.getDouble(cursor.getColumnIndex(FruitColumn.Apple.COL_AMOUNT));
                list.add(item);
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
        }


        if (!list.isEmpty()) {
            Apple apple = list.get(0);
            cv = new ContentValues();
            Uri appleUri = ContentUris.withAppendedId(FruitColumn.Apple.CONTENT_URI, apple.Id);
            cv.put(FruitColumn.Apple.COL_PRICE, 10.2);
            cv.put(FruitColumn.Apple.COL_AMOUNT, 3.24);
            cr.update(appleUri, cv, null, null);

        }
    }

    private void testRetrofit() {
        GitHubService gitHubService = GitHubServiceImpl.getInstance();

        User item = new User();
        item.name = "aa";
        item.company = "bb";
        item.login = "aa_tt";
        Call<User> addCall = gitHubService.addNewUser(item);
        addCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {
                User newUser = response.body();
                if (null != newUser)
                    Log.i("Retrofit", newUser.toString());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
        Call<User> call = gitHubService.listUser();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response, Retrofit retrofit) {

                User user = response.body();
                if (null != user)
                    Log.i("Restrofi", String.valueOf(user.toString()));
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });


    }

    @Override
    public void navigate(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.viewContent, fragment);
        //if (fragment instanceof VideoViewFragment)
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
        mCurrentFragment = fragment;

    }

}
