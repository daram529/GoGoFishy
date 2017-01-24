package com.cs496.gogofishy;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
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
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by q on 2017-01-23.
 */

public class sendToServer {

    public sendToServer() {
        super();
    }

    // AsyncTask to communicate with Facebook or our MongoDB
    public static class getResponse extends AsyncTask<Void, Void, String> {
        String urlstr;
        String contentType;

        public getResponse(String url, String contentType) {
            this.urlstr = url;
            this.contentType = contentType;
        }

        @Override
        public String doInBackground(Void... params) {
            HttpURLConnection conn;
            InputStream is;
            BufferedReader reader;
            String res;

            try {
                URL url = new URL(urlstr);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", contentType);
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                is = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                    response.append("\n");
                }
                reader.close();
                res = response.toString();
                conn.disconnect();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                return null;
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
            return res;
        }
    }
}