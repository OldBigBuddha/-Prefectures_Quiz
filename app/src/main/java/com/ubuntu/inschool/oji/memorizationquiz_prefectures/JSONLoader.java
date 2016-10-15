package com.ubuntu.inschool.oji.memorizationquiz_prefectures;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by oji on 16/10/11.
 */
public class JSONLoader extends AsyncTaskLoader<JSONArray>{
  //変数宣言
    public static final String                  API_URL         = "https://api.github.com/users/OldBigBuddha/gists";
    private             HttpsURLConnection      connection      = null;
    private             URL                     api_url         = null;
    private             BufferedInputStream     inputStream     = null;
    private             ByteArrayOutputStream   outputStream    = null;
    private             JSONArray               jsonArray       = null;
    private final       String                  GET             = "GET";
    private             byte[]                  buffer          = new byte[1024];

    public JSONLoader(Context context) {
        super(context);
    }

    @Override
    public JSONArray loadInBackground() {
        try {
            //ネット接続(https://api.github.com/users/OldBigBuddha/gists)
            api_url = new URL(API_URL);
            connection = (HttpsURLConnection) api_url.openConnection();
            connection.setRequestMethod(GET);
            connection.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //JSONArray取得
            inputStream  = new BufferedInputStream(connection.getInputStream());
            outputStream = new ByteArrayOutputStream();

            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            jsonArray   = new JSONArray(new String(outputStream.toByteArray()));

            return jsonArray;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
