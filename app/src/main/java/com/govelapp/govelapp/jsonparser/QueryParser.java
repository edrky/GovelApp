package com.govelapp.govelapp.jsonparser;

/*
Query
{"query" : "$query",
  "places" : [
    {"id" : "$id",
      "name" : "$name",
      "mainCategory" : "$category",
      "long" : "$long",
      "lat" : "$lat"
    },
    {"id" : "$id",
      "name" : "$name",
      "mainCategory" : "$category",
      "long" : "$long",
      "lat" : "$lat"
    },
    ...
  ]
}
Details
{"id" : "$id",
  "name" : "$name",
  "address" : "$address",
  "desc" : "$desc",
  "contact" : "$contact",
  "products" : "$product",
  "categories" : "$categories$",
  "services" : "$services",
  "times" : "$time",
  "image" : {
    "type" : "${jpg,png...}",
    "url" : "$url"
  }
}
 */

import android.util.Log;

import com.govelapp.govelapp.shopclasses.Shop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QueryParser {
    public static final String TAG = "QueryParser";

    public static List<Shop> shopParser(String restReply){
        List<Shop> shopList = new ArrayList<Shop>();
        try {
            JSONObject jsonRoot = new JSONObject(restReply);
            JSONArray places = jsonRoot.getJSONArray("places");

            for (int i=0; i<places.length(); i++){
                JSONObject place = places.getJSONObject(i);
                Shop shop = new Shop();
            }

        } catch (JSONException e) {
            Log.d(TAG, "error: can't parse json");
        }


        return null;
    }
}
