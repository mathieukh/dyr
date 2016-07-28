package com.mathieukh.dyr.DisplayTasks;

import com.mathieukh.dyr.BasePresenter;
import com.mathieukh.dyr.BaseView;
import com.mathieukh.dyr.data.Task;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sylom on 27/07/2016.
 */
public interface DisplayTasksContract {

    interface View extends BaseView<Presenter> {
        void showMessage(int msgRessource, int length);

        void showMessage(String msg, int length);

        void setTitleToolbar(int StringRessource);

        void setTitleToolbar(String title);

        void showTasks(List<Task> tasks, HashMap<String, Boolean> tasksChecked);

        void displaySnackbar(boolean display);

        void itemChangedAt(int adapterPosition);

        void everyItemChanged();

        void finishActivity();
    }

    interface Presenter extends BasePresenter {
        void onFABClicked();

        void onCheckboxChanged(int adapterPosition, boolean isChecked);

        void onSnackbarActionClicked();
    }
}
