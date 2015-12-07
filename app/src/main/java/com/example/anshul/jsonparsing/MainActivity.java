package com.example.anshul.jsonparsing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private ProgressDialog pDialog;

    ArrayList<RestaurantGetterSetter> Restaurant = new ArrayList();
    ListView listView;
    public static Location location;
    LocationManager locationManager;

    // URL to get contacts JSON
    private static String url = "http://staging.couponapitest.com/task.txt";


    // contacts JSONArray
    JSONArray data = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && location != null) {
            new GetContacts().execute();
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    data = jsonObj.getJSONArray("data");
                    Log.e("data", data.getString(0));
                    // looping through All Contacts
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        RestaurantGetterSetter listItem = new RestaurantGetterSetter();

                        String url = c.getString("LogoURL");
                        String BrandName = c.getString("BrandName");
                        Double Latitude = c.getDouble("Latitude");
                        Double Longitude = c.getDouble("Longitude");
                        int offers = c.getInt("NumCoupons");
                        String neighbourhood = c.getString("NeighbourhoodName");
                        Log.e("brandname", BrandName);
                        // Phone node is JSON Object
                        JSONArray Categories = c.getJSONArray("Categories");
                        String[] name = new String[10];
                        for (int j = 0; j < Categories.length(); j++) {
                            if (!Categories.getJSONObject(j).getString("Name").equals("Restaurant")) {
                                name[j] = Categories.getJSONObject(j).getString("Name");
                            }

                        }
                        // String Name = Categories.getJSONObject(0).getString("Name");
                        Location RestLocation = new Location("location");
                        RestLocation.setLatitude(Latitude);
                        RestLocation.setLongitude(Longitude);
                        //      Location location= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        float distance = location.distanceTo(RestLocation);

                        listItem.setBrandName(BrandName);
                        listItem.setName(name);
                        listItem.setUrl(url);
                        listItem.setDistance(distance);
                        listItem.setOffers(offers);
                        listItem.setNeighbouhood(neighbourhood);
                        Log.e("url", url);


                        Restaurant.add(listItem);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            Collections.sort(Restaurant, new Comparator<RestaurantGetterSetter>() {
                @Override
                public int compare(RestaurantGetterSetter lhs, RestaurantGetterSetter rhs) {
                    int distance1 = (int) lhs.distance;
                    int distance2 = (int) rhs.distance;
                    return distance1 - distance2;
                }
            });
            Log.e("listsize", "" + Restaurant.size());
            CustomAdapter adapter = new CustomAdapter(Restaurant, MainActivity.this);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }

    }

    public class MyLocationListener implements LocationListener {
        public String bestProvider;
        public Criteria criteria;
        public double latitude;
        public double longitude;


        @Override
        public void onLocationChanged(Location loc) {

            Toast.makeText(
                    getBaseContext(),
                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " + loc.getLongitude();
            //   Log.v(TAG, longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            //  Log.v(TAG, latitude);
            Log.e("LatLong", latitude + "+" + longitude);


        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }


    }


}
