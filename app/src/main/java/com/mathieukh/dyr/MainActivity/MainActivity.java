package com.mathieukh.dyr.MainActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mathieukh.dyr.NetworkChangeReceiver;
import com.mathieukh.dyr.R;
import com.mathieukh.dyr.Settings.SettingsActivity;
import com.mathieukh.dyr.data.Injection;
import com.mathieukh.dyr.utils.ActivityUtils;

/**
 * Created by sylom on 30/05/2016.
 */
public class MainActivity extends AppCompatActivity {

    private DisplayNetworksPresenter mDisplayNetworksPresenter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        DisplayNetworksFragment networksFragment =
                (DisplayNetworksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (networksFragment == null) {
            // Create the fragment
            networksFragment = DisplayNetworksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), networksFragment, R.id.contentFrame);
        }

        // Create the presenter
        mDisplayNetworksPresenter = new DisplayNetworksPresenter(
                networksFragment,
                Injection.provideWifiManager(this));
    }

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter(NetworkChangeReceiver.ACTION_NETWORK_CHANGE);
        registerReceiver(mDisplayNetworksPresenter.getReceiver(), intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mDisplayNetworksPresenter.getReceiver());
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_act_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
