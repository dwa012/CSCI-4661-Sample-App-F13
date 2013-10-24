package edu.uno.csci4661.grocerylist.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import java.util.List;

import edu.uno.csci4661.grocerylist.R;

public class SettingsActivity extends PreferenceActivity {


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

//        setupSimplePreferencesScreen();
    }


    /** {@inheritDoc} */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);

//        if (!isSimplePreferences(this)) {
//            loadHeadersFromResource(R.xml.pref_headers, target);
//        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }
    }

    // These are used to create a flat view of preferences
    private void setupSimplePreferencesScreen() {
//        if (!isSimplePreferences(this)) {
//            return;
//        }
//
//        // Add 'general' preferences.
//        addPreferencesFromResource(R.xml.pref_general);
    }

//    @Override
//    public boolean onIsMultiPane() {
//        return isXLargeTablet(this) && !isSimplePreferences(this);
//    }
//
//    private static boolean isXLargeTablet(Context context) {
//        return (context.getResources().getConfiguration().screenLayout
//        & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
//    }
//
//    private static boolean isSimplePreferences(Context context) {
//        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
//                || !isXLargeTablet(context);
//    }

}
