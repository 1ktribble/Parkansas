package edu.uark.csce.parkansas.parkansas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements
        ToolbarHomeFragment.OnToolbarHomeFragmentClickedListener,
        ToolbarFiltersFragment.OnToolbarFiltersFragmentClickedListener,
        ToolbarFilterOptionsFragment.OnToolbarFilterOptionsFragmentClickedListener,
        ToolbarLotFragment.OnToolbarLotFragmentClickedListener,
        ToolbarLotExtraFragment.OnToolbarLotExtraFragmentClickedListener{

    Intent intent;
    private GoogleMap map;

    ArrayList<Lot> lots;
    ArrayList<Polygon> polygons;
    BooleansWithTags colorList;
    BooleansWithTags timesList;
    BooleansWithTags otherList;
    LatLngBounds centerBounds;
    String popDownFragment = "no";  //current fragment in the popDown frame, "no" == empty
    int selectedIndex;              //index of selected Lot
    boolean moving = false;
    float x,y = 0.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = getIntent();

        GetData test = new GetData(new GetData.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(JSONArray products) {
                taskCompleted(products);
            }
        });
        test.happen();

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
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_exit:
                finish();
                return true;
        }
        return false;

    }

    public void openSettings(){
        @SuppressWarnings("rawtypes")
        Class c = SettingsActivity.class;
        Intent i = new Intent(this, c);

        startActivity(i);
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

    public void addToolbarHomeFragment() {
        Fragment fragment = new ToolbarHomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container1, fragment);
        ft.commit();
    }

    public void addToolbarFiltersFragment() {
        Fragment fragment = new ToolbarFiltersFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container1, fragment);
        ft.commit();
    }

    public void addToolbarFilterOptionsFragment(Bundle bundle) {
        Fragment fragment = new ToolbarFilterOptionsFragment();
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container2, fragment, "popDown");
        ft.commit();
    }

    public void addToolbarLotFragment(Bundle bundle) {
        Fragment fragment = new ToolbarLotFragment();
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container1, fragment);
        ft.commit();
    }

    public void addToolbarLotExtraFragment(Bundle bundle) {
        Fragment fragment = new ToolbarLotExtraFragment();
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container2, fragment, "popDown");
        ft.commit();
    }

    //removes fragment with popDown tag, if it is nonempty.
    public void removePopDownFragment() {
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
        }else if(tag.equals("other")){
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("list", otherList.tags);
            bundle.putBooleanArray("booleans", otherList.getPrimitive());
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
            colorList.flip(index);
            if(colorList.booleans.get(index)) {
                for (int i = 0; i < lots.size(); i++) {
                    if (lots.get(i).getPassColors().get(0).equals(tag)
                            && timesList.isTrue(lots.get(i).getTime())
                            && otherList.isTrue(lots.get(i).getOther())) {

                        polygons.get(i).setVisible(true);
                    }
                }
            }else{
                for (int i = 0; i < lots.size(); i++) {
                    if (lots.get(i).getPassColors().get(0).equals(tag)) {
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
                            && colorList.isTrue(lots.get(i).getPassColors().get(0))) {

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
        }else if(popDownFragment.equals("other")) {
            int index = otherList.getIndexOf(tag);
            otherList.flip(index);
            if(otherList.booleans.get(index)) {
                for (int i = 0; i < lots.size(); i++) {
                    if (lots.get(i).getOther().equals(tag)
                            && timesList.isTrue(lots.get(i).getTime())
                            && colorList.isTrue(lots.get(i).getPassColors().get(0))) {

                        polygons.get(i).setVisible(true);
                    }
                }
            }else{
                for (int i = 0; i < lots.size(); i++) {
                    if (lots.get(i).getOther().equals(tag)) {
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
                bundle.putString("color", lots.get(selectedIndex).getPassColors().get(0));
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
        }
    }

    public void taskCompleted(JSONArray products){
        colorList = new BooleansWithTags();
        colorList.add("blue", true);
        colorList.add("red", true);
        colorList.add("green", true);
        colorList.add("yellow", true);

        timesList = new BooleansWithTags();
        timesList.add("idk1", true);
        timesList.add("idk2", true);
        timesList.add("idk3", true);

        otherList = new BooleansWithTags();
        otherList.add("idgaf1", true);
        otherList.add("idgaf2", true);

        lots = new ArrayList<Lot>();

        ArrayList<String> Colors = new ArrayList<String>();
        ArrayList<LatLng> LatLangs = new ArrayList<LatLng>();

        /*
        Colors.add("red");
        LatLangs.add(new LatLng(36.058466, -94.180259));
        LatLangs.add(new LatLng(36.058397, -94.177619));
        LatLangs.add(new LatLng(36.057009, -94.177276));
        LatLangs.add(new LatLng(36.056922, -94.180280));
        lots.add(new Lot("Lot1", Colors, Color.RED, LatLangs, otherList.tags.get(0), timesList.tags.get(0)));
        Colors.clear();LatLangs.clear();

        Colors.add("blue");
        LatLangs.add(new LatLng(36.060513, -94.180183));
        LatLangs.add(new LatLng(36.060513, -94.179465));
        LatLangs.add(new LatLng(36.060079, -94.179465));
        LatLangs.add(new LatLng(36.060079, -94.180183));
        lots.add(new Lot("Lot2", Colors, Color.BLUE, LatLangs, otherList.tags.get(1), timesList.tags.get(1)));
        Colors.clear();LatLangs.clear();

        Colors.add("green");
        LatLangs.add(new LatLng(36.068466, -94.180259));
        LatLangs.add(new LatLng(36.068397, -94.177619));
        LatLangs.add( new LatLng(36.067009, -94.177276));
        LatLangs.add( new LatLng(36.066922, -94.180280));
        lots.add(new Lot("Lot3", Colors, Color.GREEN, LatLangs, otherList.tags.get(0), timesList.tags.get(2)));
        Colors.clear();LatLangs.clear();
        */

        try{ Thread.sleep(5000); }catch(InterruptedException e){ }
        //String s;
        //s = "[{\"id\":3,\"lotId\":49,\"zoneType\":2,\"color\":\"#FFFF00\",\"shape\":\"36.067916 -94.168373,36.068048 -94.16837,36.068044 -94.167627,36.067916 -94.16763,36.067916 -94.168373\",\"status\":1},{\"id\":4,\"lotId\":23,\"zoneType\":2,\"color\":\"#FFFF00\",\"shape\":\"36.056831 -94.180841,36.056871 -94.180841,36.05687 -94.180802,36.057182 -94.180799,36.057182 -94.181011,36.056869 -94.181,36.056865 -94.180973,36.05683 -94.180971,36.056831 -94.180841\",\"status\":1},{\"id\":5,\"lotId\":23,\"zoneType\":2,\"color\":\"#FFFF00\",\"shape\":\"36.057174 -94.181307,36.056853 -94.181307,36.056851 -94.181256,36.057176 -94.181253,36.057174 -94.181307\",\"status\":1},{\"id\":6,\"lotId\":64,\"zoneType\":2,\"color\":\"#FFFF00\",\"shape\":\"36.070514 -94.168291,36.070269 -94.168297,36.070265 -94.168294,36.070262 -94.168102,36.07051 -94.168093,36.070514 -94.168291\",\"status\":1},{\"id\":7,\"lotId\":64,\"zoneType\":2,\"color\":\"#FFFF00\",\"shape\":\"36.070252 -94.168389,36.070593 -94.168373,36.070597 -94.168464,36.070261 -94.168478,36.070252 -94.168389\",\"status\":1}]";
        try {
            //JSONObject jsonObject = new JSONObject(s);
            //Log.i("idk", jsonObject.getString("color"));
            JSONArray jsonArray;// = new JSONArray(s);
            jsonArray = products;
            for(int i=0;i<jsonArray.length();i++){
                JSONObject e = jsonArray.getJSONObject(i);

                Log.i("idk", e.getString("shape"));

                String name = e.getString("lotId");
                String color = e.getString("color");
                String shape = e.getString("shape");

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

                Colors.add("red");
                lots.add(new Lot(name, Colors, Color.parseColor(color), LatLangs, otherList.tags.get(0), timesList.tags.get(0)));
                Colors.clear();LatLangs.clear();
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
                            //.add(lots.get(index).getVertex(0),
                            //        lots.get(index).getVertex(1),
                            //        lots.get(index).getVertex(2),
                            //        lots.get(index).getVertex(3))
                            .fillColor(lots.get(index).getFillColor())
                                    //.strokeWidth(3)
                            .strokeColor(lots.get(index).getFillColor());

                    for(int j = 0; j < lots.get(index).vertices.size(); j++){
                        rectOptions.add(lots.get(index).getVertex(j));
                    }

                    polygons.add(map.addPolygon(rectOptions));
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
    }

}

