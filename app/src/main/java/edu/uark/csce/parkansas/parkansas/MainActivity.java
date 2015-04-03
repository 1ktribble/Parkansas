package edu.uark.csce.parkansas.parkansas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements
        ToolbarHomeFragment.OnToolbarHomeFragmentClickedListener,
        ToolbarFiltersFragment.OnToolbarFiltersFragmentClickedListener,
        ToolbarFilterOptionsFragment.OnToolbarFilterOptionsFragmentClickedListener,
        ToolbarLotFragment.OnToolbarLotFragmentClickedListener,
        ToolbarLotExtraFragment.OnToolbarLotExtraFragmentClickedListener{

    //  Location Services
    LocationManager lm;
    Criteria criteria;
    private GoogleMap map;
    double latA, lngA;
    String streetAddress;
    ArrayList<Lot> lots;
    ArrayList<Marker> garages;
    ArrayList<Polygon> polygons;
    BooleansWithTags colorList, timesList, otherList;
    LatLngBounds centerBounds;
    Marker marker;

    SharedPreferences sharedPreferences;
//    ListPreference classificationList;
//    NotificationManager notificationManager;
    boolean mIsBound, timeSet, serviceOn, atLeastOneNotificationChecked;

    String popDownFragment = "no";  //current fragment in the popDown frame, "no" == empty
    int selectedIndex;              //index of selected Lot
    boolean moving = false;
    float x,y = 0.0f;
    ArrayList<LatLng> poss = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    int hour, minute;
    TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        intent = getIntent();

        GetData test = new GetData(new GetData.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(JSONObject products) {
                taskCompleted(products);
            }
        });

        boolean networkConnectionAvailable = haveNetworkConnection();
        boolean connected = test.isConnected(this, networkConnectionAvailable);

        if(!connected){
            alertDialogShow(this);
        }

        /*
        Fragment fragment = new LegendFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container3, fragment);
        ft.commit();


        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.container3);
        frameLayout.setX(200);
        frameLayout.setY(200);


        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                Log.i("hello", "hello");
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        moving = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (moving) {
                            x = arg1.getRawX() - frameLayout.getWidth()/2;
                            y = arg1.getRawY() - frameLayout.getHeight()*3/2;
                            frameLayout.setX(x);
                            frameLayout.setY(y);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        moving = false;
                        break;
                }
                return true;
            }
        });
*/
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        classificationList = new ListPreference(this);
//        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        timePicker = new TimePicker(this);
        timeSet = false; serviceOn = false; atLeastOneNotificationChecked = false;

    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_refresh:
                finish();
                startActivity(getIntent());
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_help:
                return true;
            case R.id.action_alerts:
                startActivity(new Intent(this, ResultActivity.class));
                return true;
            case R.id.action_exit:
                finish();
                return true;
        }
        return false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Retrieves SharedPreference value every time the app reloads.
        // NotificationHandler notificationHandler = new NotificationHandler(this);
  //      retrieveSharedPrefs();

        if(lm != null)
            getLocation();

        Intent serviceIntent = new Intent(getApplicationContext(),
                ParkansasNotificationService.class);

        if(sharedPreferences != null){
            if(sharedPreferences.getBoolean("prefNotificationSwitch", false)
                    && ActivityUtils.atLeastOneNotificationChecked){
                serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                serviceIntent.putExtra(ActivityUtils.ON_CAMPUS_BOOL, ActivityUtils.onCampus);
                this.startService(serviceIntent);
//            if(!mIsBound)
//                doBindService();
                ActivityUtils.serviceOn = true;
            }
        }
    }

