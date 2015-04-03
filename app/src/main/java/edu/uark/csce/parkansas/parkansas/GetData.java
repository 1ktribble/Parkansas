package edu.uark.csce.parkansas.parkansas;

import java.util.ArrayList;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;


public class GetData{
    JSONParser jParser = new JSONParser();

    private static String url_get_stuff = "http://uaf58657.ddns.uark.edu/data.php";

    //JSONArray products = null;
    JSONObject products = null;

    public interface OnTaskCompleted{
        void onTaskCompleted(JSONObject products);
    }

    private OnTaskCompleted listener;

    public GetData(OnTaskCompleted listener){
        this.listener=listener;
    }

    public boolean isConnected(Context context, boolean connectionAvailable){
        if(connectionAvailable) {
            new LoadAllProducts().execute();
            return true;
        }
        else
            return false;
    }

    public JSONObject getProducts(){
        return products;
    }

    class LoadAllProducts extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            JSONObject json = jParser.makeHttpRequest(url_get_stuff, "GET", params);

            try {
                int success = json.getInt("success");
                if (success == 1) {
                    //products = json.getJSONArray("lots");
                    products = json;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listener.onTaskCompleted(products);
        }
    }
}