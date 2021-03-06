package com.mathieukh.dyr.data.source;

import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.mathieukh.dyr.data.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by sylom on 31/05/2016.
 */
public class TasksRepository implements TasksDataSource {

    private static TasksRepository INSTANCE = null;

    private final TasksDataSource mTasksLocalDataSource;

    Map<String, Task> mCachedTasks;

    boolean mCacheIsDirty = true;

    private TasksRepository(@NonNull TasksDataSource tasksLocalDataSource) {
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    public static TasksRepository getInstance(TasksDataSource tasksLocalDataSource) {
        if (INSTANCE == null)
            INSTANCE = new TasksRepository(tasksLocalDataSource);
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {
        checkNotNull(callback);
        if (mCachedTasks != null && !mCacheIsDirty) {
            callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
        }
        mTasksLocalDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
                callback.onTasksLoaded(tasks);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getTasks(@NonNull String SSIDIdentifier, @NonNull boolean entering, @NonNull LoadTasksCallback callback) {
        checkNotNull(SSIDIdentifier);
        checkNotNull(callback);

        if (mCachedTasks != null && !mCacheIsDirty) {
            callback.onTasksLoaded(
                    Stream.of(mCachedTasks.values())
                            .filter(t -> t.getSSIDAssociated().equals(SSIDIdentifier) && t.isEnteringTask() == entering)
                            .collect(Collectors.toList()));
            return;
        }
        getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                callback.onTasksLoaded(
                        Stream.of(mCachedTasks.values())
                                .filter(t -> t.getSSIDAssociated().equals(SSIDIdentifier) && t.isEnteringTask() == entering)
                                .collect(Collectors.toList()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {
        checkNotNull(taskId);
        checkNotNull(callback);

        if (mCachedTasks != null && !mCacheIsDirty && mCachedTasks.containsKey(taskId)) {
            callback.onTaskLoaded(mCachedTasks.get(taskId));
            return;
        }
        mTasksLocalDataSource.getTask(taskId, new GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                callback.onTaskLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksLocalDataSource.saveTask(task);

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), task);
    }

    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllTasks(@NonNull String SSIDIdentifier, boolean isEntering, boolean permanentOnes) {
        checkNotNull(SSIDIdentifier);

        mTasksLocalDataSource.deleteAllTasks(SSIDIdentifier, isEntering, permanentOnes);
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks = Stream.of(mCachedTasks)
                .filterNot(s -> s.getValue().getSSIDAssociated().equals(SSIDIdentifier) && s.getValue().isEnteringTask() == isEntering && s.getValue().isPermanent() == permanentOnes)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        checkNotNull(taskId);
        mTasksLocalDataSource.deleteTask(taskId);
        mCachedTasks.remove(taskId);
    }

    @Override
    public void setPermanent(@NonNull String taskId, boolean keep) {
        checkNotNull(taskId);
        mTasksLocalDataSource.setPermanent(taskId, keep);
        if (mCachedTasks.containsValue(taskId)) {
            Task t = mCachedTasks.get(taskId);
            t.setPermanent(keep);
        }
    }

    private void refreshCache(List<Task> tasks) {
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
        for (Task t : tasks) {
            mCachedTasks.put(t.getId(), t);
        }
        mCacheIsDirty = false;
    }
}
