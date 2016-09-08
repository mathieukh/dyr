package com.mathieukh.dyr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.mathieukh.dyr.data.source.TasksRepository;
import com.mathieukh.dyr.data.source.local.TasksLocalDataSource;

import io.paperdb.Paper;

/**
 * Created by sylom on 30/05/2016.
 */
public class MyApplication extends Application {

    public final static String APP_PREFERENCES = "appPreferences";
    public final static String PINNED_TASK_TUTO = "pinnedTutoTask";

    public TasksRepository mTaskRepository;
    public WifiManager mWifiManager;
    public boolean pinnedTasksTuto;

    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);
        mTaskRepository = TasksRepository.getInstance(new TasksLocalDataSource());
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        SharedPreferences sharedpreferences = getSharedPreferences(MyApplication.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        WifiInfo mNetwork = mWifiManager.getConnectionInfo();

        pinnedTasksTuto = sharedpreferences.getBoolean(PINNED_TASK_TUTO, false);
        // On ne s'occupe de créer le current_state et last_bssid seulement si il n'existe pas au préalable, sinon le broadcast s'en chargera
        if (sharedpreferences.getInt(NetworkChangeReceiver.CURRENT_STATE, 0) == 0) {
            if (mNetwork != null && mNetwork.getSSID() != null) {
                //Connexion wifi et bssid dispo:
                //Etat 1 et enregistrement du bssid
                editor.putString(NetworkChangeReceiver.LAST_SSID_CONNECTED, mNetwork.getSSID());
                editor.putInt(NetworkChangeReceiver.CURRENT_STATE, 1);
                editor.apply();
            } else {
                //Pas de connexion wifi :
                //Etat 2
                editor.putString(NetworkChangeReceiver.LAST_SSID_CONNECTED, "");
                editor.putInt(NetworkChangeReceiver.CURRENT_STATE, 2);
                editor.apply();
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        TasksRepository.destroyInstance();
    }

    public void mark(String TUTO_TAG) {
        SharedPreferences sharedpreferences = getSharedPreferences(MyApplication.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if(TUTO_TAG.equals(PINNED_TASK_TUTO)){
            pinnedTasksTuto = true;
            editor.putBoolean(PINNED_TASK_TUTO, true);
            editor.apply();
        }
    }
}
