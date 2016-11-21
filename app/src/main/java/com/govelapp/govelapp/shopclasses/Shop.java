package com.govelapp.govelapp.shopclasses;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.govelapp.govelapp.R;

public class Shop {
    final static String TAG = "Shop";

    //should this be public or private? decide and implement
    private enum category{
        CAFE,RESTAURANT,SHOP,MARKET
    }

    private int id;
    private String name;
    private category mainCategory;
    private LatLng position;
    private MarkerOptions marker;
    private BitmapDescriptor icon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public category getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(category mainCategory) {
        this.mainCategory = mainCategory;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public MarkerOptions getMarker() {
        return marker;
    }

    public void setMarker(MarkerOptions marker) {
        this.marker = marker;
    }

    public BitmapDescriptor getIcon() {
        return icon;
    }

    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }
}
