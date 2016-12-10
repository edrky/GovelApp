package com.govelapp.govelapp;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;

import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.SearchEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;

import com.govelapp.govelapp.jsonparser.QueryParser;
import com.govelapp.govelapp.restclient.RestClient;
import com.govelapp.govelapp.shopclasses.Shop;

import org.json.JSONArray;

import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    private Location location;
    private LocationRequest mLocationRequest;
    private final long MIN_TIME = 500;
    private final float MIN_DISTANCE = 1000;
    private int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION;

    private EditText bar;
    private AutoCompleteTextView actv;
    private String url = "govelapp.com/api";     //getResources().getString(R.string.url);
    private List<Shop> shopList;
    private String query;
    //our valid characters
    private static final Pattern mPattern = Pattern.compile("[a-zA-Z \t]+");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        actv = (AutoCompleteTextView) findViewById(R.id.searchBar);
        bar = (EditText) findViewById(R.id.searchBar);

        query = getIntent().getExtras().getString("query");

        //create a seperate adapter for maps activity search bar
        String[] items = {"tea", "apple", "phone case", "tooth paste", "tennis racket", "Tooth brush", "Tooth pick"}; //this is for testing purposes
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_expandable_list_item_1, items);
        actv.setAdapter(adapter);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

            //everything after this is for prototyping
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }else{
                Toast.makeText(MapsActivity.this, "gps açık değil", Toast.LENGTH_LONG).show();
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            }


            //UiSettings.setMyLocationButtonEnabled(true);

            //will uncomment when server is up
            // new webGetSetMarkers().execute(url, query);
            LatLng cafeNero = new LatLng(41.044400, 29.006949);
            Marker Cafe_nero = mMap.addMarker(new MarkerOptions()
                    .position(cafeNero)
                    .title("Cafe Nero")
                    .snippet("Snippets\nare\ngood.")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_default)));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cafeNero, 16.5f));

            LatLng kukaKafe = new LatLng(41.043850, 29.006359);
            mMap.addMarker(new MarkerOptions().position(kukaKafe).title("Kuka Kafe & Pub"));
            LatLng sahilRest = new LatLng(41.041835, 29.009481);
            mMap.addMarker(new MarkerOptions().position(sahilRest).title("Sahil Rest Cafe"));
            Cafe_nero.showInfoWindow();

            //sets the autocomplete text to the search bar and searches for the results
            actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    String mapsQuery = actv.getText().toString();
                    bar.setText(mapsQuery);
                    bar.setSelection(mapsQuery.length()); //set the cursor position
                    if(mapsQuery.length() > 0 && isValid(mapsQuery)){
                        query = mapsQuery;
                        new webGetSetMarkers().execute(url, query);
                    }else{
                        Toast.makeText(MapsActivity.this, "Invalid query.", Toast.LENGTH_LONG).show();
                    }
                }
            });


        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                // TODO Auto-generated method stub

                CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
                CameraUpdate zoom=CameraUpdateFactory.zoomTo(12);

                mMap.moveCamera(center);
                mMap.animateCamera(zoom);
            }
        });
    }

    //returns true if its a valid query
    private boolean isValid(String s){
        Matcher mMatch = mPattern.matcher(s);
        return mMatch.matches();
    }

     //url, query, void ---- params[0], params[1], void
    private class webGetSetMarkers extends AsyncTask<String, String, Void>{
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
            for (Shop sh : shopList){
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

