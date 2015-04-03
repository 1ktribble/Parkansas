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
        Intent serviceIntent = new Intent(getApplicationContext(),
                ParkansasNotificationService.class);

            Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    ActivityUtils.classificationSelection = newValue.toString();
                    return false;
                }
            };
            classificationList.setOnPreferenceChangeListener(listener);

            if (sharedPreferences.getBoolean("prefNotificationSwitch", true)) {
                Set<String> selections = sharedPreferences.getStringSet("prefNotificationType", null);
                //           String[] selected = selections.toArray(new String[] {});
                if(selections != null)
                    for (String s : selections) {
                        if (s.equals(getString(R.string.time_expiration))) {
                            ActivityUtils.timeExpirationNotificationOn = true;
                            ActivityUtils.atLeastOneNotificationChecked = true;
                        }
                        if (s.equals(getString(R.string.free_parking))) {
                            ActivityUtils.freeParkingNotificationOn = true;
                            ActivityUtils.atLeastOneNotificationChecked = true;
                        }
                        if (s.equals(getString(R.string.wake_up_call))) {
                            ActivityUtils.wakeUpCallOn = true;
                            ActivityUtils.atLeastOneNotificationChecked = true;
                        }
                        if (s.equals(getString(R.string.pre_game_day))) {
                            ActivityUtils.gameDayNotificationOn = true;
                            ActivityUtils.atLeastOneNotificationChecked = true;
                        }
                        if (s.equals(getString(R.string.harmon_notification))) {
                            ActivityUtils.harmonNotificationOn = true;
                            ActivityUtils.atLeastOneNotificationChecked = true;
                        }
                    }
            }

            Set<String> selections = sharedPreferences.getStringSet("prefUserPass", null);
            //           String[] selected = selections.toArray(new String[] {});
            if(selections != null)

                for (String s : selections) {
                    if (s.equals(getString(R.string.resident_reserved_text))) {
                        ActivityUtils.hasResidentReservedPass = true;
                    }
                    if (s.equals(getString(R.string.reserved_blue_text))) {
                        ActivityUtils.hasReservedPass = true;
                    }
                    if (s.equals(getString(R.string.reserved_faculty_text))) {
                        ActivityUtils.hasFacultyPass = true;
                    }
                    if (s.equals(getString(R.string.reserved_green_text))) {
                        ActivityUtils.hasStudentPass = true;
                    }
                    if (s.equals(getString(R.string.harmon_pass_text))) {
                        ActivityUtils.hasHarmonPass = true;
                    }
                    if (s.equals(getString(R.string.remote_pass_text))) {
                        ActivityUtils.hasRemotePass = true;
                    }
                    if (s.equals(getString(R.string.handicap_parking_text))) {
                        ActivityUtils.hasHandicapPass = true;
                    }
                }
        if(ActivityUtils.serviceOn && sharedPreferences.getBoolean("prefNotificationSwitch", false)){
            this.stopService(serviceIntent);
            ActivityUtils.serviceOn = false;
 //           doUnbindService();
        }

        Toast.makeText(getApplicationContext(), "Settings Saved", Toast.LENGTH_LONG).show();

    }

//    private ParkansasNotificationService mBoundService;
//
//    private ServiceConnection mConnection = new ServiceConnection() {
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            // This is called when the connection with the service has been
//            // established, giving us the service object we can use to
//            // interact with the service.  Because we have bound to a explicit
//            // service that we know is running in our own process, we can
//            // cast its IBinder to a concrete class and directly access it.
//            mBoundService = ((ParkansasNotificationService.LocalBinder)service).getService();
//
//            // Tell the user about this for our demo.
//            Toast.makeText(SettingsActivity.this, "Connected", Toast.LENGTH_SHORT).show();
//        }
//
//        public void onServiceDisconnected(ComponentName className) {
//            // This is called when the connection with the service has been
//            // unexpectedly disconnected -- that is, its process crashed.
//            // Because it is running in our same process, we should never
//            // see this happen.
//            mBoundService = null;
//            Toast.makeText(SettingsActivity.this, "Service Disconnected", Toast.LENGTH_SHORT).show();
//        }
//    };
//
//    void doBindService() {
//        // Establish a connection with the service.  We use an explicit
//        // class name because we want a specific service implementation that
//        // we know will be running in our own process (and thus won't be
//        // supporting component replacement by other applications).
//        bindService(new Intent(SettingsActivity.this,
//                ParkansasNotificationService.class), mConnection, Context.BIND_AUTO_CREATE);
//        mIsBound = true;
//    }
//
//    void doUnbindService() {
//        if (mIsBound) {
//            // Detach our existing connection.
//            unbindService(mConnection);
//            mIsBound = false;
//        }
//    }

}