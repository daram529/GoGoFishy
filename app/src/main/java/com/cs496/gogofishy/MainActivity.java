package com.cs496.gogofishy;

import android.app.ActivityManager;
import android.graphics.drawable.Drawable;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cs496.gogofishy.service.SensorService;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Intent intent;

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

        intent=new Intent(this, SensorService.class);

        Button startButton = (Button)findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(intent);
                Log.d("activity", "button clicked");
            }
        });

        Button stopButton = (Button)findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                stopService(intent);
                Log.d("stop", "button clicked");
            }
        });

    }


}




//        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
//        for (Sensor sensor : sensors)
//        {
//            sm.registerListener(sensorListener, sensor, 1000000);
//            Log.e("sensor TYPE:", sensor.getName()+":"+sensor.getType());
//            if (sensor.getType()==Sensor.TYPE_LIGHT){
//                Log.e("Sensor found", sensor.getName());
//            }
//        }