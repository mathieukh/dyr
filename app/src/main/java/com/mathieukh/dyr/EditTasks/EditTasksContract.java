package com.mathieukh.dyr.EditTasks;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;

import com.mathieukh.dyr.BasePresenter;
import com.mathieukh.dyr.BaseView;
import com.mathieukh.dyr.data.Task;

import java.util.List;

/**
 * Created by sylom on 30/05/2016.
 */
public interface EditTasksContract {

    interface View extends BaseView<Presenter> {
        void setViewState(int viewState);

        void showTasks(List<Task> tasks);

        void deleteTask(int position);

        void addedTask(int position);

        void modifiedTask(int position);

        void showMessage(String message);

        void setTitleToolbar(String title);

        void setTitleToolbar(int titleRes);

        void showMessage(int messageRes);

        void displayAddTaskForm(boolean display);

        void displayDialog(String title, String posButton, String negButton, DialogInterface.OnClickListener posListener, DialogInterface.OnClickListener negListener);

        void displayDialog(int title, int posButton, int negButton, DialogInterface.OnClickListener posListener, DialogInterface.OnClickListener negListener);

        void clearForm();

        void setInputText(String txt);
    }

    interface Presenter extends BasePresenter {
        void loadTasks();

        boolean onMenuItemClicked(int adapterPosition, int itemId);

        void addTask(String s);

        BroadcastReceiver getReceiver();

        void onKeepTaskClicked(int adapterPosition);
    }
}
