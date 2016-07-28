package com.mathieukh.dyr.EditTasks;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mathieukh.dyr.NetworkChangeReceiver;
import com.mathieukh.dyr.R;
import com.mathieukh.dyr.data.Injection;
import com.mathieukh.dyr.utils.ActivityUtils;

/**
 * Created by sylom on 30/05/2016.
 */
public class EditTasksActivity extends AppCompatActivity {

    private EditTasksPresenter mEditTasksPresenter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_act);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        EditTasksFragment tasksFragment =
                (EditTasksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = EditTasksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }

        // Create the presenter
        mEditTasksPresenter = new EditTasksPresenter(
                Injection.provideTasksRepository(this),
                tasksFragment,
                Injection.provideWifiManager(this));
    }

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter(NetworkChangeReceiver.ACTION_NETWORK_CHANGE);
        registerReceiver(mEditTasksPresenter.getReceiver(), intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mEditTasksPresenter.getReceiver());
        super.onPause();
    }
}
