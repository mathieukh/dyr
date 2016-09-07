package com.mathieukh.dyr.DisplayTasks;

import android.os.Bundle;
import android.support.design.widget.Snackbar;

import com.annimon.stream.Stream;
import com.google.common.base.Preconditions;
import com.mathieukh.dyr.R;
import com.mathieukh.dyr.data.Task;
import com.mathieukh.dyr.data.source.TasksDataSource;
import com.mathieukh.dyr.data.source.TasksRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sylom on 27/07/2016.
 */
public class DisplayTasksPresenter implements DisplayTasksContract.Presenter {

    List<Task> tasksPres = new ArrayList<>();
    HashMap<String, Boolean> tasksCheckedPres = new HashMap<>();
    private TasksRepository mTasksRepository;
    private DisplayTasksContract.View mVisualizeListView;
    private String mSSID;
    private boolean isEntering;
    private Bundle savedInstanceState;

    public DisplayTasksPresenter(TasksRepository tasksRepository, DisplayTasksFragment tasksFragment, String ssid, boolean isEntering, Bundle savedInstanceState) {
        this.mTasksRepository = Preconditions.checkNotNull(tasksRepository);
        this.mVisualizeListView = Preconditions.checkNotNull(tasksFragment);
        this.mSSID = ssid;
        this.isEntering = isEntering;
        this.savedInstanceState = savedInstanceState;
        this.mVisualizeListView.setPresenter(this);
    }

    @Override
    public void start() {
        setupView();
        if (savedInstanceState != null)
            restore(savedInstanceState);
    }

    private void setupView() {
        mVisualizeListView.setTitleToolbar(R.string.app_name);
        mTasksRepository.getTasks(mSSID, isEntering, new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                tasksPres = tasks;
                HashMap<String, Boolean> tasksChecked = new HashMap<>();
                for (Task t : tasks)
                    tasksChecked.put(t.getId(), false);
                tasksCheckedPres = tasksChecked;
                mVisualizeListView.showTasks(tasksPres, tasksCheckedPres);
            }

            @Override
            public void onDataNotAvailable() {
                mVisualizeListView.showMessage(R.string.error, Snackbar.LENGTH_INDEFINITE);
            }
        });

    }

    @Override
    public void onFABClicked() {
        if (tasksCheckedPres.containsValue(false)) {
            for (Map.Entry<String, Boolean> entry : tasksCheckedPres.entrySet())
                entry.setValue(true);
            mVisualizeListView.everyItemChanged();
            mVisualizeListView.displaySnackbar(true);
        } else {
            for (Map.Entry<String, Boolean> entry : tasksCheckedPres.entrySet())
                entry.setValue(false);
            mVisualizeListView.everyItemChanged();
            mVisualizeListView.displaySnackbar(false);
        }
    }

    @Override
    public void onCheckboxChanged(int adapterPosition, boolean isChecked) {
        tasksCheckedPres.put(
                tasksPres.get(adapterPosition).getId(),
                isChecked);
        mVisualizeListView.itemChangedAt(adapterPosition);

        if (tasksCheckedPres.containsValue(false))
            mVisualizeListView.displaySnackbar(false);
        else
            mVisualizeListView.displaySnackbar(true);
    }

    @Override
    public void onSnackbarActionClicked() {
        mTasksRepository.deleteAllTasks(mSSID, isEntering, false);
        mVisualizeListView.finishActivity();
    }

    @Override
    public void save(Bundle outState) {
        Stream.of(tasksCheckedPres)
                .forEach(value -> outState.putBoolean(value.getKey(), value.getValue()));
    }

    @Override
    public void restore(Bundle savedInstanceState) {
        for (String key : savedInstanceState.keySet()) {
            tasksCheckedPres.put(key, savedInstanceState.getBoolean(key));
        }
        mVisualizeListView.everyItemChanged();
        if (tasksCheckedPres.containsValue(false))
            mVisualizeListView.displaySnackbar(false);
        else
            mVisualizeListView.displaySnackbar(true);
    }
}
