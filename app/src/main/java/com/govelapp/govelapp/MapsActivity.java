package com.govelapp.govelapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;

import com.govelapp.govelapp.jsonparser.QueryParser;
import com.govelapp.govelapp.locationmenager.LocationManagerCheck;
import com.govelapp.govelapp.restclient.RestClient;
import com.govelapp.govelapp.shopclasses.Shop;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    //our valid characters OnMapReadyCallback
    private static final Pattern queryPattern = Pattern.compile("[a-zA-Z \t]+");
    private GoogleMap mMap;
    private AutoCompleteTextView actv;
    private String url = "govelapp.com/api";     //getResources().getString(R.string.url);
    private List<Shop> shopList;
    private String query;

    private RelativeLayout mDrawerLayout;
    private ImageView drawerFoto;
    private ScrollView mScrollView;
    private Marker selectedMarker;
    private LocationManager locationManager;

    private static TextView nameText,adressText,telText,webText,hoursText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //basic setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //create right side bar
        mDrawerLayout = (RelativeLayout) findViewById(R.id.mainLinearLayout);
        nameText = (TextView) findViewById(R.id.text);
        adressText = (TextView) findViewById(R.id.text1);
        telText = (TextView) findViewById(R.id.text3);
        webText = (TextView) findViewById(R.id.text4);
        hoursText = (TextView) findViewById(R.id.text2);
        drawerFoto = (ImageView) findViewById(R.id.imageView);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);

        //create a seperate adapter for maps activity search actv
        actv = (AutoCompleteTextView) findViewById(R.id.searchBar);
        String[] items = {"tea", "apple", "phone case", "tooth paste", "tennis racket", "tooth brush", "tooth pick", "kahve"}; //this is for testing purposes
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_expandable_list_item_1, items);
        actv.setAdapter(adapter);
        actv.setText(getIntent().getExtras().getString("query"));
        actv.clearFocus();
        actv.setSelection(actv.getText().length());
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
        mMap.setIndoorEnabled(false);
        UiSettings mUI = mMap.getUiSettings();
        mUI.setZoomControlsEnabled(false);
        mUI.setMyLocationButtonEnabled(false);
        mUI.setMapToolbarEnabled(true);
        mUI.setCompassEnabled(false);
        mMap.setOnMarkerClickListener(this);

        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.custom_map);
        mMap.setMapStyle(style);

        //need to hide the keyboard, couldn't figure out how
       /* View view = this.getCurrentFocus();
        view.clearFocus(); */

        LocationManagerCheck locationManagerCheck = new LocationManagerCheck(this);
        Location location = null;

        if(locationManagerCheck.isLocationServiceAvailable()){
            if (locationManagerCheck.getProviderType() == 1){
                //    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            else if (locationManagerCheck.getProviderType() == 2){
                //  location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }else{
            locationManagerCheck.createLocationServiceError(MapsActivity.this);
        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity.this, "Location enabled", Toast.LENGTH_LONG).show();
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(MapsActivity.this, "Location permission is disabled.", Toast.LENGTH_SHORT).show();
            //request permission
        }

        actv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDrawerLayout.isShown()){
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        showcaseBesiktas();
    }

    @Override
    public boolean onMarkerClick(final Marker marker){
        selectedMarker = marker;
        if(actv.isShown()){
            Animation animation1 =
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_up_search_bar);
            actv.startAnimation(animation1);
            actv.setVisibility(View.INVISIBLE);
            UiSettings mUI = mMap.getUiSettings();
            mUI.setMyLocationButtonEnabled(true);
        }
        if(!mDrawerLayout.isShown()){
            Animation animation =  AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.drawer_open);
            mDrawerLayout.startAnimation(animation);
        }
        showDrawer(marker);
        return false;
    }

    private void showDrawer(final Marker marker){
        //experimental
        String hours = "\t6:00-24:00\n";

        nameText.setText(marker.getTitle());
        adressText.setText("Serencebey Yokuşu Sk. NO:11A Beşiktaş");
        telText.setText("0212 327 0328");
        webText.setText("google.com");
        hoursText.setText(R.string.working_hours);
        hoursText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hoursText.setText("\tPazartesi:\n" + "\tSalı:\n" + "\tÇarşamba:\n" + "\tPerşembe:\n" +
                        "\tCuma:\n" +
                        "\tCumartesi:\n" +
                        "\tPazar:");
            }
        });

        if(!webText.getText().toString().isEmpty()){
            webText.setClickable(true);
            webText.setMovementMethod(LinkMovementMethod.getInstance());
        }

        drawerFoto.setX(10);
        drawerFoto.getLayoutParams().height = 400;
        drawerFoto.getLayoutParams().width = 400;

        mDrawerLayout.setVisibility(View.VISIBLE);
        mScrollView.setVisibility(View.VISIBLE);
        mDrawerLayout.bringToFront();

        drawerFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScrollView.setVisibility(View.INVISIBLE);
                drawerFoto.requestLayout();
                drawerFoto.setX(100);
                drawerFoto.getLayoutParams().height = 1000;
                drawerFoto.getLayoutParams().width = 1000;
            }
        });

        Log.d("Marker click", "Drawer is visible");

    }

    private void showcaseBesiktas() {
        String showcasePlaces[][] = {
                {"Derya Promosyon", "41.044232", "29.008083"},
                {"Nokta Copy Center", "41.044087", "29.008058"},
                {"Tasarım ve Fotoğraf", "41.044232", "29.008083"},
                {"Tufan Kırtasiye", "41.043967", "29.008068"},
                {"Sanat Copy Center", "41.043967", "29.008068"},
                {"Tiridi Fabrika", "41.043913", "29.008064"}
        };
        for(int i=0; i<showcasePlaces.length; i++){
            LatLng placeLatLng = new LatLng(Double.parseDouble(showcasePlaces[i][1]),
                    Double.parseDouble(showcasePlaces[i][2]));
            Marker placeMarker = mMap.addMarker(new MarkerOptions()
                    .position(placeLatLng)
                    .title(showcasePlaces[i][0])
                    .snippet("Copy & Print")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flare_black_48dp)));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.044066, 29.008070), 16.5f));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mDrawerLayout.isShown()){
                if(!mScrollView.isShown()){
                    showDrawer(selectedMarker);
                }else{
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    Log.d("Drawer Layout", "is hidden");
                    hoursText.clearComposingText();
                    Animation animation =  AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.drawer_close);
                    mDrawerLayout.startAnimation(animation);

                    Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_down_search_bar);
                    actv.startAnimation(animation1);
                    actv.setVisibility(View.VISIBLE);

                    UiSettings mUI = mMap.getUiSettings();
                    mUI.setMyLocationButtonEnabled(false);
                }
            }else{
                Intent queryIntent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(queryIntent);
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

