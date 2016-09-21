package com.mathieukh.dyr.activities.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sylom on 28/07/2016.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
