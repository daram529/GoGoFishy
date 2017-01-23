package com.cs496.gogofishy;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
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
import android.media.Image;
import android.os.BatteryManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
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

import com.cs496.gogofishy.receiver.PowerConnectionReceiver;
import com.cs496.gogofishy.service.SensorService;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    TextView fishy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout bg = (RelativeLayout) findViewById(R.id.activity_main);
        GifDrawable oceangif = null;
        try {
            oceangif = new GifDrawable(getAssets(), "rosing.gif" );
            bg.setBackground(oceangif);
        } catch (IOException e) {
            e.printStackTrace();
        }

        intent=new Intent(this, SensorService.class);

        //Battery status
        Intent batteryStatus = this.registerReceiver(PowerConnectionReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

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


        // GET RESPONSE
        String res = null;
        try {
            res = new sendToServer.getResponse(getString(R.string.ip_address), "application/String").execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.e("response", res+"");

        fishy = (TextView)findViewById(R.id.fishSaying);
        fishy.setText(res);

        Button chatButton = (Button) findViewById(R.id.chatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText chat = (EditText)findViewById(R.id.chat);
                String sendMsg = chat.getText().toString();
                String res = null;
                try {
                    res = new sendToServer.getResponse(getString(R.string.ip_address)+"/message?msg="+ URLEncoder.encode(sendMsg, "utf-8"), "application/String").execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.e("response", res+"");
                fishy.setText(res);
            }
        });

    }

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
            //이제 이걸 물로 표현하면 되겠네

            TextView tv = (TextView) findViewById(R.id.textView);
            tv.setText("Battery Level: " + Integer.toString(level) + "%");
        }
    };
}
