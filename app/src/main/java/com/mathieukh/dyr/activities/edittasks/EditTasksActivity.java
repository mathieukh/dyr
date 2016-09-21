package com.mathieukh.dyr.activities.edittasks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mathieukh.dyr.R;
import com.mathieukh.dyr.data.Injection;

/**
 * Created by sylom on 24/08/2016.
 */
public class EditTasksActivity extends AppCompatActivity {

    String ssid;
    private Toolbar mToolbar;
    private ViewPager mPager;
    private EditTasksPresenter mEditTasksEnteringPresenter, mEditTasksExitingPresenter;
    private DisplayTasksAdapter mAdapter;
    private TabLayout mTabs;
    private EditTasksFragment fragmentEntering, fragmentExiting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_act);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabs = (TabLayout) findViewById(R.id.tabs);
        if (!getIntent().hasExtra("SSID")) {
            finish();
        } else {
            ssid = getIntent().getStringExtra("SSID");
        }
        setSupportActionBar(mToolbar);
        mAdapter = new DisplayTasksAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mTabs.setupWithViewPager(mPager);
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    public class DisplayTasksAdapter extends FragmentPagerAdapter {

        public DisplayTasksAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {

                case 0:
                    fragmentEntering = (EditTasksFragment) getSupportFragmentManager().findFragmentByTag(makeFragmentName(mPager.getId(), position));
                    if(fragmentEntering == null){
                        fragmentEntering = EditTasksFragment.newInstance();
                    }
                    mEditTasksEnteringPresenter = new EditTasksPresenter(Injection.provideTasksRepository(getApplicationContext()), fragmentEntering, ssid, true);
                    return fragmentEntering;

                case 1:
                    fragmentExiting = (EditTasksFragment) getSupportFragmentManager().findFragmentByTag(makeFragmentName(mPager.getId(), position));
                    if(fragmentExiting == null){
                        fragmentExiting = EditTasksFragment.newInstance();
                    }
                    mEditTasksExitingPresenter = new EditTasksPresenter(Injection.provideTasksRepository(getApplicationContext()), fragmentExiting, ssid, false);
                    return fragmentExiting;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.entering);
                case 1:
                    return getResources().getString(R.string.exiting);
                default:
                    return super.getPageTitle(position);
            }
        }
    }
}
