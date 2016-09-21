package com.mathieukh.dyr.activities.displaytasks;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.mathieukh.dyr.R;
import com.mathieukh.dyr.data.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by sylom on 27/07/2016.
 */
public class DisplayTasksFragment extends Fragment implements DisplayTasksContract.View {

    Snackbar snackbarDismiss;
    private RecyclerView mToDoList;
    private FloatingActionButton mTasksDoneButton;
    private CoordinatorLayout mCoordinatorLayout;

    private TaskAdapter mAdapter;
    private DisplayTasksContract.Presenter mPresenter;

    public DisplayTasksFragment() {
        // Requires empty public constructor
    }

    public static DisplayTasksFragment newInstance() {
        return new DisplayTasksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new TaskAdapter(new ArrayList<>(0), new HashMap<>());

    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(DisplayTasksContract.Presenter presenter) {
        this.mPresenter = Preconditions.checkNotNull(presenter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.display_frag, container, false);
        mToDoList = (RecyclerView) view.findViewById(R.id.toDoList);
        mCoordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinatorLayout);
        mTasksDoneButton = (FloatingActionButton) getActivity().findViewById(R.id.fab_tasks_done);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToDoList.setAdapter(mAdapter);
        mToDoList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mToDoList.setItemAnimator(new DefaultItemAnimator());
        mTasksDoneButton.setOnClickListener(view1 -> mPresenter.onFABClicked());
        snackbarDismiss = Snackbar
                .make(mCoordinatorLayout, R.string.all_tasks_marked, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.done_action, view1 -> {
                    mPresenter.onSnackbarActionClicked();
                });
    }


    @Override
    public void showTasks(List<Task> tasks, HashMap<String, Boolean> tasksChecked) {
        mAdapter.replaceData(tasks, tasksChecked);
    }

    @Override
    public void displaySnackbar(boolean display) {
        if (display)
            snackbarDismiss.show();
        else
            snackbarDismiss.dismiss();
    }


    @Override
    public void everyItemChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void finishActivity() {
        getActivity().finish();
    }

    @Override
    public void itemChangedAt(int adapterPosition) {
        mAdapter.notifyItemChanged(adapterPosition);
    }

    @Override
    public void setTitleToolbar(String title) {
        checkNotNull(title);
        checkNotNull(((AppCompatActivity) getActivity()).getSupportActionBar());
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    @Override
    public void setTitleToolbar(int titleRes) {
        checkNotNull(getResources().getString(titleRes));
        setTitleToolbar(getString(titleRes));
    }

    @Override
    public void showMessage(String message, int length) {
        checkNotNull(getView());
        Snackbar.make(getView(), message, length).show();
    }

    @Override
    public void showMessage(int messageRes, int length) {
        checkNotNull(getString(messageRes));
        showMessage(getString(messageRes), length);
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView vDesc;
        public CheckBox vCheckBox;

        public TaskViewHolder(View v) {
            super(v);
            vDesc = (TextView) v.findViewById(R.id.task_desc_edit);
            vCheckBox = (CheckBox) v.findViewById(R.id.task_done_checkbox);
        }
    }

    public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {

        private boolean onBind;

        private List<Task> taskList;
        private HashMap<String, Boolean> tasksChecked;

        public TaskAdapter(List<Task> taskList, HashMap<String, Boolean> tasksChecked) {
            this.taskList = taskList;
            this.tasksChecked = tasksChecked;
        }

        public void replaceData(List<Task> tasks, HashMap<String, Boolean> tasksChecked) {
            checkNotNull(tasks);
            this.taskList = tasks;
            this.tasksChecked = tasksChecked;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }

        @Override
        public void onBindViewHolder(TaskViewHolder taskViewHolder, int i) {
            Task task = taskList.get(taskViewHolder.getAdapterPosition());
            taskViewHolder.vDesc.setText(task.getmDescription());
            taskViewHolder.vCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
                if (!onBind)
                    mPresenter.onCheckboxChanged(taskViewHolder.getAdapterPosition(), b);
            });
            onBind = true;
            taskViewHolder.vCheckBox.setChecked(tasksChecked.get(task.getId()));
            onBind = false;
            if (tasksChecked.get(task.getId())) {
                taskViewHolder.vDesc.setPaintFlags(taskViewHolder.vDesc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                taskViewHolder.vDesc.setPaintFlags(taskViewHolder.vDesc.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }

        @Override
        public TaskViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_task_display, viewGroup, false);
            return new TaskViewHolder(itemView);
        }
    }
}
