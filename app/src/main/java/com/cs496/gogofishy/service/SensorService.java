package com.cs496.gogofishy.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.cs496.gogofishy.MainActivity;
import com.cs496.gogofishy.R;
import com.cs496.gogofishy.SoundMeter;

import java.io.IOException;

/**
 * Created by q on 2017-01-20.
 */

public class SensorService extends Service{
    private static final String TAG = "SensorService";
    SensorManager sm;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 1000;
    SoundMeter mSensor;

    NotificationManager nm;
    PendingIntent pendingIntent;

    static long[] disable_t = {0, 0, 0, 0};
    static boolean[] disable_b = {false, false, false, false};

    boolean half_round = false;
    int full_round=0;
    long round_time = 0;

    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        super.onDestroy();
//        thread.stopForever();
//        thread = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        Toast.makeText(this, "My Service onStartCommand", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onCreate(){
        Log.d(TAG, "onCreate");
        Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();

        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        Sensor accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor prox = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        Sensor light = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        Sensor step = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor orien = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        sm.registerListener(sensorListener, light, 100000);
        sm.registerListener(sensorListener, prox, 100000);
        sm.registerListener(sensorListener, accel, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(sensorListener, step, 100000);
        sm.registerListener(sensorListener, orien, SensorManager.SENSOR_DELAY_FASTEST);

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(getBaseContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);


        mSensor = new SoundMeter();

        try {
            mSensor.start();
            Toast.makeText(getBaseContext(), "Sound sensor initiated.", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void setDisable_b(int index, boolean disable){
        disable_b[index] = disable;
    }

    public static void setDisable_t(int index, long t){
        disable_t[index] = t;
    }

    public SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
//            Log.e("disable_b", disable_b[0]+" , "+disable_b[1]+" , "+disable_b[2]+" , "+disable_b[3]);
            double decibel = mSensor.getDecibel();
            if (decibel > 85){
                if (!disable_b[0]){
                    Log.i(TAG, "too loud (decibel over 85)");

                    Notification.Builder mBuilder = new Notification.Builder(getBaseContext());
                    mBuilder.setSmallIcon(R.drawable.ic_shark);
                    mBuilder.setTicker("Notification.Builder");
                    mBuilder.setContentTitle("시끄러워!!!!!");
                    mBuilder.setContentText("시끄러워서 잠을 잘 수가 없어!!");
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setContentIntent(pendingIntent);
                    mBuilder.setAutoCancel(true);
                    mBuilder.setPriority(Notification.PRIORITY_MAX);
                    mBuilder.setOnlyAlertOnce(true);
                    mBuilder.setLights(100, 1000, 1000); //set color flashing when alarm
                    mBuilder.setGroup("Crying");

                    Intent careReceive = new Intent(getBaseContext(), NotificationReceiver.class);
                    careReceive.putExtra("notifyID", 99 );
                    careReceive.setAction("S_CARE_ACTION");
                    PendingIntent pendingCare = PendingIntent.getBroadcast(getBaseContext(), 12345, careReceive, PendingIntent.FLAG_UPDATE_CURRENT);

                    Intent ignoreReceive = new Intent(getBaseContext(), NotificationReceiver.class);
                    ignoreReceive.setAction("IGNORE_ACTION");
                    PendingIntent pendingIgnore = PendingIntent.getBroadcast(getBaseContext(), 12345, ignoreReceive, PendingIntent.FLAG_UPDATE_CURRENT);

                    mBuilder.addAction(R.drawable.ic_shark,"달래주기",pendingCare);
                    mBuilder.addAction(R.drawable.ic_shark,"무시하기",pendingIgnore);

                    nm.notify(99, mBuilder.build());
                } else {
                    if (System.currentTimeMillis() - disable_t[0] > 60000){
                        setDisable_b(0, false);
                    }
                }
            }else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
                if (event.values[0] <1 && !disable_b[1]) {
                    Log.i("too close", "aha");

                    Notification.Builder mBuilder = new Notification.Builder(getBaseContext());
                    mBuilder.setSmallIcon(R.drawable.ic_shark);
                    mBuilder.setTicker("Notification.Builder");
                    mBuilder.setContentTitle("저리 치워!!!!!");
                    mBuilder.setContentText("내 눈 앞에서 치우란말야!!");
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setContentIntent(pendingIntent);
                    mBuilder.setAutoCancel(true);
                    mBuilder.setPriority(Notification.PRIORITY_MAX);
                    mBuilder.setOnlyAlertOnce(true);
                    mBuilder.setLights(100, 1000, 1000); //set color flashing when alarm
                    mBuilder.setGroup("Crying");

                    Intent careReceive = new Intent(getBaseContext(), NotificationReceiver.class);
                    careReceive.putExtra("notifyID", 100);
                    careReceive.setAction("P_CARE_ACTION");
                    PendingIntent pendingCare = PendingIntent.getBroadcast(getBaseContext(), 12345, careReceive, PendingIntent.FLAG_UPDATE_CURRENT);

                    Intent ignoreReceive = new Intent(getBaseContext(), NotificationReceiver.class);
                    ignoreReceive.setAction("IGNORE_ACTION");
                    PendingIntent pendingIgnore = PendingIntent.getBroadcast(getBaseContext(), 12345, ignoreReceive, PendingIntent.FLAG_UPDATE_CURRENT);

                    mBuilder.addAction(R.drawable.ic_shark, "달래주기", pendingCare);
                    mBuilder.addAction(R.drawable.ic_shark, "무시하기", pendingIgnore);

                    nm.notify(100, mBuilder.build());
                } else{
                    if (System.currentTimeMillis() - disable_t[1] > 60000){
                        setDisable_b(1, false);
                    }
                }
            } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                long curTime = System.currentTimeMillis();
                if ((curTime - lastUpdate) > 100 && !disable_b[2]) {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;

                    float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                    if (speed > SHAKE_THRESHOLD) {
                        Log.d("motion", "shake gesture");

                        Notification.Builder mBuilder = new Notification.Builder(getBaseContext());
                        mBuilder.setSmallIcon(R.drawable.ic_shark);
                        mBuilder.setTicker("Notification.Builder");
                        mBuilder.setContentTitle("흔들지 말란 말야!");
                        mBuilder.setContentText("너무 어지러워 ㅠㅠ");
                        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                        mBuilder.setContentIntent(pendingIntent);
                        mBuilder.setAutoCancel(true);
                        mBuilder.setPriority(Notification.PRIORITY_MAX);
                        mBuilder.setOnlyAlertOnce(true);
                        mBuilder.setLights(100, 1000, 1000); //set color flashing when alarm
                        mBuilder.setGroup("Crying");

                        Intent careReceive = new Intent(getBaseContext(), NotificationReceiver.class);
                        careReceive.putExtra("notifyID", 101);
                        careReceive.setAction("A_CARE_ACTION");
                        PendingIntent pendingCare = PendingIntent.getBroadcast(getBaseContext(), 12345, careReceive, PendingIntent.FLAG_UPDATE_CURRENT);

                        Intent ignoreReceive = new Intent(getBaseContext(), NotificationReceiver.class);
                        ignoreReceive.setAction("IGNORE_ACTION");
                        PendingIntent pendingIgnore = PendingIntent.getBroadcast(getBaseContext(), 12345, ignoreReceive, PendingIntent.FLAG_UPDATE_CURRENT);

                        mBuilder.addAction(R.drawable.ic_shark,"달래주기",pendingCare);
                        mBuilder.addAction(R.drawable.ic_shark,"무시하기",pendingIgnore);

                        nm.notify(101, mBuilder.build());
                    }

                    last_x = x;
                    last_y = y;
                    last_z = z;

                } else {
                    if (System.currentTimeMillis() - disable_t[2] > 60000){
                        setDisable_b(2, false);
                    }
                }
            } else if (event.sensor.getType() == Sensor.TYPE_LIGHT){
                if (event.values[0] < 100 && !disable_b[3]){
                    Notification.Builder mBuilder = new Notification.Builder(getBaseContext());
                    mBuilder.setSmallIcon(R.drawable.ic_shark);
                    mBuilder.setTicker("Notification.Builder");
                    mBuilder.setContentTitle("깜깜한건 싫어!!!");
                    mBuilder.setContentText("앞이 안보여 무섭단 말이에요 ㅠㅠ");
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    mBuilder.setContentIntent(pendingIntent);
                    mBuilder.setAutoCancel(true);
                    mBuilder.setPriority(Notification.PRIORITY_MAX);
                    mBuilder.setOnlyAlertOnce(true);
                    mBuilder.setLights(100, 1000, 1000); //set color flashing when alarm
                    mBuilder.setGroup("Crying");

                    Intent careReceive = new Intent(getBaseContext(), NotificationReceiver.class);
                    careReceive.putExtra("notifyID", 102);
                    careReceive.setAction("L_CARE_ACTION");
                    PendingIntent pendingCare = PendingIntent.getBroadcast(getBaseContext(), 12345, careReceive, PendingIntent.FLAG_UPDATE_CURRENT);

                    Intent ignoreReceive = new Intent(getBaseContext(), NotificationReceiver.class);
                    ignoreReceive.setAction("IGNORE_ACTION");
                    PendingIntent pendingIgnore = PendingIntent.getBroadcast(getBaseContext(), 12345, ignoreReceive, PendingIntent.FLAG_UPDATE_CURRENT);

                    mBuilder.addAction(R.drawable.ic_shark,"달래주기",pendingCare);
                    mBuilder.addAction(R.drawable.ic_shark,"무시하기",pendingIgnore);

                    nm.notify(102, mBuilder.build());
                } else {
                    if (System.currentTimeMillis() - disable_t[3] > 60000){
                        setDisable_b(3, false);
                    }
                }
            } else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION){
                float azimuth = event.values[0];
                float pitch = event.values[1];
                float roll = event.values[2];

                if (pitch > 27){
                    half_round = true;
                }
                if (pitch < -27){
                    if (half_round && (System.currentTimeMillis() - round_time < 3000)){
                        full_round +=1;
                        half_round = false;
                        round_time = System.currentTimeMillis();
                    }
                    else if (System.currentTimeMillis()- round_time > 3000){
                        full_round = 0;
                        round_time = System.currentTimeMillis();
                    }
                }


                if (full_round > 3){
                    Log.e("Orientation", "달래졌다!!!");
                    full_round = 0;
                }





//                Log.e("Orientation", "달래기 성공");

            } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
                Log.e("StepCounter", event.values[0]+"");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Integer num = intent.getIntExtra("notifyID", 0);
            Log.e("num", num+"");
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            SensorService ss = new SensorService();
            if ("CARE_ACTION".equals(action)) {
                Log.e(TAG, "CARE CALLED");
                Toast.makeText(context, "CARE CALLED", Toast.LENGTH_SHORT).show();
                nm.cancel(num);
            } else if ("S_CARE_ACTION".equals(action)) {
                nm.cancel(num);
                ss.setDisable_b(0, true);
                ss.setDisable_t(0, System.currentTimeMillis());
            } else if ("P_CARE_ACTION".equals(action)) {
                nm.cancel(num);
                ss.setDisable_b(1, true);
                ss.setDisable_t(1, System.currentTimeMillis());
            } else if ("A_CARE_ACTION".equals(action)) {
                nm.cancel(num);
                ss.setDisable_b(2, true);
                ss.setDisable_t(2, System.currentTimeMillis());
            } else if ("L_CARE_ACTION".equals(action)) {
                nm.cancel(num);
                ss.setDisable_b(3, true);
                ss.setDisable_t(3, System.currentTimeMillis());
            } else if ("IGNORE_ACTION".equals(action)) {
                Log.e(TAG, "IGNORE CALLED");
                Toast.makeText(context, "IGNORE CALLED", Toast.LENGTH_SHORT).show();
                nm.cancelAll();
            }
        }
    }
}
