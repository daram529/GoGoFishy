package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.BoolRes;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.net.Uri;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cs496.gogofishy.App;
import com.cs496.gogofishy.R;
import com.cs496.gogofishy.RequestService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link FishyWidgetConfigureActivity FishyWidgetConfigureActivity}
 */
public class FishyWidget extends AppWidgetProvider {

    private static final String BUTTON_CLICKED    = "buttonClicked";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.e("widget", "updateAppWidget()");
    }

    public Bitmap buildUpdate(String time, Context context)
    {
        Bitmap myBitmap = Bitmap.createBitmap(300, 150, Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        Typeface clock = Typeface.createFromAsset(context.getAssets(),"fonts/Mutjinueonuenal.ttf");
//        paint.setAntiAlias(true);
//        paint.setSubpixelText(true);

        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(clock);
        paint.setColor(Color.WHITE);
        paint.setTextSize(23);
        paint.setTextAlign(Paint.Align.LEFT);
        int x = 15, y = 30;
        for (int i=0; i< (int)(time.length()-1)/20+1; i++){
            if (i==(int)(time.length()-1)/20)
                myCanvas.drawText(time.substring(i*20, time.length()-1), x, y, paint);
            else
                myCanvas.drawText(time.substring(i*20, i*20+20), x, y, paint);
            y += paint.descent() - paint.ascent();
        }
//        for (String line: time.split("\n")) {
//            myCanvas.drawText(line, x, y, paint);
//            y += paint.descent() - paint.ascent();
//        }
//        myCanvas.drawText(time, 80, 60, paint);
        return myBitmap;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);

        if (BUTTON_CLICKED.equals(intent.getAction())){
            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.fishy_widget);
            final ComponentName widget = new ComponentName(context, FishyWidget.class);
            views.setTextViewText(R.id.appwidget_text, "");

            final Handler handler = new Handler();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        URL url = new URL("http://52.79.161.158:8080/");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        try {
                            conn.setRequestMethod("GET");
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        }
                        conn.setRequestProperty("Content-Type", "application/String");
                        conn.setRequestProperty("Accept-Charset", "UTF-8");
                        InputStream is = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        String line;
                        StringBuffer response = new StringBuffer();
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                            response.append("\n");
                        }
                        reader.close();
                        final String res = response.toString();
                        conn.disconnect();
                        Log.e("Widget", res);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
//                                views.setTextViewText(R.id.appwidget_text, res);
                                views.setImageViewBitmap(R.id.imageview, buildUpdate(res, context));
                                appWidgetManager.updateAppWidget(widget, views);
                            }
                        });
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }.start();


        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    //update minimum every 30 minutes
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.e("widget", "testing" + appWidgetIds.length);
        CharSequence widgetText = FishyWidgetConfigureActivity.loadTitlePref(context, appWidgetIds[0]);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.fishy_widget);
        ComponentName widget = new ComponentName(context, FishyWidget.class);

//        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setOnClickPendingIntent(R.id.button, getPendingSelfIntent(context, BUTTON_CLICKED));

//        float opacity = 0.3f;           //opacity = 0: fully transparent, opacity = 1: no transparancy
//        int backgroundColor = 0x000000;
//        views.setInt(R.layout.fishy_widget, "setBackgroundColor", (int)(opacity * 0xFF) << 24 | backgroundColor);

        appWidgetManager.updateAppWidget(widget, views);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            FishyWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

