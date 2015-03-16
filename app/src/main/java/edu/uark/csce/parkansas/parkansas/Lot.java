package edu.uark.csce.parkansas.parkansas;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Lot {

    long id;
    String name;
    ArrayList<LatLng> vertices;
    int fillColor;
    String color;
    String time;
    String other;


    Lot(String nameArg, ArrayList<LatLng> verticesArg, int fillColorArg, String colorArg,
         String timeArg, String otherArg) {

        name = nameArg;
        color = colorArg;
        other = otherArg;
        fillColor = fillColorArg;
        vertices = new ArrayList<>();
        for(int i = 0; i < verticesArg.size(); i++) {
            vertices.add(verticesArg.get(i));
        }
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
    public String getColor() {
        return color;
    }
    public String getTime(){
        return time;
    }
    public String getOther(){
        return other;
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
