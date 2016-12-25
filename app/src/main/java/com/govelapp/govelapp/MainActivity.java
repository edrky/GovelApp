package com.govelapp.govelapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.view.animation.Animation.AnimationListener;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AccelerateInterpolator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    public static final String tag = "MainActivity";

    private AutoCompleteTextView searchBar;
    private ImageView logo;
    private Button searchButton;
    private static final Pattern queryPattern = Pattern.compile("[a-zA-Z \t/&]+");
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo = (ImageView) findViewById(R.id.logoView);
        logo.setVisibility(View.VISIBLE);
        searchBar = (AutoCompleteTextView) findViewById(R.id.searchBar);
        searchButton = (Button) findViewById(R.id.searchButton);

        mToolBar = (Toolbar) findViewById(R.id.main_toolbar);
        mToolBar.setLogo(R.drawable.ic_launcher);

        //will get from our database per week
        String[] items = {"Market & Food/Food/Cheese",
                "Market & Food/Food/Kasar Cheese",
                "Market & Food/Food/Cream Cheese",
                "Market & Food/Food/Vodka",
                "Market & Food/Food/Rum",
                "Market & Food/Food/Raki",
                "Grocery/Vegetables/Chilli",
                "Grocery/Vegetables/Green Pepper",
                "Grocery/Vegetables/Chili",
                "Grocery/Fruits/Strawberry",
                "Grocery/Fruits/Grape",
                "Grocery/Fruits/Grapefruit",
                "Copy & Print/Print/Letterhead",
                "Copy & Print/Print/Invitation Card Printing",
                "Copy & Print/Print/Pillow Printing",
                "Copy & Print/Print/Scanning",
                "Copy & Print/Print/Photocopy"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, items);
        searchBar.setAdapter(adapter);

        searchBar = (AutoCompleteTextView) findViewById(R.id.searchBar);

        searchBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(logo.isShown()){
                    fadeOutAndHideImage(logo);   //make search searchBar fade out
                }
            }
        });

        //writes the text to searchBar
        searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = searchBar.getText().toString();
                searchBar.setText(s);
                searchBar.setSelection(s.length()); //set the cursor position
            }
        });

        //searchButton doSearch
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchBar.getText().toString();
                if (query.length() > 0 && isValid(query)) {
                    doSearch(query);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid query.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //search starter for keyboard
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent keyEvent) {
                String query = searchBar.getText().toString();
                if (i == EditorInfo.IME_ACTION_SEARCH && query.length() > 0 && isValid(query)) {
                    doSearch(query);
                    return true;
                } else {
                    Toast.makeText(MainActivity.this, "Invalid query.", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });
    }

    private void fadeOutAndHideImage(final ImageView logo) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        //fadeOut.setStartOffset(100); // Start fading out after 100 milli seconds
        fadeOut.setDuration(700);

        fadeOut.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                logo.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        logo.startAnimation(fadeOut);
    }

    private void doSearch(String query) {
        Intent queryIntent = new Intent(MainActivity.this, MapsActivity.class);
        Log.d(tag, query);
        queryIntent.putExtra("query", query);
        startActivity(queryIntent);
        //will add a transation
    }

    //returns true if its a valid query
    private boolean isValid(String s) {
        Matcher mMatch = queryPattern.matcher(s);
        return mMatch.matches();
    }
}