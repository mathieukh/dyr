package com.mathieukh.dyr.activities.displaytasks;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.common.base.Preconditions;
import com.mathieukh.dyr.R;
import com.mathieukh.dyr.activities.settings.SettingsFragment;
import com.mathieukh.dyr.data.Injection;
import com.mathieukh.dyr.utils.ActivityUtils;

/**
 * Created by sylom on 27/07/2016.
 */
public class DisplayTasksActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DisplayTasksPresenter mDisplayTasksPresenter;
    private AdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_act);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean displayAd = sharedPref.getBoolean(SettingsFragment.AD_PREF_KEY, true);
        if(displayAd){
            MobileAds.initialize(getApplicationContext(), "NOTDEFINED");
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mAdView = (AdView) findViewById(R.id.adView);

        if(displayAd) {
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("DA3E1CD0E0F13C0F02A7D383F65D7594").build();
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    mAdView.setVisibility(View.GONE);
                }
            });
        }else {
            mAdView.setVisibility(View.GONE);
        }

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
    protected void onPause() {
        super.onPause();
        mAdView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdView.destroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mDisplayTasksPresenter.save(outState);
    }
}
