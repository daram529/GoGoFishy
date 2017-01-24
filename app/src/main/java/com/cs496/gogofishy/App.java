package com.cs496.gogofishy;

import android.app.Application;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by q on 2017-01-19.
 */

public class App extends Application {

    public static boolean disable_sound = false;

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
