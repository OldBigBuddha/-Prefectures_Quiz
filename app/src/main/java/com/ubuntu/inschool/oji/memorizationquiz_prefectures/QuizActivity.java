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
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class QuizActivity extends AppCompatActivity implements Runnable, LoaderManager.LoaderCallbacks<JSONArray> {

    private ProgressDialog progressDialog   = null;
    private Thread         thread           = null;

    private TextView       question         = null;
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
    private final String   DFILENAME        = "Dummy.csv";

    private FileManager    QFileManager     = null;
    private FileManager    DFileManager     = null;

    //問題・ダミーデータ
    private Map<Integer, Map<String, String>>       prefecturs_Name     = new HashMap<>();      //番号：都道府県：県庁所在地
    private List<String>                            prefecturs_dummy    = new ArrayList<>();    //ダミー用


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
        DFileManager = new FileManager(new File(getFilesDir().toString() + "/" + DFILENAME));

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
                        connenct(QTargetJSON.getString(RAW_URL), Datatype.QUESITION);
                    }

                    if (object.optJSONObject(FILES).optJSONObject(DFILENAME) != null) {
                        JSONObject DTargetJSON = object.getJSONObject(FILES).getJSONObject(DFILENAME);
                        connenct(DTargetJSON.getString(RAW_URL), Datatype.DUMMY);
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
                    setText();
                    if (datatype == Datatype.DUMMY) {
                        progressDialog.dismiss();
                    }
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
        if (datatype == Datatype.QUESITION) {
            QFileManager.createFile();
            QFileManager.setFileContents(csv);
            QFileManager.write();
            parseCSV(QFileManager, Datatype.QUESITION);
        }

        if (datatype == Datatype.DUMMY) {
            DFileManager.createFile();
            DFileManager.setFileContents(csv);
            DFileManager.write();
            parseCSV(DFileManager, Datatype.DUMMY);
        }
    }

    public void parseCSV(FileManager fileManager, Datatype datatype) {
        String csv = fileManager.read();
        int count = 0;
        for (String s:csv.split("\n")) {
            String[] value = s.split(",");
            if (count == 0) {
                count++;
                continue;
            } else {
                if (datatype == Datatype.QUESITION) {
                    int number = Integer.valueOf(value[0]);
                    prefecturs_Name.put(number, new HashMap<String, String>());
                    prefecturs_Name.get(number).put(value[1], value[2]);
//                    questionNumber.add(count);
                } else if (datatype == Datatype.DUMMY) {
                    prefecturs_dummy.add(value[0]);
//                    dummyNumber.add(count);
                }
                count++;
            }
        }
        Log.d("Parse", "Finished!");
//        if (datatype == Datatype.DUMMY) setText();
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

    public void setText() {
        int random = new Random().nextInt(47);
        if (!prefecturs_Name.get(random).isEmpty()) {
        Iterator<String> iterator = prefecturs_Name.get(random).keySet().iterator();
        question.setText(iterator.next());
        }
//        prefecturs_Name.get(random);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            question  = (TextView)findViewById(R.id.prefecturesName);

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
    DUMMY, QUESITION
}
