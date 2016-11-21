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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QueryParser {
    public static final String TAG = "QueryParser";

    public static List<Shop> shopParser(String jsonReply){
        List<Shop> shopList = new ArrayList<Shop>();
        try {
            JSONObject jsonRootObject = new JSONObject(jsonReply);



        } catch (JSONException e) {
            Log.d(TAG, "error: can't parse json");
        }


        return null;
    }
}
