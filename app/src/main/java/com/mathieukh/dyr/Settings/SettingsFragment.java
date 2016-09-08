package com.mathieukh.dyr.Settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.mathieukh.dyr.R;

/**
 * Created by sylom on 28/07/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    public static final String VIBRATOR_PREF_KEY = "pref_vibrator";
    public static final String SOUND_PREF_KEY = "pref_sound";
    public static final String LED_PREF_KEY = "pref_led";
    public static final String AD_PREF_KEY = "pref_ad";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
