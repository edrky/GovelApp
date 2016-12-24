package com.govelapp.govelapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

    private RelativeLayout mDrawerLayout;
    private ImageView drawerFoto;

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
        UiSettings mUI = mMap.getUiSettings();
        mUI.setZoomControlsEnabled(false);
        mUI.setMapToolbarEnabled(true);
        mUI.setCompassEnabled(false);
        mMap.setOnMarkerClickListener(this);

        //need to hide the keyboard, couldn't figure out how
        View view = this.getCurrentFocus();
        view.clearFocus();

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(MapsActivity.this, "Location permission is disabled.", Toast.LENGTH_SHORT).show();
            //request permission
        }

        showcaseBesiktas();

        drawerFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //fotoyu büyült
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker){
        nameText.setText(marker.getTitle());
        adressText.setText("Serencebey Yokuşu Sk. NO:11A Beşiktaş");
        telText.setText("0212 327 0328");
        webText.setText("google.com");
        hoursText.setText("Pazartesi:\t06.00-24.00\n" +
                "\t\t\tSalı:\t\t06.00-24.00\t\n" +
                "\t\t\tÇarşamba:\t06.00-24.00\n" +
                "\t\t\tPerşembe:\t06.00-24.00\n" +
                "\t\t\tCuma:\t\t06.00-24.00\n" +
                "\t\t\tCumartesi:\t06.00-24.00\n" +
                "\t\t\tPazar:\t06.00-24.00");

       if(!webText.getText().toString().isEmpty()){
           webText.setClickable(true);
           webText.setMovementMethod(LinkMovementMethod.getInstance());
       }

        mDrawerLayout.setVisibility(View.VISIBLE);
        mDrawerLayout.bringToFront();
        Log.d("Drawer", "is visible");

        return false;
    }

    private void showcaseBesiktas() {
        LatLng meydanMarket = new LatLng(41.043694, 29.008614);
        Marker Market = mMap.addMarker(new MarkerOptions()
                .position(meydanMarket)
                .title("Meydan Market")
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flare_black_48dp)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(meydanMarket, 16.5f));

        LatLng kukaKafe = new LatLng(41.043850, 29.006359);
        mMap.addMarker(new MarkerOptions().position(kukaKafe).title("Kuka Kafe & Pub"));
        LatLng sahilRest = new LatLng(41.041835, 29.009481);
        mMap.addMarker(new MarkerOptions().position(sahilRest).title("Sahil Rest Cafe"));
        Market.showInfoWindow();
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

