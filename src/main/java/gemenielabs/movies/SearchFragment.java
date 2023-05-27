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
        // Inflate preferences from XML resource
        addPreferencesFromResource(R.xml.preference_settings);

        // Set preference change listener for popular, top rated, and favorites preferences
        setPreferenceChangeListener(getString(R.string.popular_key));
        setPreferenceChangeListener(getString(R.string.top_rated_key));
        setPreferenceChangeListener(getString(R.string.favorites_key));
    }

    // Set preference change listener for the given preference key
    private void setPreferenceChangeListener(String preferenceKey) {
        Preference preference = findPreference(preferenceKey);
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // TODO: Handle shared preference changes if needed
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // TODO: Handle preference changes if needed
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register shared preference change listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister shared preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
