package com.govelapp.govelapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (EditText)findViewById(R.id.search_bar);
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if(i == EditorInfo.IME_ACTION_SEARCH && v.getText().length() > 0){
                    //prototype start
                    v.setText("kahve");
                    String query = "kahve";
                    //prototype end
                    //String query = v.getText().toString();
                    Intent queryIntent = new Intent(MainActivity.this, MapsActivity.class);
                    queryIntent.putExtra("query", query);
                    startActivity(queryIntent);
                    handled = true;
                }
                return handled;
            }
        });
    }
}

//Make REST request hereWatashiWaKai
//1. Create url from queryParts and actual url
//2. http connection to the url, and get answer
//3. get map objects
//4. create single group of map markers to pass as intentl
//for now, let's set some example value on button press
//queryString = "kahve pasta";
//text.setText(queryString);
//remove upper lines, just for testing
