package edu.uark.csce.parkansas.parkansas;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Lot {

    long id;
    String name;
    int fillColor;
    ArrayList<LatLng> vertices;
    ArrayList<String> passColors;
    String other;
    String time;


    Lot(String nameArg, ArrayList<String> colorArg,
        int fillColorArg, ArrayList<LatLng> verticesArg, String otherArg, String timeArg) {

        name = nameArg;
        passColors = new ArrayList<String>();
        for(int i = 0; i < colorArg.size(); i++) {
            passColors.add(colorArg.get(i));
        }
        fillColor = fillColorArg;
        vertices = new ArrayList<LatLng>();
        for(int i = 0; i < verticesArg.size(); i++) {
            vertices.add(verticesArg.get(i));
        }
        other = otherArg;
        time = timeArg;
    }

    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getFillColor() {
        return fillColor;
    }
    public ArrayList<LatLng> getVertices() {
        return vertices;
    }
    public LatLng getVertex(int index){return vertices.get(index);}
    public ArrayList<String> getPassColors() {
        return passColors;
    }
    public String getOther(){
        return other;
    }
    public String getTime(){
        return time;
    }
    public boolean hasColor(String color){
        return passColors.contains(color);
    }
    public LatLng getCenter(){
        double centerLong = 0;
        double centerLat = 0;

        int size = vertices.size();
        for(int i = 0; i < size; i++){
            centerLong += vertices.get(i).longitude;
            centerLat += vertices.get(i).latitude;
        }
        centerLat = centerLat/size;
        centerLong = centerLong/size;

        return (new LatLng(centerLat,centerLong));

    }
}
