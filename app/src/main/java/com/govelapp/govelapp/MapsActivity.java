package com.govelapp.govelapp;

import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.SearchEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;
import com.govelapp.govelapp.jsonparser.QueryParser;
import com.govelapp.govelapp.restclient.RestClient;
import com.govelapp.govelapp.shopclasses.Shop;

import org.json.JSONArray;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText bar;
    private AutoCompleteTextView actv;
    private String url = "govelapp.com/api"; //values'e koy
    private List<Shop> shopList;
    private String query;

    private void webGetSetMarkers(){
        //implement this as a thread / asynchronously
        RestClient rc = new RestClient();
        String jsonReply = rc.getStandardQueryJson(url, query);

        QueryParser qp = new QueryParser();
        shopList = qp.parseShopList(jsonReply);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        actv = (AutoCompleteTextView)findViewById(R.id.searchBar);
        bar = (EditText) findViewById(R.id.searchBar);

        //create a seperate adapter for maps activity search bar
        String[] items = {"tea", "apple", "phone case", "tooth paste", "tennis racket", "Tooth brush", "Tooth pick"}; //this is for testing purposes
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,items);
        actv.setAdapter(adapter);

        query = getIntent().getExtras().getString("query");

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        webGetSetMarkers();
    }

    //yanlıs olabilir kontrol et
   /* @Override
    public boolean onSearchRequested(SearchEvent searchEvent) {
        query = aramabarı.getStuff.toString
                webgetrequest
    }*/

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

        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                bar.setText(actv.getText().toString());
                bar.setSelection(actv.getText().toString().length()); //set the cursor position
            }
        });

        //everything after this is for prototyping
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }else{
            //prompt user for permission
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.043437, 29.008537), 16.5f));

        for (Shop sh : shopList){
            mMap.addMarker(sh.getMarkerOptions());
        }

        LatLng cafeNero = new LatLng(41.044400, 29.006949);
        mMap.addMarker(new MarkerOptions().position(cafeNero).title("Cafe Nero"));
        LatLng kukaKafe = new LatLng(41.043850, 29.006359);
        mMap.addMarker(new MarkerOptions().position(kukaKafe).title("Kuka Kafe & Pub"));
        LatLng sahilRest = new LatLng(41.041835, 29.009481);
        mMap.addMarker(new MarkerOptions().position(sahilRest).title("Sahil Rest Cafe"));
    }
}
