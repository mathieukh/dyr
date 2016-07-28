package com.mathieukh.dyr.EditTasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import com.kennyc.view.MultiStateView;
import com.mathieukh.dyr.R;
import com.mathieukh.dyr.data.Task;
import com.mathieukh.dyr.data.source.TasksDataSource;
import com.mathieukh.dyr.data.source.TasksRepository;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by sylom on 31/05/2016.
 */
public class EditTasksPresenter implements EditTasksContract.Presenter {

    private WifiManager mWifiManager;
    private WifiReceiver mBroadcastReceiver;
    private TasksRepository mTasksRepository;
    private EditTasksContract.View mVisualizeListView;
    private WifiInfo mNetwork;

    private List<Task> tasksPres = new ArrayList<>();

    public EditTasksPresenter(@NonNull TasksRepository tasksRepository, @NonNull EditTasksContract.View visualizeListView, @NonNull WifiManager wifiManager) {
        this.mTasksRepository = checkNotNull(tasksRepository);
        this.mVisualizeListView = checkNotNull(visualizeListView);
        this.mWifiManager = checkNotNull(wifiManager);
        mBroadcastReceiver = new WifiReceiver();
        this.mVisualizeListView.setPresenter(this);
    }

    @Override
    public void start() {
        setupView();
    }

    private void setupView() {
        mVisualizeListView.setTitleToolbar(R.string.app_name);
        mNetwork = mWifiManager.getConnectionInfo();
        if (mNetwork != null && mNetwork.getBSSID() != null) {
            mVisualizeListView.displayAddTaskForm(true);
            if (!mNetwork.getSSID().equals(""))
                mVisualizeListView.setTitleToolbar(mNetwork.getSSID());
            loadTasks();
        } else {
            mVisualizeListView.displayAddTaskForm(false);
            mVisualizeListView.setViewState(MultiStateView.VIEW_STATE_ERROR);
            mVisualizeListView.showMessage(R.string.no_wifi);
        }
    }

    @Override
    public void loadTasks() {
        mVisualizeListView.displayAddTaskForm(false);
        mVisualizeListView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        mTasksRepository.getTasks(mNetwork.getBSSID(), new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                tasksPres = tasks;
                mVisualizeListView.showTasks(tasksPres);
                refreshView();
            }

            @Override
            public void onDataNotAvailable() {
                tasksPres = new ArrayList<>();
                mVisualizeListView.showTasks(tasksPres);
                refreshView();
            }
        });
    }

    private void refreshView() {
        mVisualizeListView.displayAddTaskForm(true);
        if (tasksPres.isEmpty()) {
            mVisualizeListView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        } else {
            mVisualizeListView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        }
    }

    @Override
    public boolean onMenuItemClicked(int adapterPosition, int itemID) {
        switch (itemID) {
            case R.id.delete_task:
                try {
                    mVisualizeListView.displayDialog(R.string.delete_task, android.R.string.yes, android.R.string.no, (dialog, which) -> {
                        String taskID = tasksPres.get(adapterPosition).getId();
                        mTasksRepository.deleteTask(taskID);
                        tasksPres.remove(adapterPosition);
                        mVisualizeListView.deleteTask(adapterPosition);
                        refreshView();
                    }, (dialog, which) -> {
                        dialog.cancel();
                    });
                    return true;
                } catch (Exception ignored) {
                }
            case R.id.modify_task:
                try {
                    String taskID = tasksPres.get(adapterPosition).getId();
                    mVisualizeListView.setInputText(tasksPres.get(adapterPosition).getmDescription());
                    mTasksRepository.deleteTask(taskID);
                    tasksPres.remove(adapterPosition);
                    mVisualizeListView.deleteTask(adapterPosition);
                    refreshView();
                    return true;
                } catch (Exception ignored) {
                }
        }
        return false;
    }

    @Override
    public void addTask(String s) {
        if (!s.equalsIgnoreCase("")) {
            Task t = new Task(mNetwork.getBSSID(), s, false);
            mTasksRepository.saveTask(t);
            tasksPres.add(t);
            mVisualizeListView.addedTask(tasksPres.indexOf(t));
        }
        mVisualizeListView.clearForm();
        refreshView();
    }

    @Override
    public BroadcastReceiver getReceiver() {
        return mBroadcastReceiver;
    }

    public class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setupView();
        }
    }
}