//    private void retrieveSharedPrefs(){
//        if(sharedPreferences != null) {
//
//            Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    ActivityUtils.classificationSelection = newValue.toString();
//                    return false;
//                }
//            };
//            classificationList.setOnPreferenceChangeListener(listener);
//
//            if (sharedPreferences.getBoolean("prefNotificationSwitch", true)) {
//                Set<String> selections = sharedPreferences.getStringSet("prefNotificationType", null);
//                //           String[] selected = selections.toArray(new String[] {});
//                if(selections != null)
//                for (String s : selections) {
//                    if (s.equals(getString(R.string.time_expiration))) {
//                        ActivityUtils.timeExpirationNotificationOn = true;
//                        atLeastOneNotificationChecked = true;
//                    }
//                    if (s.equals(getString(R.string.free_parking))) {
//                        ActivityUtils.freeParkingNotificationOn = true;
//                        atLeastOneNotificationChecked = true;
//                    }
//                    if (s.equals(getString(R.string.wake_up_call))) {
//                        ActivityUtils.wakeUpCallOn = true;
//                        atLeastOneNotificationChecked = true;
//                    }
//                    if (s.equals(getString(R.string.pre_game_day))) {
//                        ActivityUtils.gameDayNotificationOn = true;
//                        atLeastOneNotificationChecked = true;
//                    }
//                    if (s.equals(getString(R.string.harmon_notification))) {
//                        ActivityUtils.harmonNotificationOn = true;
//                        atLeastOneNotificationChecked = true;
//                    }
//                }
//            }
//            else{
//                Intent serviceIntent = new Intent(getApplicationContext(),
//                        ParkansasNotificationService.class);
//                this.stopService(serviceIntent);
//                serviceOn = false;
//            }
//
//            Set<String> selections = sharedPreferences.getStringSet("prefUserPass", null);
//            //           String[] selected = selections.toArray(new String[] {});
//            if(selections != null)
//
//                for (String s : selections) {
//                if (s.equals(getString(R.string.resident_reserved_text))) {
//                    ActivityUtils.hasResidentReservedPass = true;
//                }
//                if (s.equals(getString(R.string.reserved_blue_text))) {
//                    ActivityUtils.hasReservedPass = true;
//                }
//                if (s.equals(getString(R.string.reserved_faculty_text))) {
//                    ActivityUtils.hasFacultyPass = true;
//                }
//                if (s.equals(getString(R.string.reserved_green_text))) {
//                    ActivityUtils.hasStudentPass = true;
//                }
//                if (s.equals(getString(R.string.harmon_pass_text))) {
//                    ActivityUtils.hasHarmonPass = true;
//                }
//                if (s.equals(getString(R.string.remote_pass_text))) {
//                    ActivityUtils.hasRemotePass = true;
//                }
//                if (s.equals(getString(R.string.handicap_parking_text))) {
//                    ActivityUtils.hasHandicapPass = true;
//                }
//            }
//        }
//    }

    private void openSettings() {
            @SuppressWarnings("rawtypes")
            Intent i = new Intent(this, SettingsActivity.class);

            startActivity(i);
     }

    private void alertDialogShow(Context context){
        final AlertDialog.Builder adb = new AlertDialog.Builder(context);

        AlertDialog ad = adb.create();
        ad.setMessage("Parkansas failed to launch. Check cellular or Wi-Fi connection.");
        ad.show();
    }

    //returns true if tap point is inside polygon defined by arraylist
    private boolean isPointInPolygon(LatLng touchPoint, ArrayList<LatLng> vertices) {
        //algorithm casts a ray to the right from the touch point and counts-
        //  intersections with the polygon by the ray.
        //assumes list of points isn't closed, ie first and last point is the same
        Boolean intersectCountIsEven = false;
        for(int j = 0; j < vertices.size() - 1; j++) {
            if( rayCast(touchPoint, vertices.get(j), vertices.get(j + 1)) ) {
                intersectCountIsEven = !intersectCountIsEven;
            }
        }

        //do the last segment since our list of points isn't closed
        if( rayCast(touchPoint, vertices.get(vertices.size() - 1), vertices.get(0)) ) {
            intersectCountIsEven = !intersectCountIsEven;
        }

        return intersectCountIsEven;
    }

    //casts a ray from touch point to the right, returns true if intersects with segment vertA->vertB
    private boolean rayCast(LatLng touch, LatLng vA, LatLng vB) {

        double aY = vA.latitude;
        double bY = vB.latitude;
        double aX = vA.longitude;
        double bX = vB.longitude;
        double tY = touch.latitude;
        double tX = touch.longitude;

        if ( (aY>tY && bY>tY) || (aY<tY && bY<tY) || (aX<tX && bX<tX) ) {
            return false;//if both points of the segment are above, below or to the left of the tap
        }

        if(aX == bX){//infinite slope, straight up and down
            return true;//since one point is to the right, both are
        }

        double m = (aY-bY) / (aX-bX);//rise over run
        double b = (-aX) * m + aY;//y = mx +b -> b = -mx + y
        double x = (tY - b) / m;// x = (y - b) / m

        return x > tX;//is the tap to the left of the line segment
    }

    //Creates Fragments and add them to map

    private void addToolbarHomeFragment() {
        Fragment fragment = new ToolbarHomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container1, fragment);
        ft.commit();
    }

    private void addToolbarFiltersFragment() {
        Fragment fragment = new ToolbarFiltersFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container1, fragment);
        ft.commit();
    }

    private void addToolbarFilterOptionsFragment(Bundle bundle) {
        Fragment fragment = new ToolbarFilterOptionsFragment();
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container2, fragment, "popDown");
        ft.commit();
    }

    private void addToolbarLotFragment(Bundle bundle) {
        Fragment fragment = new ToolbarLotFragment();
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container1, fragment);
        ft.commit();
    }

    private void addToolbarLotExtraFragment(Bundle bundle) {
        Fragment fragment = new ToolbarLotExtraFragment();
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container2, fragment, "popDown");
        ft.commit();
    }

    //removes fragment with popDown tag, if it is nonempty.
    private void removePopDownFragment() {
        if(!popDownFragment.equals("no")) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(getSupportFragmentManager().findFragmentByTag("popDown"));
            ft.commit();
            popDownFragment = "no";
        }
    }

    //Listeners Called When Fragment is clicked, handle the tag of the button

    @Override
    public void onToolbarHomeFragmentClicked(String tag) {
        if(tag.equals("centerMap")){
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(centerBounds, 1));
        }else if(tag.equals("lotFilters")){
            addToolbarFiltersFragment();
        }
    }

    @Override
    public void onToolbarFiltersFragmentClicked(String tag) {

        if(tag.equals(popDownFragment)){
            removePopDownFragment();
        }else if(tag.equals("color")){
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("list", colorList.tags);
            bundle.putBooleanArray("booleans", colorList.getPrimitive());
            addToolbarFilterOptionsFragment(bundle);
            popDownFragment = tag;
        }else if(tag.equals("times")){
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("list", timesList.tags);
            bundle.putBooleanArray("booleans", timesList.getPrimitive());
            addToolbarFilterOptionsFragment(bundle);
            popDownFragment = tag;
        }else if(tag.equals("back")){
            removePopDownFragment();
            addToolbarHomeFragment();
        }
    }

    @Override
    public void onToolbarFilterOptionsFragmentClicked(String tag) {
        if(popDownFragment.equals("color")) {
            int index = colorList.getIndexOf(tag);
            Log.i("index", String.valueOf(index));
            Log.i("tag", tag);
            colorList.flip(index);
            if(colorList.booleans.get(index)) {
                for (int i = 0; i < lots.size(); i++) {
                    if (lots.get(i).getColor().equals(tag)
                            && timesList.isTrue(lots.get(i).getTime())
                            && otherList.isTrue(lots.get(i).getOther())) {

                        polygons.get(i).setVisible(true);
                    }
                }
            }else{
                for (int i = 0; i < lots.size(); i++) {
                    if (lots.get(i).getColor().equals(tag)) {
                        polygons.get(i).setVisible(false);
                    }
                }
            }
        }else if(popDownFragment.equals("times")) {
            int index = timesList.getIndexOf(tag);
            timesList.flip(index);
            if(timesList.booleans.get(index)) {
                for (int i = 0; i < lots.size(); i++) {
                    if (lots.get(i).getTime().equals(tag)
                            && otherList.isTrue(lots.get(i).getOther())
                            && colorList.isTrue(lots.get(i).getColor())) {

                        polygons.get(i).setVisible(true);
                    }
                }
            }else{
                for (int i = 0; i < lots.size(); i++) {
                    if (lots.get(i).getTime().equals(tag)) {
                        polygons.get(i).setVisible(false);
                    }
                }
            }
        }
    }

    @Override
    public void onToolbarLotFragmentClicked(String tag) {
        if(tag.equals("back")){
            removePopDownFragment();
            addToolbarHomeFragment();
        }else if(tag.equals("showMore")){
            if(popDownFragment.equals("no")) {
                popDownFragment = tag;
                Bundle bundle = new Bundle();
                bundle.putString("color", lots.get(selectedIndex).getColor());
                bundle.putString("other", lots.get(selectedIndex).getOther());
                bundle.putString("times", lots.get(selectedIndex).getTime());
                addToolbarLotExtraFragment(bundle);
            }else{
                removePopDownFragment();
            }
        }
    }

    @Override
    public void onToolbarLotExtraFragmentClicked(String tag) {
        if(tag.equals("directions")){
            //handle Directions
            LatLng centerPoint = lots.get(selectedIndex).getCenter();
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr="
                            + latA + "," + lngA + "&daddr="
                            + centerPoint.latitude + "," + centerPoint.longitude));
            startActivity(mapIntent);
        }
    }

    private void taskCompleted(JSONObject products){
        colorList = new BooleansWithTags();
        colorList.add("Reserved", true);
        colorList.add("Faculty/Staff", true);
        colorList.add("Resident Reserved", true);
        colorList.add("Student", true);
        colorList.add("Remote", true);
        colorList.add("Parking Meters", true);
        colorList.add("Short Term Meters", true);
        colorList.add("ADA Parking", true);

        timesList = new BooleansWithTags();

        otherList = new BooleansWithTags();
        otherList.add("placeholder", true);

        lots = new ArrayList<Lot>();
        garages = new ArrayList<>();

        ArrayList<LatLng> LatLangs = new ArrayList<LatLng>();

        try {
            JSONArray jsonArray;
            jsonArray = products.getJSONArray("lots");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject e = jsonArray.getJSONObject(i);

                String name = e.getString("lotId");
                String color = e.getString("color");
                String shape = e.getString("shape");
                String type = e.getString("zoneType");
                String time = e.getString("time");
                if(type.equals("1")) type = colorList.tags.get(0);
                else if(type.equals("2")) type = colorList.tags.get(1);
                else if(type.equals("3")) type = colorList.tags.get(2);
                else if(type.equals("4")) type = colorList.tags.get(3);
                else if(type.equals("5")) type = colorList.tags.get(4);
                else if(type.equals("6")) type = colorList.tags.get(5);
                else if(type.equals("7")) type = colorList.tags.get(6);
                else if(type.equals("9")) type = colorList.tags.get(7);

                int start = 0;
                int end = -1;

                while(end != shape.length()){

                    start = end + 1;
                    end = shape.indexOf(' ', start);
                    String lat = shape.substring(start, end);
                    start = shape.indexOf('-', end);
                    end = shape.indexOf('|', start);
                    if(end == -1) end = shape.length();
                    String lng = shape.substring(start, end);

                    LatLangs.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
                }

                lots.add(new Lot(name, LatLangs, Color.parseColor(color), type, time, otherList.tags.get(0)));
                LatLangs.clear();
            }
            jsonArray = products.getJSONArray("timeList");
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject e = jsonArray.getJSONObject(i);
                String time = e.getString("time");
                timesList.add(time, true);
            }

            jsonArray = products.getJSONArray("garages");
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject e = jsonArray.getJSONObject(i);
                poss.add(new LatLng(Double.parseDouble(e.getString("lat")), Double.parseDouble(e.getString("lng"))));
                names.add(e.getString("name"));
                ids.add(e.getString("id"));

            }

        } catch (JSONException e){
            Log.e("error", "there was an error(no shit)");
        }

        addToolbarHomeFragment();

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //starting bounds, ne = northeast, sw = southwest
                LatLng ne = new LatLng(36.070352, -94.168765);
                LatLng sw = new LatLng(36.056000, -94.183871);
                centerBounds = new LatLngBounds(sw, ne);

                map.moveCamera(CameraUpdateFactory.newLatLngBounds(centerBounds, 1));
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                polygons = new ArrayList<Polygon>();

                //adds each lot polygon from lots to the map
                for (int index = 0; index < lots.size(); index++) {
                    PolygonOptions rectOptions = new PolygonOptions()
                            .fillColor(lots.get(index).getFillColor())
                                    //.strokeWidth(3)
                            .strokeColor(lots.get(index).getFillColor());

                    for (int j = 0; j < lots.get(index).vertices.size(); j++) {
                        rectOptions.add(lots.get(index).getVertex(j));
                    }

                    polygons.add(map.addPolygon(rectOptions));
                }
                    for (int index = 0; index < poss.size(); index++) {
                        garages.add(map.addMarker(new MarkerOptions()
                                .position(poss.get(index))
                                .title(names.get(index))));
                    }
                }

        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng clickPoint) {
                for (int index = 0; index < lots.size(); index++) {
                    if(polygons.get(index).isVisible()
                            && isPointInPolygon(clickPoint, lots.get(index).getVertices())){
                        selectedIndex = index;
                        removePopDownFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("name", lots.get(index).getName());
                        addToolbarLotFragment(bundle);
                        break;
                    }else if(index + 1 == lots.size()){
                        removePopDownFragment();
                        addToolbarHomeFragment();
                    }
                }
            }
        });

        setCriteria();
        getLocation();
    }

    private void getLocation() {
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String provider = lm.getBestProvider(criteria, true);

        Location l = lm.getLastKnownLocation(provider);
        updateWithNewLocation(l);
        lm.requestLocationUpdates(provider, 2000, 10, locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void updateWithNewLocation(Location loc) {
        if(loc != null) {
            latA = loc.getLatitude();
            lngA = loc.getLongitude();
            showLocation(latA, lngA);
        }
    }

    private void showLocation(double latA, double lngA) {
        LatLng location = new LatLng(latA, lngA);
        Geocoder geocoder;

        geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(latA, lngA, 1);

            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
//                StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                StringBuilder strReturnedAddress = new StringBuilder();
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                streetAddress = (strReturnedAddress.toString());
            }
            else{
                streetAddress = ("No Address returned!");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            streetAddress = ("Cannot get Address!");
        }

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 16);
        map.animateCamera(update);

        // remove previous marker
        if(marker != null)
            marker.remove();

        marker = map.addMarker(new MarkerOptions().position(location).title(streetAddress));
        ActivityUtils.onCampus = checkRadius(marker);

        Log.i("OnCampus", "" + Boolean.toString(ActivityUtils.onCampus));

//
//        if(!conditionalsChecked) {
////            if(sharedPreferences.getBoolean("prefNotificationSwitch", true) &&
////                    atLeastOneNotificationChecked) {
////                if(!serviceOn) {
////                    Intent serviceIntent = new Intent(getApplicationContext(),
////                            ParkansasNotificationService.class);
////                    serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    serviceIntent.putExtra(ActivityUtils.ON_CAMPUS_BOOL, onCampus);
////                    this.startService(serviceIntent);
////
////                    serviceOn = true;
////                }
//                alertConditionals(ActivityUtils.onCampus);
//                conditionalsChecked = true;
//
////            }

        }
