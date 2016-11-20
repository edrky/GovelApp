package com.govelapp.govelapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.search_button);
        text = (EditText)findViewById(R.id.search_input);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String queryString = text.getText().toString();
                String[] queryParts = queryString.split(" ");

                //Make REST request hereWatashiWaKai

                //1. Create url from queryParts and actual url
                //2. http connection to the url, and get answer
                //3. get map objects
                //4. create single group of map markers to pass as intentl
                //for now, let's set some example value on button press
                queryString = "kahve pasta";
                text.setText(queryString);
                //remove upper lines, just for testing


            }
        });
    }
}
