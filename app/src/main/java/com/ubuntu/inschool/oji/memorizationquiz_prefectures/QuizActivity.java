package com.ubuntu.inschool.oji.memorizationquiz_prefectures;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class QuizActivity extends AppCompatActivity implements Runnable, LoaderManager.LoaderCallbacks<JSONArray> {

    private ProgressDialog progressDialog   = null;
    private Thread         thread           = null;

    private Button         btAnswer1        = null;
    private Button         btAnswer2        = null;
    private Button         btAnswer3        = null;
    private Button         btAnswer4        = null;

    private JSONLoader     jsonLoader       = null;
    private JSONArray      array            = null;
    private JSONObject     object           = null;

    private final String   FILES            = "files";
    private final String   RAW_URL          = "raw_url";
    private final String   QFILENAME        = "Todoufuken_Kenchoushozaichi.csv";
    private final String   DUMMY            = "Dummy.csv";

    private FileManager    QFileManager     = null;
    private FileManager    DFileManager     = null;

    //問題・ダミーデータ
    private Map<String, String>     prefecturs_Name     = new HashMap<>();      //都道府県：県庁所在地
    private Map<Integer, String>    prefecturs_Number   = new HashMap<>();      //番号    ：都道府県
    private List<String>            prefecturs_dummy    = new ArrayList<>();    //ダミー用


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //ロードダイアログ生成
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.incrementProgressBy(30);
        progressDialog.incrementProgressBy(70);
        progressDialog.setMessage("Loading Question...");
        progressDialog.show();

        thread = new Thread(this);
        thread.start();

    }

    @Override
    public void run() {
        QFileManager = new FileManager(new File(getFilesDir().toString() + "/" + QFILENAME));
        DFileManager = new FileManager(new File(getFilesDir().toString() + "/" + DUMMY));

        try {
            Thread.sleep(1800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        handler.sendEmptyMessage(0);
    }

    @Override
    public Loader<JSONArray> onCreateLoader(int i, Bundle bundle) {

        jsonLoader = new JSONLoader(getApplicationContext());
        jsonLoader.forceLoad();

        return jsonLoader;
    }

    @Override
    public void onLoadFinished(Loader<JSONArray> loader, JSONArray jsonArray) {
        Log.d("Load", "Succeed");

        if (jsonArray != null) {
            //rawUrlを取得
            try {
                for (int i = 0;i < jsonArray.length(); i++) {
                    object = jsonArray.getJSONObject(i);
                    if (object.optJSONObject(FILES).optJSONObject(QFILENAME) != null) {
                        JSONObject QTargetJSON = object.getJSONObject(FILES).getJSONObject(QFILENAME);
                        connenct(QTargetJSON.getString(RAW_URL), Datatype.QUIESTION);
                    }

                    if (object.optJSONObject(FILES).optJSONObject(DUMMY) != null) {
                        JSONObject DTargetJSON = object.getJSONObject(FILES).getJSONObject(DUMMY);
                        connenct(DTargetJSON.getString(RAW_URL), Datatype.QUIESTION);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<JSONArray> loader) {
        Log.d("Connect","Failed");
    }

    //CSV取得
    public void connenct(final String raw_url, final Datatype datatype) {
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(raw_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                Log.d("Connect", "Succeed");

                try {
                    saveCSV(new String(bytes, "UTF-8"), datatype);
                    progressDialog.dismiss();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    //取得したCSVを保存
    public void saveCSV(String csv, Datatype datatype) {
        if (datatype == Datatype.QUIESTION) {
            QFileManager.createFile();
            QFileManager.setFileContents(csv);
            QFileManager.write();
        } else if (datatype == Datatype.DUMMY) {
            DFileManager.createFile();
            DFileManager.setFileContents(csv);
            DFileManager.write();
        }
    }

    //ネット接続状況取得
    private boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            btAnswer1 = (Button)findViewById(R.id.btAnswer_1);
            btAnswer2 = (Button)findViewById(R.id.btAnswer_2);
            btAnswer3 = (Button)findViewById(R.id.btAnswer_3);
            btAnswer4 = (Button)findViewById(R.id.btAnswer_4);

            getLoaderManager().initLoader(0,null,QuizActivity.this);
//            if (isOnline()) {
//                getLoaderManager().initLoader(0, null, QuizActivity.this);
//            } else {
//                //ファイル読み込み・CSVパース
//            }
        }
    };
}

enum Datatype {
    DUMMY,QUIESTION
}
