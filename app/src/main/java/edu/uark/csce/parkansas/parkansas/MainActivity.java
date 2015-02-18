package edu.uark.csce.parkansas.parkansas;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;

import com.facebook.Session;



public class MainActivity extends Activity {

    SharedPreferences sharedPreferences;
    Intent intent;

    //logging in variables
    boolean loggedIn, skipLogin, timer;
    String name, date;

    // PassType variable
//    PassType passType;

    // Map Variables
//    MapKey mapKey;

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = getIntent();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        loggedIn = false;
        skipLogin = sharedPreferences.getBoolean(ActivityUtils.SKIP_LOGIN, false);

        if (!loggedIn && !skipLogin)
            lauchLoginActivity();

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);

        // String provider = LocationManager.GPS_PROVIDER;
        String provider = locationManager.getBestProvider(criteria, true);
        Location loc = locationManager.getLastKnownLocation(provider);

        updateWithNewLocation(loc);
        locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(ActivityUtils.APPTAG, "[Main Activity] onResume");
        skipLogin = sharedPreferences.getBoolean(ActivityUtils.SKIP_LOGIN, false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login: {
                lauchLoginActivity();
                return true;
            }
            case R.id.action_settings: {
                break;
            }
            case R.id.action_exit: {
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivityUtils.LOGIN_RETURN: {
                if (resultCode == Activity.RESULT_OK)
                    break;
                else
                    break;
            }
            default:
                break;
        }
    }

    private void lauchLoginActivity() {
        intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, ActivityUtils.LOGIN_RETURN);
    }

    private void updateWithNewLocation(Location loc) {
            double lat = 36.068654;
            double lng = -94.174635;

            LatLng location = new LatLng(lat, lng);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location,
                    16);
            map.animateCamera(update);
            map.addMarker(new MarkerOptions().position(location).title(
                    "I am here!"));
        }


        LocationListener locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLocationChanged(Location location) {
                updateWithNewLocation(location);
            }
        };
    }