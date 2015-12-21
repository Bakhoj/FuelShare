package com.example.anders.fuelshare.data;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.anders.fuelshare.map.MapAct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class AsyncLocationsDatabase  extends AsyncTask<Void, Void, String> {
    private static final String TAG_RESULTS="result";
    private static final String TAG_ADD = "address";
    private static final String TAG_LAT = "Lat";
    private static final String TAG_LNG ="Lng";
    MapAct mActivity;
    JSONArray markers = null;


    public AsyncLocationsDatabase(Activity activity){mActivity = (MapAct) activity;}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {

        try{
            String link="http://larspeter.mfrid.com/markers.php";

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }
            return sb.toString();
        }
        catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }

    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject jsonObj = new JSONObject(s);
            markers = jsonObj.getJSONArray(TAG_RESULTS);
            for(int i=0;i<markers.length();i++){
                JSONObject c = markers.getJSONObject(i);
                String address = c.getString(TAG_ADD);
                double lat = c.getDouble(TAG_LAT);
                double lng = c.getDouble(TAG_LNG);

                mActivity.addChargerMarkers(lat, lng, address);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPostExecute(s);
    }
}
