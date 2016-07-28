package com.mathieukh.dyr.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.mathieukh.dyr.MyApplication;
import com.mathieukh.dyr.data.source.TasksRepository;

/**
 * Created by sylom on 03/06/2016.
 */
public class Injection {

    public static TasksRepository provideTasksRepository(Context cxt) {
        return ((MyApplication) cxt.getApplicationContext()).mTaskRepository;
    }

    public static WifiManager provideWifiManager(Context cxt) {
        return ((MyApplication) cxt.getApplicationContext()).mWifiManager;
    }

    public static ConnectivityManager provideConnectivityManager(Context cxt) {
        return ((MyApplication) cxt.getApplicationContext()).mConnectivityManager;
    }
}
