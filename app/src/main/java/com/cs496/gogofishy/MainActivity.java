package com.cs496.gogofishy;

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

import java.util.List;

public class MainActivity extends AppCompatActivity {

    SensorManager sm;
    NotificationManager nm;
    PendingIntent pendingIntent;

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

        sm = (SensorManager)getSystemService(SENSOR_SERVICE);

//        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
//        for (Sensor sensor : sensors)
//        {
//            sm.registerListener(sensorListener, sensor, 1000000);
//            Log.e("sensor TYPE:", sensor.getName()+":"+sensor.getType());
//            if (sensor.getType()==Sensor.TYPE_LIGHT){
//                Log.e("Sensor found", sensor.getName());
//            }
//        }


        Sensor light = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        sm.registerListener(sensorListener, light, 10000000);


        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sm.unregisterListener(sensorListener);
            }
        });
    }


    public SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
//            sensorType.setText(event.sensor.getType());
//            sensorValue.setText(event.values[0]+"");
//            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
//                if (event.values[0] <1){
//                    Log.i("too close", "aha");
//                }
//            }
            if (event.sensor.getType() == Sensor.TYPE_LIGHT){
                Log.i("value", event.values[0] + "lux");
                if (event.values[0] < 200){
                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(getBaseContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

                    Notification.Builder mBuilder = new Notification.Builder(getBaseContext());
                    mBuilder.setSmallIcon(R.drawable.ic_shark);
                    mBuilder.setTicker("Notification.Builder");
                    mBuilder.setWhen(System.currentTimeMillis());
                    mBuilder.setNumber(10);
                    mBuilder.setContentTitle("Notification.Builder Title");
                    mBuilder.setContentText("Notification.Builder Massage");
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setContentIntent(pendingIntent);
                    mBuilder.setAutoCancel(true);
                    mBuilder.setPriority(Notification.PRIORITY_MAX);

                    nm.notify(111, mBuilder.build());
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

}