package com.mathieukh.dyr.EditTasks;

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

    private TasksRepository mTasksRepository;
    private EditTasksContract.View mVisualizeListView;
    private String ssid;
    private boolean isEntering;

    private List<Task> tasksPres = new ArrayList<>();

    public EditTasksPresenter(@NonNull TasksRepository tasksRepository, @NonNull EditTasksContract.View visualizeListView, String ssid, boolean isEntering) {
        this.mTasksRepository = checkNotNull(tasksRepository);
        this.mVisualizeListView = checkNotNull(visualizeListView);
        this.ssid = ssid;
        this.isEntering = isEntering;
        this.mVisualizeListView.setPresenter(this);
    }

    @Override
    public void start() {
        mVisualizeListView.setTitleToolbar(ssid.replaceAll("\"", ""));
        loadTasks();
    }

    @Override
    public void loadTasks() {
        mVisualizeListView.displayAddTaskForm(false);
        mVisualizeListView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        mTasksRepository.getTasks(ssid, isEntering, new TasksDataSource.LoadTasksCallback() {
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
            Task t = new Task(ssid, s, false, isEntering);
            mTasksRepository.saveTask(t);
            tasksPres.add(t);
            mVisualizeListView.addedTask(tasksPres.indexOf(t));
        }
        mVisualizeListView.clearForm();
        refreshView();
    }

    @Override
    public void onKeepTaskClicked(int adapterPosition, boolean checked) {
        mTasksRepository.setPermanent(tasksPres.get(adapterPosition).getId(), checked);
        tasksPres.get(adapterPosition).setPermanent(checked);
    }

}