//        Intent intent = new Intent(this, ParkansasBackgroundService.class);
//        intent.putExtra(ActivityUtils.ON_CAMPUS_BOOL, onCampus);
//
//        startService(intent);

//    private void alertConditionals(boolean onCampusCheck){
//       boolean between9and6 = checkTime();
//        Log.i("Checking Time", ""+ between9and6 + " " + ActivityUtils.wakeUpCallOn);
//        Log.i("Time Set", hour + ":" + minute);
//
//
//            if (onCampusCheck) {
//                if (between9and6 && ActivityUtils.wakeUpCallOn) {
////                    showWakeUpNotification();
//
//                    if(!ActivityUtils.alarmTimeSet) {
//                            showDialog(ActivityUtils.ALARM_ID);
//                            Log.i("Time Set", hour + ":" + minute);
//                        }
//                }
//            } else {
//                if (ActivityUtils.timeExpirationNotificationOn) {
//
//                }
//                if (ActivityUtils.gameDayNotificationOn) {
//
//                }
//                if (ActivityUtils.freeParkingNotificationOn) {
//
//                }
//                if (ActivityUtils.harmonNotificationOn && !ActivityUtils.hasHarmonPass) {
//
//                }
//            }
//    }

//    public void showWakeUpNotification(){
////        intent = getIntent();
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, ActivityUtils.ALARM_ID, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Notification notification = new Notification.Builder(this)
//                .setContentTitle(getString(R.string.app_name))
//                .setStyle(new Notification.BigTextStyle().bigText(getString(R.string.wake_up_call_msg)))
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true)
//                .addAction(R.drawable.ic_suspend_alert,"Set Time", pendingIntent)
//                .addAction(R.drawable.ic_dismiss_alert, getString(R.string.dismiss_text), pendingIntent)
//                .build();
//
//        notification.defaults |= Notification.DEFAULT_ALL;
//        notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
//
//        notificationManager.notify(ActivityUtils.NOTIFICATION_ID, notification);
//
//        if(timeSet)
//            notificationManager.cancel(ActivityUtils.NOTIFICATION_ID);
//    }

    @Override
    protected void onPause(){
        super.onPause();
        lm.removeUpdates(locationListener);
    }

    private void setCriteria() {
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
    }

    private boolean checkRadius(Marker marker){
        double uarkLat = 36.067832;
        double uarkLong = -94.173655;
        float[] distances = new float[1];

        Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude,
                uarkLat, uarkLong, distances);

        Log.i("Check Radius", "Meters from Campus: " + distances[0]);

        if(distances[0] < (1609.34/2))
            return true;
        return false;
    }

