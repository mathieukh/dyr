package com.mathieukh.dyr.DisplayTasks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.common.base.Preconditions;
import com.mathieukh.dyr.R;
import com.mathieukh.dyr.data.Injection;
import com.mathieukh.dyr.utils.ActivityUtils;

/**
 * Created by sylom on 27/07/2016.
 */
public class DisplayTasksActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DisplayTasksPresenter mDisplayTasksPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_act);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (!getIntent().hasExtra("SSID") || !getIntent().hasExtra("ENTERING"))
            finish();
        String ssid = Preconditions.checkNotNull(getIntent().getStringExtra("SSID"));
        boolean isEntering = getIntent().getBooleanExtra("ENTERING", false);
        setSupportActionBar(mToolbar);

        DisplayTasksFragment tasksFragment =
                (DisplayTasksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = DisplayTasksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }
        // Create the presenter
        mDisplayTasksPresenter = new DisplayTasksPresenter(
                Injection.provideTasksRepository(this),
                tasksFragment, ssid, isEntering, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mDisplayTasksPresenter.save(outState);
    }
}
