package com.mathieukh.dyr.data.source;

import android.support.annotation.NonNull;

import com.mathieukh.dyr.data.Task;

import java.util.List;

/**
 * Created by sylom on 31/05/2016.
 */
public interface TasksDataSource {

    void getTasks(@NonNull LoadTasksCallback callback);

    void getTasks(@NonNull String BSSIDIdentifier, @NonNull LoadTasksCallback callback);

    void getTasks(@NonNull String BSSIDIdentifier, @NonNull boolean permanentOnes, @NonNull LoadTasksCallback callback);

    void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback);

    void saveTask(@NonNull Task task);

    void refreshTasks();

    void deleteAllTasks(@NonNull String BSSIDIdentifier, @NonNull boolean permanentOnes);

    void deleteTask(@NonNull String taskId);

    interface LoadTasksCallback {

        void onTasksLoaded(List<Task> tasks);

        void onDataNotAvailable();
    }

    interface GetTaskCallback {

        void onTaskLoaded(Task task);

        void onDataNotAvailable();
    }
}