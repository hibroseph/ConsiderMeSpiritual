package joellc.considermespiritual;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * This is where the settings are hosted
 * TODO: See how I can implement PreferenceFragmentCompat here
 */
public class PreferencesFragment extends PreferenceFragment {

    String TAG = getClass().getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        // Load new quotes
        Preference newQuotesButton = (Preference) findPreference(getString(R.string.setting_key_new_quotes));
        newQuotesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Log.d(TAG, "You clicked to see if there are new quotes available");

                // TODO: Add code to check to see if there are quotes available to download

                return false;
            }
        });

        // Delete all quotes
        Preference deleteQuotesButton = (Preference) findPreference(getString(R.string.setting_key_delete));

        deleteQuotesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "You clicked to delete all the quotes");

                // TODO: Add code to delete all quotes from database

                return false;
            }
        });


    }

}
