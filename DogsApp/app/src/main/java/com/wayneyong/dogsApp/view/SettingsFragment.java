package com.wayneyong.dogsApp.view;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.wayneyong.dogsApp.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.settings, rootKey);
    }

}