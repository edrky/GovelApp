package com.govelapp.govelapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.view.animation.Animation.AnimationListener;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AccelerateInterpolator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    public static final String tag = "MainActivity";

    private AutoCompleteTextView actv;
    private EditText bar;
    private ImageView logo;
    boolean isHidden = false;
    private Button searchButton;
    private GridView storesList; //list of places like supermarket or grocery store
    //our valid characters
    private static final Pattern queryPattern = Pattern.compile("[a-zA-Z \t]+");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logo = (ImageView)findViewById(R.id.logoImg);
        actv = (AutoCompleteTextView) findViewById(R.id.searchBar);
        bar = (EditText)findViewById(R.id.searchBar);
        searchButton = (Button)findViewById(R.id.searchButton);
        storesList = (GridView)findViewById(R.id.stores_list);

        //will get from our database per week
        String[] items = {"tea", "apple", "phone case", "tooth paste", "tennis racket", "Tooth brush", "Tooth pick"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,items);
        actv.setAdapter(adapter);

        actv =(AutoCompleteTextView)findViewById(R.id.searchBar);

        bar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(!isHidden) {
                        fadeOutAndHideImage(logo);   //make search bar fade out
                        isHidden = true;
                    }
            }
        });

        //writes the text to searchBar
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {
                String s = actv.getText().toString();
                bar.setText(s);
                bar.setSelection(s.length()); //set the cursor position
            }
        });

        //searchButton doSearch
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = bar.getText().toString();
                if(query.length() > 0 && isValid(query)){
                    doSearch(query);
                }else{
                    Toast.makeText(MainActivity.this, "Invalid query.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //search starter for keyboard
        bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent keyEvent) {
                String query = bar.getText().toString();
                if(i == EditorInfo.IME_ACTION_SEARCH && query.length() > 0 && isValid(query)){
                   doSearch(query);
                    return true;
                }
                else{
                    Toast.makeText(MainActivity.this, "Invalid query.", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });
    }

    private void fadeOutAndHideImage(final ImageView logo)
    {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        //fadeOut.setStartOffset(100); // Start fading out after 100 milli seconds
        fadeOut.setDuration(300);

        fadeOut.setAnimationListener(new AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                logo.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        logo.startAnimation(fadeOut);
    }

    private void doSearch(String query){
        Intent queryIntent = new Intent(MainActivity.this, MapsActivity.class);
        Log.d(tag, query);
        queryIntent.putExtra("query", query);
        startActivity(queryIntent);
    }

    //returns true if its a valid query
    private boolean isValid(String s){
            Matcher mMatch = queryPattern.matcher(s);
            return mMatch.matches();
    }
}