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
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mathieukh.dyr.DisplayTasks.DisplayTasksActivity;
import com.mathieukh.dyr.Settings.SettingsFragment;
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

    public final static String LAST_BSSID_CONNECTED = "lastBssid";
    public final static String CURRENT_STATE = "currentState";


    private void notifyActivities(Context context) {
        Intent i = new Intent();
        i.setAction(ACTION_NETWORK_CHANGE);
        context.sendBroadcast(i);
    }

    private void triggerNotification(Context context, String bssid) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        TasksRepository mTasksRepository;
        if (Injection.provideTasksRepository(context) != null)
            mTasksRepository = Injection.provideTasksRepository(context);
        else
            mTasksRepository = TasksRepository.getInstance(new TasksLocalDataSource());
        mTasksRepository.refreshTasks();
        mTasksRepository.getTasks(bssid, new TasksDataSource.LoadTasksCallback() {
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

                    if(sharedPref.getBoolean(SettingsFragment.VIBRATOR_PREF_KEY, true))
                        mBuilder.setVibrate(new long[]{0,1000});
                    if(sharedPref.getBoolean(SettingsFragment.SOUND_PREF_KEY, true) && am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
                        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    if(sharedPref.getBoolean(SettingsFragment.LED_PREF_KEY, true)) {
                        mBuilder.setLights(Color.RED, 3000, 3000);
                    }

                    // Creates an Intent for the Activity
                    Intent notifyIntent =
                            new Intent(context, DisplayTasksActivity.class);
                    // Sets the Activity to start in a new, empty task
                    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    notifyIntent.putExtra("BSSID", bssid);
                    // Creates the PendingIntent
                    PendingIntent notifyPendingIntent =
                            PendingIntent.getActivity(
                                    context,
                                    0,
                                    notifyIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    // Puts the PendingIntent into the notification builder
                    mBuilder.setContentIntent(notifyPendingIntent);

                    //Creates an Intent for the action
                    Intent doneAction =
                            new Intent();
                    doneAction.putExtra("BSSID", bssid);
                    doneAction.setAction(NotificationActionDoneReceiver.DONE_ACTION);
                    //Creates the PendingIntent
                    PendingIntent pendingIntentDone =
                            PendingIntent.getBroadcast(
                                    context,
                                    12345,
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
                    mNotificationManager.notify(0, mBuildInbox.build());

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

        if (currentState == 0) {
            Log.d("NetworkChangeError", "Une erreur est survenue : Etat inatteignable dans le NetworkChangeReceiver");
        } else {
            switch (currentState) {
                //On était connecté à un réseau wifi
                case 1:
                    //Le réseau wifi précédent est donc forcément noté
                    String bssidPrec = sharedpreferences.getString(LAST_BSSID_CONNECTED, "");
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) {
                        if (intent.getStringExtra(WifiManager.EXTRA_BSSID) != null && !intent.getStringExtra(WifiManager.EXTRA_BSSID).equals(bssidPrec)) {
                            //Si on est connecté à un nouveau réseau wifi que celui sur lequel on était
                            triggerNotification(context, bssidPrec);
                            editor.putString(LAST_BSSID_CONNECTED, intent.getStringExtra(WifiManager.EXTRA_BSSID));
                            editor.apply();
                            notifyActivities(context);
                        }
                    } else {
                        //On était connecté à un réseau wifi mais on ne l'est plus
                        triggerNotification(context, bssidPrec);
                        editor.putString(LAST_BSSID_CONNECTED, "");
                        editor.putInt(CURRENT_STATE, 2);
                        editor.apply();
                        notifyActivities(context);
                    }
                    break;

                //On n'était connecté à aucun réseau wifi
                case 2:
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected() && intent.getStringExtra(WifiManager.EXTRA_BSSID) != null) {
                        //Si on est connecté à un réseau wifi et qu'il est exploitable ( BSSID accessible)
                        editor.putString(LAST_BSSID_CONNECTED, intent.getStringExtra(WifiManager.EXTRA_BSSID));
                        editor.putInt(CURRENT_STATE, 1);
                        editor.apply();
                        notifyActivities(context);
                    }
                    break;
            }
        }
    }
}
