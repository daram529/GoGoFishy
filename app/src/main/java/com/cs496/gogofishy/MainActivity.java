package com.cs496.gogofishy;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout bg = (RelativeLayout) findViewById(R.id.activity_main);
        GifDrawable oceangif = null;
        try {
            oceangif = new GifDrawable(getAssets(), "beach.gif" );
            bg.setBackground(oceangif);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
