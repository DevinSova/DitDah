package com.neoskeeter.ditdah;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.widget.Toast;

/**
 * Created by Devin (Neoskeeter) Sova on 8/14/17
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_app);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int numOfPreferences = preferenceScreen.getPreferenceCount();

        //Initialize Preference Summaries
        for (int i = 0; i < numOfPreferences; i++) {
            Preference p = preferenceScreen.getPreference(i);
            //Set summaries for all non Switch Preferences
            if(!(p instanceof SwitchPreferenceCompat)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }

        Preference editTextPref = findPreference(getString(R.string.pref_dit_representation_key));
        editTextPref.setOnPreferenceChangeListener(this);
        editTextPref = findPreference(getString(R.string.pref_dah_representation_key));
        editTextPref.setOnPreferenceChangeListener(this);
    }

    private void setPreferenceSummary(Preference preference, String value) {
        //Check if it's a ListPreference (which is a special case to set)
        if(preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value);
            if(index >= 0) {
                listPreference.setSummary(listPreference.getEntries()[index]);
            }
        }
        else
            preference.setSummary(value);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        //Update Summary for that Preference
        Preference preference = findPreference(s);
        if(preference != null) {
            //Only for non Switch Preferences
            if(!(preference instanceof SwitchPreferenceCompat)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        //Currently not set to do anything.
        //This is used if we have a preference where you type in a value.
        //But as of now I don't have any ones like that

        String ditRepKey = getString(R.string.pref_dit_representation_key);
        String dahRepKey = getString(R.string.pref_dah_representation_key);

        if(preference.getKey().equals(ditRepKey)){
            String newDitRep = (String) newValue;
            if(newDitRep.length() != 1) {
                Toast ditError = Toast.makeText(getContext(), "Please enter 1 character to prepresent a \"Dit\" in translation", Toast.LENGTH_LONG);
                ditError.show();
                return false;
            }
        }
        else if(preference.getKey().equals(dahRepKey)) {
            String newDahRep = (String) newValue;
            if(newDahRep.length() != 1) {
                Toast dahError = Toast.makeText(getContext(), "Please enter 1 character to prepresent a \"Dah\" in translation", Toast.LENGTH_LONG);
                dahError.show();
                return false;
            }
        }
        return true;
    }
}
