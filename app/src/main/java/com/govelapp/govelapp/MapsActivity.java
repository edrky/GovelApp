package com.govelapp.govelapp;

import android.Manifest;
import android.content.pm.PackageManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.identity.intents.AddressConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;

import com.govelapp.govelapp.jsonparser.QueryParser;
import com.govelapp.govelapp.restclient.RestClient;
import com.govelapp.govelapp.shopclasses.Shop;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    //our valid characters OnMapReadyCallback
    private static final Pattern queryPattern = Pattern.compile("[a-zA-Z \t]+");
    final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;
    private GoogleMap mMap;
    private AutoCompleteTextView actv;
    private String url = "govelapp.com/api";     //getResources().getString(R.string.url);
    private List<Shop> shopList;
    private String query;

    private ListView mDrawerList;
    private LinearLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //basic setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //create right side bar
        mDrawerLayout = (LinearLayout) findViewById(R.id.right_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.linear_drawer_list);

        //create a seperate adapter for maps activity search actv
        actv = (AutoCompleteTextView) findViewById(R.id.searchBar);
        String[] items = {"tea", "apple", "phone case", "tooth paste", "tennis racket", "tooth brush", "tooth pick", "kahve"}; //this is for testing purposes
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_expandable_list_item_1, items);
        actv.setAdapter(adapter);
        actv.setText(getIntent().getExtras().getString("query"));
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mapsQuery = actv.getText().toString();
                actv.setText(mapsQuery);
                actv.setSelection(mapsQuery.length()); //set the cursor position
                if (mapsQuery.length() > 0 && queryValidityTest(mapsQuery)) {
                    query = mapsQuery;
                    new webGetSetMarkers().execute(url, query);
                } else {
                    Toast.makeText(MapsActivity.this, "Invalid query.", Toast.LENGTH_LONG).show();
                }
            }
        });

        query = getIntent().getExtras().getString("query");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //MAP SETUP
        mMap = googleMap;
        UiSettings mUI = mMap.getUiSettings();
        mUI.setZoomControlsEnabled(false);
        mUI.setMapToolbarEnabled(true);
        mUI.setCompassEnabled(false);
        mMap.setOnMarkerClickListener(this);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(MapsActivity.this, "Location permission is disabled.", Toast.LENGTH_SHORT).show();
            //request permission
        }

        showcaseBesiktas();

        mDrawerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker){
        List<String> markerInfos = new ArrayList<>();
        if(marker.getTitle() != null){
            markerInfos.add("Title: " + marker.getTitle());
        }
        if(marker.getSnippet() != null){
            markerInfos.add("Info: " + marker.getSnippet());
        }
        //String[] examples = new String[]{"a","b","c","d"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, markerInfos);

       mDrawerList.setAdapter(adapter);

        mDrawerLayout.setVisibility(View.VISIBLE);
        mDrawerLayout.bringToFront();
        Log.d("Layout", "is visible");

        return false;
    }

    private void showcaseBesiktas() {
        LatLng cafeNero = new LatLng(41.044400, 29.006949);
        Marker Cafe_nero = mMap.addMarker(new MarkerOptions()
                .position(cafeNero)
                .title("Cafe Nero")
                .snippet("Snippets are good.")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flare_black_48dp)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cafeNero, 16.5f));

        LatLng kukaKafe = new LatLng(41.043850, 29.006359);
        mMap.addMarker(new MarkerOptions().position(kukaKafe).title("Kuka Kafe & Pub"));
        LatLng sahilRest = new LatLng(41.041835, 29.009481);
        mMap.addMarker(new MarkerOptions().position(sahilRest).title("Sahil Rest Cafe"));
        Cafe_nero.showInfoWindow();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mDrawerLayout.isShown()){
                mDrawerLayout.setVisibility(View.INVISIBLE);
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    //returns true if its a valid query
    private boolean queryValidityTest(String s) {
        Matcher mMatch = queryPattern.matcher(s);
        return mMatch.matches();
    }

    //url, query, void ---- params[0], params[1], void
    private class webGetSetMarkers extends AsyncTask<String, String, Void> {
        //loading screen(?)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //main function to run
        @Override
        protected Void doInBackground(String... params) {
            RestClient rc = new RestClient();
            String jsonReply = rc.getStandardQueryJson(params[0], params[1]);

            QueryParser qp = new QueryParser();
            shopList = qp.parseShopList(jsonReply);
            return null;
        }

        //do after doInBackground is finished
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            for (Shop sh : shopList) {
                mMap.addMarker(sh.getMarkerOptions());
            }
        }

       /* @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }*/

        @Override
        protected void onCancelled(Void result) {

            super.onCancelled(result);
        }
    }
}

