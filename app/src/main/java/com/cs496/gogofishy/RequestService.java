package com.cs496.gogofishy;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import layout.FishyWidget;

public class RequestService extends Service {
    static public RequestQueue queue;
    public RequestService() {
    }

    @Override
    public void onCreate() {
        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);
    }

    @Override
    public int onStartCommand(Intent aIntent, int flags, int aStartId) {
        final RemoteViews _views = new RemoteViews(this.getPackageName(), R.layout.fishy_widget);

        String url ="http://52.79.161.158:8080/crawl?genre=fish";
        Log.e("service", "about to send request");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.e("request", response);
                        _views.setTextViewText(R.id.appwidget_text, "Response is: "+ response.substring(0,100));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getBaseContext(), "timeout by volley",Toast.LENGTH_LONG).show();
                }
                Log.e("request", error.toString());
                _views.setTextViewText(R.id.appwidget_text, error.toString());
            }
        });
        //stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        //_views.setTextViewText(R.id.appwidget_text, widgetText);
        ComponentName _widget =
                new ComponentName(this, FishyWidget.class);
        AppWidgetManager _manager =
                AppWidgetManager.getInstance(this);
        _manager.updateAppWidget(_widget, _views);
    return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}