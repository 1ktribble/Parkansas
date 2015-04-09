package edu.uark.csce.parkansas.parkansas;

import java.util.ArrayList;

public class BooleansWithTags {

    ArrayList<String> tags;
    ArrayList<Boolean> booleans;

    BooleansWithTags() {
        tags = new ArrayList<>();
        booleans = new ArrayList<>();
    }

    public void add(String tagArg, boolean boolArg){
        tags.add(tagArg);
        booleans.add(boolArg);
    }
    public int getIndexOf(String tagArg){
        return tags.indexOf(tagArg);
    }
    public boolean[] getPrimitive(){
        boolean[] booleansPrimitive = new boolean[booleans.size()];
        for (int i = 0; i < booleans.size(); i++){
            booleansPrimitive[i] = booleans.get(i);
        }
        return booleansPrimitive;
    }
    public void flip(int i){
        booleans.set(i, !booleans.get(i));
    }
    public void setAll(boolean bool_){
        for(int i = 0; i < booleans.size(); i++){
            booleans.set(i, bool_);
        }
    }
    public int size(){
        return tags.size();
    }
    public boolean isTrue(String tag){
        int index = tags.indexOf(tag);
        return booleans.get(index);
    }
}