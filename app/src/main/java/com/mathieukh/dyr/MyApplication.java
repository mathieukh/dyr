package com.mathieukh.dyr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
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

    public TasksRepository mTaskRepository;
    public WifiManager mWifiManager;
    public ConnectivityManager mConnectivityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);
        mTaskRepository = TasksRepository.getInstance(new TasksLocalDataSource());
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        SharedPreferences sharedpreferences = getSharedPreferences(MyApplication.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        WifiInfo mNetwork = mWifiManager.getConnectionInfo();

        // On ne s'occupe de créer le current_state et last_bssid seulement si il n'existe pas au préalable, sinon le broadcast s'en chargera
        if (sharedpreferences.getInt(NetworkChangeReceiver.CURRENT_STATE, 0) == 0) {
            if (mNetwork != null && mNetwork.getBSSID() != null) {
                //Connexion wifi et bssid dispo:
                //Etat 1 et enregistrement du bssid
                editor.putString(NetworkChangeReceiver.LAST_BSSID_CONNECTED, mNetwork.getBSSID());
                editor.putInt(NetworkChangeReceiver.CURRENT_STATE, 1);
                editor.apply();
            } else {
                //Pas de connexion wifi :
                //Etat 2
                editor.putString(NetworkChangeReceiver.LAST_BSSID_CONNECTED, "");
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
}
