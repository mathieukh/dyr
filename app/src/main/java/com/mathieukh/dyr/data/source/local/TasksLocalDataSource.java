package com.mathieukh.dyr.data.source.local;

import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.mathieukh.dyr.data.Task;
import com.mathieukh.dyr.data.source.TasksDataSource;

import io.paperdb.Paper;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by sylom on 31/05/2016.
 */
public class TasksLocalDataSource implements TasksDataSource {

    private final static String TASK_BOOK = "task_book";

    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {
        checkNotNull(callback);
        try {
            callback.onTasksLoaded(
                    Stream.of(Paper.book(TASK_BOOK).getAllKeys())
                            .map(k -> (Task) Paper.book(TASK_BOOK).read(k))
                            .collect(Collectors.toList()));
        } catch (Exception ignored) {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getTasks(@NonNull String SSIDIdentifier, @NonNull boolean entering, @NonNull LoadTasksCallback callback) {
        checkNotNull(SSIDIdentifier);
        checkNotNull(callback);
        try {
            callback.onTasksLoaded(
                    Stream.of(Paper.book(TASK_BOOK).getAllKeys())
                            .map(k -> (Task) Paper.book(TASK_BOOK).read(k))
                            .filter(t -> t.getSSIDAssociated().equals(SSIDIdentifier) && t.isEnteringTask() == entering)
                            .collect(Collectors.toList()));
        } catch (Exception ignored) {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {
        checkNotNull(taskId);
        checkNotNull(callback);
        try {
            Task t = Paper.book(TASK_BOOK).read(taskId, null);
            if (t != null)
                callback.onTaskLoaded(t);
            else
                callback.onDataNotAvailable();
        } catch (Exception ignored) {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        Paper.book(TASK_BOOK).write(task.getId(), task);
    }

    @Override
    public void refreshTasks() {
        //Handles in the TasksRepository
    }

    @Override
    public void deleteAllTasks(@NonNull String SSIDIdentifier, boolean isEntering, boolean permanentOnes) {
        Stream.of(Paper.book(TASK_BOOK).getAllKeys())
                .map(k -> (Task) Paper.book(TASK_BOOK).read(k))
                .filter(t -> t.getSSIDAssociated().equals(SSIDIdentifier) && t.isEnteringTask() == isEntering && t.isPermanent() == permanentOnes)
                .map(Task::getId)
                .forEach(s -> Paper.book(TASK_BOOK).delete(s));
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        checkNotNull(taskId);
        Paper.book(TASK_BOOK).delete(taskId);
    }

    @Override
    public void setPermanent(@NonNull String taskId, boolean keep) {
        checkNotNull(taskId);
        Task t = Paper.book(TASK_BOOK).read(taskId, null);
        if (t != null) {
            t.setPermanent(keep);
            Paper.book(TASK_BOOK).write(taskId, t);
        }
    }
}
