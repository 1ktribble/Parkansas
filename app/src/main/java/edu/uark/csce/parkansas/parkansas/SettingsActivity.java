package edu.uark.csce.parkansas.parkansas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import java.util.Set;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On 
 * handset devices, settings are presented as a single list. On tablets, 
 * settings are split by category, with category headers shown to the left of 
 * the list of settings. 
 * <p/> 
 * See <a href="http://developer.android.com/design/patterns/settings.html"> 
 * Android Design: Settings</a> for design guidelines and the <a 
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings 
 * API Guide</a> for more information on developing a Settings UI. 
 */
public class SettingsActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */

    SharedPreferences sharedPreferences;
    ListPreference classificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        classificationList = new ListPreference(this);
        sharedPreferences = android.preference.PreferenceManager.
                getDefaultSharedPreferences(getBaseContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        retrieveSharedPrefs();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }

        if (pref instanceof MultiSelectListPreference) {
            StringBuilder sb = new StringBuilder();

            MultiSelectListPreference multiSelectListPreference = (MultiSelectListPreference) pref;
            Set<String> entryValues = multiSelectListPreference.getValues();

            int i = entryValues.size();

            for (String selection : entryValues) {
                sb.append(selection);
                if(i != 1)
                    sb.append(", ");
                if(i%3 == 0 && multiSelectListPreference.getKey() == "prefUserPass")
                    sb.append("\n");
                i--;
            }
            pref.setSummary(sb.toString());
        }
    }

    private void retrieveSharedPrefs(){
//        Intent serviceIntent = new Intent(getApplicationContext(),
//                ParkansasNotificationService.class);

            Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    ActivityUtils.classificationSelection = newValue.toString();
                    return false;
                }
            };
            classificationList.setOnPreferenceChangeListener(listener);

//            if (sharedPreferences.getBoolean("prefNotificationSwitch", true)) {
//                Set<String> selections = sharedPreferences.getStringSet("prefNotificationType", null);
//                //           String[] selected = selections.toArray(new String[] {});
//                if(selections != null)
//                    for (String s : selections) {
//                        if (s.equals(getString(R.string.time_expiration))) {
//                        //    ActivityUtils.timeExpirationNotificationOn = true;
//                        // use SharedPreferences instead of Globals
//                            sharedPreferences.edit().putBoolean(ActivityUtils.TIME_EXPIRATION_ALERT, true)
//                                    .apply();
//                            ActivityUtils.atLeastOneNotificationChecked = true;
//                        }
//                        if (s.equals(getString(R.string.free_parking))) {
//                        //    ActivityUtils.freeParkingNotificationOn = true;
//                            sharedPreferences.edit().putBoolean(ActivityUtils.FREE_PARKING_ALERT, true)
//                                    .apply();
//                            ActivityUtils.atLeastOneNotificationChecked = true;
//                        }
//                        if (s.equals(getString(R.string.wake_up_call))) {
//                        //    ActivityUtils.wakeUpCallOn = true;
//                            sharedPreferences.edit().putBoolean(ActivityUtils.WAKEUP_ALERT, true)
//                                    .apply();
//                            ActivityUtils.atLeastOneNotificationChecked = true;
//                        }
//                        if (s.equals(getString(R.string.pre_game_day))) {
//                        //    ActivityUtils.gameDayNotificationOn = true;
//                            sharedPreferences.edit().putBoolean(ActivityUtils.GAMEDAY_ALERT, true)
//                                    .apply();
//                            ActivityUtils.atLeastOneNotificationChecked = true;
//                        }
//                        if (s.equals(getString(R.string.harmon_notification))) {
//                        //    ActivityUtils.harmonNotificationOn = true;
//                            sharedPreferences.edit().putBoolean(ActivityUtils.HARMON_ALERT, true)
//                                    .apply();
//                            ActivityUtils.atLeastOneNotificationChecked = true;
//                        }
//                    }
//            }

            Set<String> selections = sharedPreferences.getStringSet("prefUserPass", null);
            //           String[] selected = selections.toArray(new String[] {});
            if(selections != null)

                for (String s : selections) {
                    if (s.equals(getString(R.string.resident_reserved_text))) {
                    //    ActivityUtils.hasResidentReservedPass = true;
                        sharedPreferences.edit().putBoolean(ActivityUtils.RESIDENT_RESERVED, true)
                                .apply();
                    }
                    if (s.equals(getString(R.string.reserved_blue_text))) {
                    //    ActivityUtils.hasReservedPass = true;
                        sharedPreferences.edit().putBoolean(ActivityUtils.RESERVED_PASS, true)
                                .apply();
                    }
                    if (s.equals(getString(R.string.reserved_faculty_text))) {
                    //    ActivityUtils.hasFacultyPass = true;
                        sharedPreferences.edit().putBoolean(ActivityUtils.FACULTY_PASS, true)
                                .apply();
                    }
                    if (s.equals(getString(R.string.reserved_green_text))) {
                    //    ActivityUtils.hasStudentPass = true;
                        sharedPreferences.edit().putBoolean(ActivityUtils.STUDENT_PASS, true)
                                .apply();
                    }
                    if (s.equals(getString(R.string.harmon_pass_text))) {
                    //    ActivityUtils.hasHarmonPass = true;
                        sharedPreferences.edit().putBoolean(ActivityUtils.HARMON_PASS, true)
                                .apply();
                    }
                    if (s.equals(getString(R.string.remote_pass_text))) {
                    //    ActivityUtils.hasRemotePass = true;
                        sharedPreferences.edit().putBoolean(ActivityUtils.REMOTE_PASS, true)
                                .apply();
                    }
                    if (s.equals(getString(R.string.handicap_parking_text))) {
                    //    ActivityUtils.hasHandicapPass = true;
                        sharedPreferences.edit().putBoolean(ActivityUtils.ADA_PASS, true)
                                .apply();
                    }
                }

        Toast.makeText(getApplicationContext(), "Settings Saved", Toast.LENGTH_LONG).show();

    }



}