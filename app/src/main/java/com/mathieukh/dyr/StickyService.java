package com.mathieukh.dyr;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by sylom on 17/09/2016.
 */
public class StickyService extends Service {

    private NetworkChangeReceiver mNetworkChangeReceiver;
    private NotificationActionReceiver mNotificationActionReceiver;

    @Override
    public void onCreate() {
        mNetworkChangeReceiver = new NetworkChangeReceiver();
        mNotificationActionReceiver = new NotificationActionReceiver();
        registerReceiver(mNetworkChangeReceiver,
                new IntentFilter("android.net.wifi.STATE_CHANGE"));
        registerReceiver(mNotificationActionReceiver,
                new IntentFilter("com.mathieukh.dyr.done_action_notif"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mNetworkChangeReceiver);
        unregisterReceiver(mNotificationActionReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
