package edu.uark.csce.parkansas.parkansas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Handler;
import android.os.Message;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
        ToolbarLotExtraFragment.OnToolbarLotExtraFragmentClickedListener,
        OnMapReadyCallback {

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
    boolean timeSet, serviceOn, atLeastOneNotificationChecked;

    String popDownFragment = "no";  //current fragment in the popDown frame, "no" == empty
    int selectedIndex;              //index of selected Lot
//    boolean moving = false;
//    float x,y = 0.0f;
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



       checkConnection();

//        /*
//        Fragment fragment = new LegendFragment();
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.container3, fragment);
//        ft.commit();
//
//
//        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.container3);
//        frameLayout.setX(200);
//        frameLayout.setY(200);
//
//
//        frameLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View arg0, MotionEvent arg1) {
//                Log.i("hello", "hello");
//                switch (arg1.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        moving = true;
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        if (moving) {
//                            x = arg1.getRawX() - frameLayout.getWidth()/2;
//                            y = arg1.getRawY() - frameLayout.getHeight()*3/2;
//                            frameLayout.setX(x);
//                            frameLayout.setY(y);
//                        }
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        moving = false;
//                        break;
//                }
//                return true;
//            }
//        });
//*/
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
    public void onStart(){
        super.onStart();
        ActivityUtils.mainActivityActive = true;
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
            case R.id.action_alerts:
                startActivity(new Intent(this, ResultActivity.class));
                finish();
                return true;
            case R.id.action_about_developers:
                startActivity(new Intent(this, DeveloperInfoActivity.class));
                return true;
            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
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

       checkConnection();

//        if(lm != null)
//            getLocation();

//        Intent serviceIntent = new Intent(getApplicationContext(),
//                ParkansasNotificationService.class);

        if(sharedPreferences != null){
//            if(sharedPreferences.getBoolean("prefNotificationSwitch", false)
//                    && ActivityUtils.atLeastOneNotificationChecked){
//                serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                serviceIntent.putExtra(ActivityUtils.ON_CAMPUS_BOOL, ActivityUtils.onCampus);
//                this.startService(serviceIntent);
//            if(!mIsBound)
//                doBindService();
                ActivityUtils.serviceOn = true;
//            }
        }
        resetDisconnectTimer();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if(lm != null)
            getLocation();
    }

    private void checkConnection(){
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
    }

    private void openSettings() {
            @SuppressWarnings("rawtypes")
            Intent i = new Intent(this, SettingsActivity.class);

            startActivity(i);
     }

    private void alertDialogShow(Context context){
        final AlertDialog.Builder adb = new AlertDialog.Builder(context);

        AlertDialog ad = adb.create();
        ad.setMessage("PARKansas maps failed to launch. Check cellular or Wi-Fi connection.");
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
        bundle.putString(ActivityUtils.PARKING_LOT_TIME, lots.get(selectedIndex).getTime());
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
            if(tag.equals("all")){
                colorList.setAll(true);

                for(int i = 0; i < lots.size(); i++){
                    if (timesList.isTrue(lots.get(i).getTime())) {
                        polygons.get(i).setVisible(true);
                    }
                }
            }else if(tag.equals("none")){
                colorList.setAll(false);

                for (int i = 0; i < lots.size(); i++) {
                    polygons.get(i).setVisible(false);
                }
            }else {
                int index = colorList.getIndexOf(tag);
                colorList.flip(index);

                if (colorList.booleans.get(index)) {
                    for (int i = 0; i < lots.size(); i++) {
                        if (lots.get(i).getColor().equals(tag)
                                && timesList.isTrue(lots.get(i).getTime())) {

                            polygons.get(i).setVisible(true);
                        }
                    }
                } else {
                    for (int i = 0; i < lots.size(); i++) {
                        if (lots.get(i).getColor().equals(tag)) {
                            polygons.get(i).setVisible(false);
                        }
                    }
                }
            }
        }else if(popDownFragment.equals("times")) {
            if(tag.equals("all")){
                timesList.setAll(true);

                for(int i = 0; i < lots.size(); i++){
                    if (colorList.isTrue(lots.get(i).getColor())) {
                        polygons.get(i).setVisible(true);
                    }
                }
            }else if(tag.equals("none")){
                timesList.setAll(false);

                for (int i = 0; i < lots.size(); i++) {
                    polygons.get(i).setVisible(false);
                }
            }else {
                int index = timesList.getIndexOf(tag);
                timesList.flip(index);
                if (timesList.booleans.get(index)) {
                    for (int i = 0; i < lots.size(); i++) {
                        if (lots.get(i).getTime().equals(tag)
                                && colorList.isTrue(lots.get(i).getColor())) {

                            polygons.get(i).setVisible(true);
                        }
                    }
                } else {
                    for (int i = 0; i < lots.size(); i++) {
                        if (lots.get(i).getTime().equals(tag)) {
                            polygons.get(i).setVisible(false);
                        }
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
        }else if(tag.equals("parkHere")) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Park Here")
                    .setMessage("Would you like to set a notification?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(),
                                    ResultActivity.class);
                            intent.putExtra(ActivityUtils.PARKING_LOT_NAME, lots.get(selectedIndex)
                                    .getName());
                            intent.putExtra(ActivityUtils.PARKING_LOT_TIME, lots.get(selectedIndex)
                                    .getTime());
                            dialog.dismiss();
                            startActivity(intent);
                            ActivityUtils.openedFromPARKHERE = true;
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            // Get the layout inflater
//                LayoutInflater inflater = getActivity().getLayoutInflater();
//                final View diaView = inflater.inflate(R.layout.dialog_meter, null);
//
//                builder.setView(diaView);
//                final AlertDialog dialog = builder.create();
//                dialog.show();
//                final TimePicker pickerTime = (TimePicker) diaView.findViewById(R.id.timePicker);
//
//                pickerTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//                    @Override
//                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//                        TextView textView = (TextView) diaView.findViewById(R.id.costText);
//
//                        Calendar rightNow = Calendar.getInstance();
//
//                        double cost = 1.00;
//                        int hours = hourOfDay - rightNow.get(Calendar.HOUR_OF_DAY);
//                        int minutes = minute - rightNow.get(Calendar.MINUTE);
//                        double totalCost = hours * cost + (minutes * cost)/60;
//                        totalCost = new BigDecimal(totalCost).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//                        if(totalCost  < 0) totalCost = 0;
//                        textView.setText("$" + String.valueOf(totalCost));
//                    }
//                });

//                Button btn = (Button) diaView.findViewById(R.id.costYes);
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String t1 = String.valueOf(pickerTime.getCurrentHour());
//                        String t2 = String.valueOf(pickerTime.getCurrentMinute());
//                        if(t2.length() == 1){
//                            t2 = '0' + t2;
//                        }
//
//                        onToolbarLotExtraFragmentClickedListener.onToolbarLotExtraFragmentClicked(t1+':'+t2);
//                        dialog.dismiss();
//                    }
//                });
//                Button btn2 = (Button) diaView.findViewById(R.id.costNo);
//                btn2.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
        }
        else{
              Log.i("Time", tag);
              Log.i("Lot", String.valueOf(lots.get(selectedIndex).getName()));
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
                Marker harmonMarker, stadiumDrMarker, garlandMarker, meadowMarker;
                for (int index = 0; index < poss.size(); index++) {
                    if (ids.get(index).equals("51")) {
                            harmonMarker = map.addMarker(new MarkerOptions()
                                    .position(poss.get(index))
                                    .title("(HAPG) Harmon Avenue Garage")
                                    .snippet("146 N. Harmon Ave. Fayetteville, Arkansas 72701")
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.g)));
                            garages.add(harmonMarker);
                        } else if (ids.get(index).equals("52")) {
                            stadiumDrMarker = map.addMarker(new MarkerOptions()
                                    .position(poss.get(index))
                                    .title("(SDPG) Stadium Drive Garage")
                                    .snippet("380 N. Stadium Dr. Fayetteville, Arkansas 72701")
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.g)));
                            garages.add(stadiumDrMarker);
                        } else if (ids.get(index).equals("87")) {
                            garlandMarker = map.addMarker(new MarkerOptions()
                                    .position(poss.get(index))
                                    .title("(GAPG) Garland Avenue Garage")
                                    .snippet("650 N. Garland Ave. Fayetteville, Arkansas 72701")
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.g)));
                            garages.add(garlandMarker);
                        } else if (ids.get(index).equals("111")) {
                            meadowMarker = map.addMarker(new MarkerOptions()
                                    .position(poss.get(index))
                                    .title("(MSPG) Meadow Stree Parking Garage")
                                    .snippet("1308 W. Meadow St. Fayetteville, Arkansas 72701")
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.g)));
                            garages.add(meadowMarker);
                        }
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
                        bundle.putString("LotType", lots.get(index).getColor());
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
        lm.requestLocationUpdates(provider, 1000, 2, locationListener);
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
            updateWithNewLocation(null);
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
            else {
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

    @Override
    protected void onPause(){
        super.onPause();
        if(locationListener != null && lm != null)
          lm.removeUpdates(locationListener);

    }

    @Override
    protected void onStop(){
        super.onStop();
        ActivityUtils.mainActivityActive = false;
        stopDisconnectTimer();
//        map.addMarker(new MarkerOptions()
//                .position(location)
//                .title(streetAddress)
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.current)));
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

    public static final long DISCONNECT_TIMEOUT = 300000; // 5 min = 5 * 60 * 1000 ms

    private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            // Perform any required operation on disconnect
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }
}

