package edu.uark.csce.parkansas.parkansas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

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
            public void onTaskCompleted(JSONObject products) {
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
        }
    }

    public void taskCompleted(JSONObject products){
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

