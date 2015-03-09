package edu.uark.csce.parkansas.parkansas;

import java.util.ArrayList;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;


public class GetData{
    JSONParser jParser = new JSONParser();

    private static String url_get_stuff = "http://192.168.0.2/testing/getdata2.php";

    JSONArray products = null;

    public void happen() {
        new LoadAllProducts().execute();
    }

    public JSONArray getProducts(){
        return products;
    }

    class LoadAllProducts extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            JSONObject json = jParser.makeHttpRequest(url_get_stuff, "GET", params);

            try {
                int success = json.getInt("success");
                if (success == 1) {
                    products = json.getJSONArray("lots");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}