package edu.illinois.cs.cs125.mp7;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.illinois.cs.cs125.mp7.Keys;


public class MainActivity extends AppCompatActivity {
    /** Location Permissions. */
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    // Why is this being wierd
    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "MP7:Main";

    /** Request queue for our API requests. */
    private static RequestQueue requestQueue;

    private String olat;
    private String olon;
    private String dlat;
    private String dlon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(MainActivity.this);

        final Button startSearch = findViewById(R.id.button_send);
        startSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                getLocation();

                EditText address = (EditText) findViewById(R.id.input_address);
                String b = address.getText().toString();
                getDestination(b);
                getRoute();
            }
        });
    }

    void getRoute() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
        String test = sdf.format(cal.getTime());
        Log.e(TAG, test);
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://developer.cumtd.com/api/v2.2/json/getplannedtripsbylatlon?key=" + Keys.MTD_API_KEY + "&origin_lat=" + olat + "&origin_lon=" + olon + "&destination_lat=" + dlat + "&destination_lon=" + dlon + "&time=" + test,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            String result = "";
                            try {
                                JSONArray it = response.getJSONArray("itineraries");
                                JSONObject itineraries = it.getJSONObject(0);
                                String start = itineraries.getString("start_time");
                                String end = itineraries.getString("end_time");
                                String travel = itineraries.getString("travel_time");
                                JSONArray legs = itineraries.getJSONArray("legs");
                                JSONObject se = legs.getJSONObject(1);
                                JSONArray services = se.getJSONArray("services");
                                for (int i = 0; i < services.length(); i++) {
                                    result += services.getJSONObject(i).getJSONObject("route").getString("route_id");
                                    result += "\n";
                                    result += services.getJSONObject(i).getJSONObject("begin").getString("name") + "     ";
                                    result += services.getJSONObject(i).getJSONObject("begin").getString("time");
                                    result += "\n";
                                    result += services.getJSONObject(i).getJSONObject("end").getString("name") + "     ";
                                    result += services.getJSONObject(i).getJSONObject("end").getString("time");
                                    result += "\n";
                                }

                            } catch (Exception e) { e.printStackTrace(); }
                            TextView View1 = findViewById(R.id.MTD_Result);
                            View1.setText(result);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.e(TAG, error.toString());
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void getDestination(final String address) {
        String a = address.replaceAll(" ", "+");
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://maps.googleapis.com/maps/api/geocode/json?key=" + Keys.GOOGLE_API_KEY + "&address=" + a,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            try {;
                                JSONArray test = (JSONArray) response.get("results");
                                JSONObject test2 = test.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                                Log.d(TAG, test2.getString("lat"));
                                Log.d(TAG, test2.getString("lng"));
                                dlat = test2.getString("lat");
                                dlon = test2.getString("lng");
                            } catch (JSONException ignored) { }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(final VolleyError error) {
                            Log.e(TAG, error.toString());
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)  != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location location = locationManager.getLastKnownLocation
                    (LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                Log.d(TAG, Double.toString(lat));
                Log.d(TAG, Double.toString(lon));
                olat = Double.toString(lat);
                olon = Double.toString(lon);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }
}
