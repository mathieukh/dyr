package com.mathieukh.dyr;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.common.base.Preconditions;
import com.mathieukh.dyr.data.Injection;
import com.mathieukh.dyr.data.source.TasksRepository;
import com.mathieukh.dyr.data.source.local.TasksLocalDataSource;

/**
 * Created by sylom on 28/07/2016.
 */
public class NotificationActionDoneReceiver extends BroadcastReceiver {

    public static final String DONE_ACTION = "com.mathieukh.dyr.done_action_notif";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DONE_ACTION.equals(action)) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            TasksRepository mTasksRepository;
            if (Injection.provideTasksRepository(context) != null)
                mTasksRepository = Injection.provideTasksRepository(context);
            else
                mTasksRepository = TasksRepository.getInstance(new TasksLocalDataSource());
            String ssid = Preconditions.checkNotNull(intent.getStringExtra("SSID"));
            boolean isEntering = intent.getBooleanExtra("ENTERING", false);
            mTasksRepository.deleteAllTasks(ssid, isEntering, false);
            int idNotif = Integer.parseInt("" + (isEntering ? 1 : 0) + ssid.hashCode());
            mNotificationManager.cancel(idNotif);
        }
    }
}
