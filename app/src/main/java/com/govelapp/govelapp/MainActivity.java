package com.govelapp.govelapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.animation.Animation.AnimationListener;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AccelerateInterpolator;




public class MainActivity extends AppCompatActivity {
    public static final String tag = "MainActivity";
    
    private AutoCompleteTextView actv;
    private EditText bar;
    private ImageView logo;
    boolean isHidden = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logo = (ImageView)findViewById(R.id.logoImg);
        actv = (AutoCompleteTextView) findViewById(R.id.searchBar);
        bar = (EditText)findViewById(R.id.searchBar);


        String[] items = {"tea", "apple", "phone case", "tooth paste", "tennis racket", "Tooth brush", "Tooth pick"}; //veriler
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


        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {
                Intent queryIntent = new Intent(MainActivity.this, MapsActivity.class);
                String query = actv.getText().toString();
                Log.d(tag, query);
                bar.setText(query);
                bar.setSelection(query.length()); //set the cursor position
                    queryIntent.putExtra("query", query);
                    startActivity(queryIntent);

            // Toast.makeText(MainActivity.this, query, Toast.LENGTH_LONG).show();
            }
        });



    }

    private void fadeOutAndHideImage(final ImageView logo)
    {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        //fadeOut.setStartOffset(100); // Start fading out after 100 milli seconds
        fadeOut.setDuration(800);

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
