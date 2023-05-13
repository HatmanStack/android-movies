package gemenielabs.movies;


import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.Preference;

import androidx.preference.PreferenceFragmentCompat;

public class SearchFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_settings);

        Preference popular = findPreference(getString(R.string.popular_key));
        Preference top_rated = findPreference(getString(R.string.top_rated_key));
        Preference favorites = findPreference(getString(R.string.favorites_key));
        popular.setOnPreferenceChangeListener(this);
        top_rated.setOnPreferenceChangeListener(this);
        favorites.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
