package com.mathieukh.dyr.EditTasks;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kennyc.view.MultiStateView;
import com.mathieukh.dyr.R;
import com.mathieukh.dyr.data.Task;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by sylom on 30/05/2016.
 */
public class EditTasksFragment extends Fragment implements EditTasksContract.View {

    MultiStateView mStateView;
    RecyclerView mToDoList;
    TaskAdapter mAdapter;
    private RelativeLayout mFormAddTask;
    private EditText mTaskDescForm;
    private Button mTaskAddButton;
    private EditTasksContract.Presenter mPresenter;

    public EditTasksFragment() {
        // Requires empty public constructor
    }

    public static EditTasksFragment newInstance() {
        return new EditTasksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new TaskAdapter(new ArrayList<>(0));
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(EditTasksContract.Presenter presenter) {
        this.mPresenter = checkNotNull(presenter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_frag, container, false);
        mStateView = (MultiStateView) view.findViewById(R.id.stateView);
        mToDoList = (RecyclerView) view.findViewById(R.id.toDoList);
        mFormAddTask = (RelativeLayout) getActivity().findViewById(R.id.form_add_task);
        mTaskDescForm = (EditText) getActivity().findViewById(R.id.task_desc_edit);
        mTaskAddButton = (Button) getActivity().findViewById(R.id.add_task_button);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToDoList.setAdapter(mAdapter);
        mToDoList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mToDoList.setItemAnimator(new DefaultItemAnimator());
        mTaskDescForm.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                mPresenter.addTask(mTaskDescForm.getText().toString());
                handled = true;
            }
            return handled;
        });
        mTaskDescForm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mTaskDescForm.getText().toString().equals("")) {
                    mTaskAddButton.setEnabled(false);
                    mTaskAddButton.setAlpha((float) 0.26);
                } else {
                    mTaskAddButton.setEnabled(true);
                    mTaskAddButton.setAlpha((float) 1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTaskAddButton.setOnClickListener(v -> mPresenter.addTask(mTaskDescForm.getText().toString()));
    }

    @Override
    public void setViewState(int viewState) {
        mStateView.setViewState(viewState);
    }

    @Override
    public void showTasks(List<Task> tasks) {
        mAdapter.replaceData(tasks);
    }

    @Override
    public void deleteTask(int position) {
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void addedTask(int position) {
        mAdapter.notifyItemInserted(position);
        mToDoList.smoothScrollToPosition(position);
    }

    @Override
    public void modifiedTask(int position) {
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void showMessage(String message) {
        checkNotNull(getView());
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showMessage(int messageRes) {
        checkNotNull(getString(messageRes));
        showMessage(getString(messageRes));
    }

    @Override
    public void displayAddTaskForm(boolean display) {
        if (!display)
            mFormAddTask.setVisibility(View.GONE);
        else
            mFormAddTask.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayDialog(String title, String posButton, String negButton, DialogInterface.OnClickListener posListener, DialogInterface.OnClickListener negListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(title);
        alertDialogBuilder.setPositiveButton(posButton, posListener);
        alertDialogBuilder.setNegativeButton(negButton, negListener);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void displayDialog(int title, int posButton, int negButton, DialogInterface.OnClickListener posListener, DialogInterface.OnClickListener negListener) {
        displayDialog(getString(title), getString(posButton), getString(negButton), posListener, negListener);
    }

    @Override
    public void clearForm() {
        mTaskDescForm.setText("");
    }

    @Override
    public void setInputText(String txt) {
        mTaskDescForm.setText(txt);
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

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView vDesc;
        public TextView vDate;
        public ImageButton vKeep;

        public TaskViewHolder(View v) {
            super(v);
            vDesc = (TextView) v.findViewById(R.id.descriptionTaskTV);
            vDate = (TextView) v.findViewById(R.id.dateTaskTV);
            vKeep = (ImageButton) v.findViewById(R.id.keep_task_button);
        }
    }

    public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {

        private List<Task> taskList;

        public TaskAdapter(List<Task> taskList) {
            this.taskList = taskList;
        }

        public void replaceData(List<Task> tasks) {
            checkNotNull(tasks);
            taskList = tasks;
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
            taskViewHolder.vDate.setText(String.format(getString(R.string.add_prep), DateUtils.getRelativeTimeSpanString(getContext(), task.getmDateModified().getTime(), true)));
            taskViewHolder.itemView.setOnLongClickListener(v1 -> {
                PopupMenu popup = new PopupMenu(taskViewHolder.itemView.getContext(), taskViewHolder.itemView);
                popup.inflate(R.menu.task_menu);
                popup.setOnMenuItemClickListener(item -> mPresenter.onMenuItemClicked(taskViewHolder.getAdapterPosition(), item.getItemId()));
                popup.show();
                return true;
            });
            taskViewHolder.vKeep.setOnClickListener(view -> {
                mPresenter.onKeepTaskClicked(taskViewHolder.getAdapterPosition());
            });
            if(task.isPermanent()){
                taskViewHolder.vKeep.setBackgroundResource(R.color.colorPrimary);
                taskViewHolder.vKeep.setImageResource(R.drawable.ic_pin_white);
            }else {
                taskViewHolder.vKeep.setBackgroundResource(android.R.color.transparent);
                taskViewHolder.vKeep.setImageResource(R.drawable.ic_pin_black);
            }
        }

        @Override
        public TaskViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_task, viewGroup, false);
            return new TaskViewHolder(itemView);
        }
    }

}
