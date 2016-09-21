package com.mathieukh.dyr;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mathieukh.dyr.activities.displaytasks.DisplayTasksActivity;
import com.mathieukh.dyr.activities.settings.SettingsFragment;
import com.mathieukh.dyr.data.Injection;
import com.mathieukh.dyr.data.Task;
import com.mathieukh.dyr.data.source.TasksDataSource;
import com.mathieukh.dyr.data.source.TasksRepository;
import com.mathieukh.dyr.data.source.local.TasksLocalDataSource;

import java.util.List;

/**
 * Created by sylom on 19/07/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    public final static String ACTION_NETWORK_CHANGE = "com.mathieukh.dyr.NETWORK_CHANGE";

    public final static String LAST_SSID_CONNECTED = "lastSSID";
    public final static String CURRENT_STATE = "currentState";


    private void notifyActivities(Context context) {
        Intent i = new Intent();
        i.setAction(ACTION_NETWORK_CHANGE);
        context.sendBroadcast(i);
    }

    private void triggerNotification(Context context, String ssid, boolean isEntering) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        TasksRepository mTasksRepository;
        if (Injection.provideTasksRepository(context) != null)
            mTasksRepository = Injection.provideTasksRepository(context);
        else
            mTasksRepository = TasksRepository.getInstance(new TasksLocalDataSource());
        mTasksRepository.refreshTasks();
        mTasksRepository.getTasks(ssid, isEntering, new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                if (!tasks.isEmpty()) {
                    int tasksNumber = tasks.size();
                    String contentText;
                    if (tasksNumber > 1) {
                        contentText = String.format(context.getResources().getString(R.string.notification_content), tasksNumber);
                    } else {
                        contentText = tasks.get(0).getmDescription();
                    }
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setPriority(2)
                                    .setAutoCancel(true)
                                    .setSmallIcon(R.drawable.ic_dyr_notif)
                                    .setContentTitle(context.getResources().getString(R.string.notification_title))
                                    .setContentText(contentText);

                    if (sharedPref.getBoolean(SettingsFragment.VIBRATOR_PREF_KEY, true))
                        mBuilder.setVibrate(new long[]{0, 1000});
                    if (sharedPref.getBoolean(SettingsFragment.SOUND_PREF_KEY, true) && am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
                        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    if (sharedPref.getBoolean(SettingsFragment.LED_PREF_KEY, true)) {
                        mBuilder.setLights(Color.RED, 3000, 3000);
                    }

                    // Creates an Intent for the Activity
                    Intent notifyIntent =
                            new Intent(context, DisplayTasksActivity.class);
                    // Sets the Activity to start in a new, empty task
                    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    notifyIntent.putExtra("SSID", ssid);
                    notifyIntent.putExtra("ENTERING", isEntering);
                    // Creates the PendingIntent

                    int idNotif = Integer.parseInt("" + (isEntering ? 1 : 0) + ssid.hashCode());
                    PendingIntent notifyPendingIntent =
                            PendingIntent.getActivity(
                                    context,
                                    idNotif,
                                    notifyIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    // Puts the PendingIntent into the notification builder
                    mBuilder.setContentIntent(notifyPendingIntent);

                    //Creates an Intent for the action
                    Intent doneAction =
                            new Intent();
                    doneAction.putExtra("SSID", ssid);
                    doneAction.putExtra("ENTERING", isEntering);
                    doneAction.setAction(NotificationActionReceiver.DONE_ACTION);
                    //Creates the PendingIntent
                    PendingIntent pendingIntentDone =
                            PendingIntent.getBroadcast(
                                    context,
                                    idNotif,
                                    doneAction,
                                    PendingIntent.FLAG_UPDATE_CURRENT);

                    if (tasksNumber > 1)
                        mBuilder.addAction(R.drawable.ic_done_all_black_24dp, context.getString(R.string.done_action), pendingIntentDone);
                    else
                        mBuilder.addAction(R.drawable.ic_done_black_24dp, context.getString(R.string.done_action), pendingIntentDone);


                    NotificationCompat.InboxStyle mBuildInbox = new NotificationCompat.InboxStyle(mBuilder);
                    for (Task t : tasks) {
                        mBuildInbox = mBuildInbox.addLine("- " + t.getmDescription());
                    }
                    if (tasksNumber > 1) {
                        mBuildInbox.setSummaryText(mBuilder.mContentText);
                    }
                    mNotificationManager.notify(idNotif, mBuildInbox.build());

                }
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyApplication.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        int currentState = sharedpreferences.getInt(CURRENT_STATE, 0);
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

        notifyActivities(context);

        if (currentState == 0) {
            Log.d("NetworkChangeError", "Une erreur est survenue : Etat inatteignable dans le NetworkChangeReceiver");
        } else {
            switch (currentState) {
                //On était connecté à un réseau wifi
                case 1:
                    //Le réseau wifi précédent est donc forcément noté
                    String ssidPrec = sharedpreferences.getString(LAST_SSID_CONNECTED, "");
                    if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) {
                        WifiInfo network = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                        if (!network.getSSID().equals("") && !network.getSSID().equals(ssidPrec)) {
                            //Si on est connecté à un nouveau réseau wifi que celui sur lequel on était
                            triggerNotification(context, ssidPrec, false);
                            editor.putString(LAST_SSID_CONNECTED, network.getSSID());
                            editor.apply();
                        }
                    } else {
                        //On était connecté à un réseau wifi mais on ne l'est plus
                        triggerNotification(context, ssidPrec, false);
                        editor.putString(LAST_SSID_CONNECTED, "");
                        editor.putInt(CURRENT_STATE, 2);
                        editor.apply();
                    }
                    break;

                //On n'était connecté à aucun réseau wifi
                case 2:
                    if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) {
                        //Si on est connecté à un réseau wifi et qu'il est exploitable ( SSID accessible)
                        WifiInfo network = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                        if (!network.getSSID().equals("")) {
                            triggerNotification(context, network.getSSID(), true);
                            editor.putString(LAST_SSID_CONNECTED, network.getSSID());
                            editor.putInt(CURRENT_STATE, 1);
                            editor.apply();
                        }
                    }
                    break;
            }
        }
    }
}
