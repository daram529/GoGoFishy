package com.cs496.gogofishy;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.BatteryManager;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cs496.gogofishy.receiver.PowerConnectionReceiver;
import com.cs496.gogofishy.service.SensorService;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    TextView fishy;
    Typeface font;
    EditText chat;
    SpeechRecognizer mRecognizer;
    Intent i;
    SensorService service = new SensorService();

    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout bg = (RelativeLayout) findViewById(R.id.activity_main);
        GifDrawable oceangif = null;
        try {
            oceangif = new GifDrawable(getAssets(), "rosing.gif");
            bg.setBackground(oceangif);
        } catch (IOException e) {
            e.printStackTrace();
        }

        intent = new Intent(this, SensorService.class);
        startService(intent);

        //Battery status
        Intent batteryStatus = this.registerReceiver(PowerConnectionReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        //Location
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null)
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        // GET RESPONSE
        String res = null;
        try {
            res = new sendToServer.getResponse(getString(R.string.ip_address) + "/?long=" + longitude + "&lat=" + latitude, "application/String").execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.e("response", res + "");

        fishy = (TextView) findViewById(R.id.fishSaying);
        font = Typeface.createFromAsset(getAssets(), "fonts/Mutjinueonuenal.ttf");
        fishy.setTypeface(font);
        fishy.setText(res);

        Button roseButton = (Button) findViewById(R.id.roseButton);
        roseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GET RESPONSE
                String res = null;
                try {
                    res = new sendToServer.getResponse(getString(R.string.ip_address) + "/?long=" + longitude + "&lat=" + latitude, "application/String").execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                fishy.setText(res);
            }
        });

        chat = (EditText) findViewById(R.id.chat);
        chat.setTypeface(font);

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        Button tempButton = (Button) findViewById(R.id.temp);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.disable_sound = true;
                Log.e("button", "clicked");
                mRecognizer.startListening(i);
                Toast.makeText(getBaseContext(), "장미에게 예쁜 말을 해주세요", Toast.LENGTH_SHORT).show();
            }
        });


        Button chatButton = (Button) findViewById(R.id.chatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendMsg = chat.getText().toString();
                String res = null;
                try {
                    res = new sendToServer.getResponse(getString(R.string.ip_address) + "/message?msg=" + URLEncoder.encode(sendMsg, "utf-8"), "application/String").execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.e("response", res + "");
                fishy.setText(res);
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.e("Main", "longitude:"+longitude + " ,latitude:"+latitude);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    private RecognitionListener listener = new RecognitionListener() {

        @Override
        public void onRmsChanged(float rmsdB) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onResults(Bundle results) {
            // TODO Auto-generated method stub
            Log.e("im here", "TT");
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            chat.setText(rs[0]+"");
            Log.e("speech Text", rs[0]+"");
            App.disable_sound = false;
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(int error) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onEndOfSpeech() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onBeginningOfSpeech() {
            // TODO Auto-generated method stub

        }
    };

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(PowerConnectionReceiver);
        super.onDestroy();
    }

    //Battery receiver. 여기에서 widget 건드려야겠다.
    private BroadcastReceiver PowerConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float)scale;

            Log.d("PowerReceiver", isCharging + " , "+batteryPct);
        }
    };
}