//    private boolean checkTime() {
//        String nine_oClock = "21:00:00";
//        String six_oClock = "06:00:00";
//        String today = (String) android.text.format.DateFormat.format("HH:mm:ss", new java.util.Date());
//        Date todayDate = null, nineDate = null, sixDate = null;
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//
//        try {
//            todayDate = simpleDateFormat.parse(today);
//            nineDate = simpleDateFormat.parse(nine_oClock);
//            sixDate = simpleDateFormat.parse(six_oClock);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Log.i("Time. Today: ", todayDate + " 9:00 - " + nineDate + "Comparison: " +
//                Boolean.toString((todayDate).after(nineDate)));
//
//        if (todayDate.after(nineDate) && todayDate.before(sixDate)) {
//            return true;
//        }
//        else
//            return true;
//    }
//
//    @Override
//    protected Dialog onCreateDialog(int id){
//        TimePickerDialog timePickerDialog =
//                new TimePickerDialog(this, timePickerListener, hour, minute, false);
//
//        timePickerDialog.setTitle(getString(R.string.wake_up_call));
//        timePickerDialog.setMessage(getString(R.string.wake_up_call_alert));
//        switch(id){
//            case ActivityUtils.ALARM_ID:
//                return timePickerDialog;
//        }
//        return null;
//    }
//
//    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
//        @Override
//        public void onTimeSet(TimePicker view, int hourOfDay, int nMinute) {
//            hour = hourOfDay;
//            minute = nMinute;
//
//            sharedPreferences.edit().putInt(ActivityUtils.HOUR_KEY, hour).apply();
//            sharedPreferences.edit().putInt(ActivityUtils.MINUTE_KEY, minute).apply();
//
//            timePicker.setCurrentHour(sharedPreferences.getInt(ActivityUtils.HOUR_KEY, 0));
//            timePicker.setCurrentMinute(sharedPreferences.getInt(ActivityUtils.MINUTE_KEY, 0));
//
//            ActivityUtils.alarmTimeSet = true;
//            Log.i("Time Set", sharedPreferences.getInt(ActivityUtils.HOUR_KEY, 0) + ":" + minute);
//            Toast.makeText(MainActivity.this, "Wake Up Call set for " +
//                    sharedPreferences.getInt(ActivityUtils.HOUR_KEY, 0)+ ":" +
//                    ((minute < 10)? "0"+sharedPreferences.getInt(ActivityUtils.MINUTE_KEY, 0):
//                            sharedPreferences.getInt(ActivityUtils.MINUTE_KEY, 0)),
//                            Toast.LENGTH_LONG).show();
//        }
//    };

}

