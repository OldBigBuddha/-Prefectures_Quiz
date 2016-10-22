package com.ubuntu.inschool.oji.memorizationquiz_prefectures;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class QuizActivity extends AppCompatActivity implements Runnable, LoaderManager.LoaderCallbacks<JSONArray> {

    private ProgressDialog progressDialog   = null;
    private Thread         thread           = null;

    private TextView       counter_View     = null;
    private TextView       question_View    = null;

    private TextView       btAnswer1        = null;
    private TextView       btAnswer2        = null;
    private TextView       btAnswer3        = null;
    private TextView       btAnswer4        = null;

    private ImageView      TFImage1         = null;
    private ImageView      TFImage2         = null;
    private ImageView      TFImage3         = null;
    private ImageView      TFImage4         = null;
    private ImageView      selectedImage    = null;

    private JSONLoader     jsonLoader       = null;
    private JSONObject     object           = null;

    private final String   FILES            = "files";
    private final String   RAW_URL          = "raw_url";
    private final String   QFILENAME        = "Todoufuken_Kenchoushozaichi.csv";
    private final String   DFILENAME        = "Dummy.csv";

    private FileManager    QFileManager     = null;
    private FileManager    DFileManager     = null;

    //問題・ダミーデータ
    private Map<Integer, Map<String, String>>       prefecturs_Name     = new HashMap<>();          //番号：都道府県：県庁所在地
    private List<String>                            prefecturs_dummy    = new ArrayList<>();        //ダミー用
    private List<String>                            answers             = new ArrayList<>();
    private int                                     questionsNumber     = 10;
    private int                                     nowQuestionNum      = 1;

    //ランダム変数
    private ShuffleRandom setQuestionRandom, setAnswersRnadom,dummyRandom;

    private String question_data = "";
    private String answer_data   = "";

    private boolean   isAnswered = false;

    private SoundPlay soundTrue  = null;
    private SoundPlay soundFalse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();
        questionsNumber = intent.getIntExtra(MainActivity.INTENTKEY, 10);

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
        setQuestionRandom = new ShuffleRandom(1, MainActivity.PREFECTURSNUMBER);
        setAnswersRnadom = new ShuffleRandom(1,4);
        soundTrue  = new SoundPlay(this, R.raw.true_sound);
        soundFalse = new SoundPlay(this, R.raw.false_sound);


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
                    if (!prefecturs_Name.isEmpty() && !prefecturs_dummy.isEmpty()) {
                        setQuestion();
                        setAnswers();
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

    //CSV解析
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
        if (!prefecturs_dummy.isEmpty()) dummyRandom = new ShuffleRandom(1, prefecturs_dummy.size() - 1);
        Log.d("Parse", "Finished!");
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

    public void setQuestion() {

        int r = setQuestionRandom.getRandomInt();

        if (!prefecturs_Name.get(r).isEmpty()) {
        Iterator<String> iterator = prefecturs_Name.get(r).keySet().iterator();
            question_data = iterator.next();
            answer_data   = prefecturs_Name.get(r).get(question_data);
        question_View.setText(question_data);
        }
        answers.add(answer_data);
    }

    public void setAnswers() {
        for (int i = 0; i < 3; i++) {
            int r = dummyRandom.getRandomInt();
            answers.add(prefecturs_dummy.get(r));
        }
        Collections.shuffle(answers);
        btAnswer1.setText(answers.get(0));
        btAnswer2.setText(answers.get(1));
        btAnswer3.setText(answers.get(2));
        btAnswer4.setText(answers.get(3));

        answers = new ArrayList<>();
    }

    //判定
    public void judgeAnswer(TextView textView) {
        String userAnswer = textView.getText().toString();
        boolean isAnswer;
        if (answer_data.equals(userAnswer)) isAnswer = true;
        else                                isAnswer = false;

        if (textView == btAnswer1) setImage(TFImage1, isAnswer);
        if (textView == btAnswer2) setImage(TFImage2, isAnswer);
        if (textView == btAnswer3) setImage(TFImage3, isAnswer);
        if (textView == btAnswer4) setImage(TFImage4, isAnswer);

        isAnswered = !isAnswered;
   }

    public void setImage(final ImageView image, boolean tf) {
        image.setVisibility(View.VISIBLE);
        selectedImage = image;
        if (tf)  {
            soundTrue.play();
            image.setImageResource(R.drawable.true_img);
        }
        if (!tf) {
            soundFalse.play();
            image.setImageResource(R.drawable.false_img);
        }

        Handler  handler  = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                selectedImage.setVisibility(View.INVISIBLE);
                }
        };
        handler.postDelayed(runnable, 500);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            question_View   = (TextView)findViewById(R.id.prefecturesName);
            counter_View    = (TextView)findViewById(R.id.counter);
            counter_View.setText(String.format("第%02d問",nowQuestionNum));

            Button.OnClickListener onClickListener = new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    judgeAnswer((TextView) view);
                    nowQuestionNum++;
                    counter_View.setText(String.format("第%02d問", nowQuestionNum));
                    setQuestion();
                    setAnswers();
                }
            };

            btAnswer1 = (TextView) findViewById(R.id.btAnswer_1);
            btAnswer1.setOnClickListener(onClickListener);
            TFImage1  = (ImageView)findViewById(R.id.check_TF_1);
            btAnswer2 = (TextView) findViewById(R.id.btAnswer_2);
            btAnswer2.setOnClickListener(onClickListener);
            TFImage2  = (ImageView)findViewById(R.id.check_TF_2);
            btAnswer3 = (TextView) findViewById(R.id.btAnswer_3);
            btAnswer3.setOnClickListener(onClickListener);
            TFImage3  = (ImageView)findViewById(R.id.check_TF_3);
            btAnswer4 = (TextView) findViewById(R.id.btAnswer_4);
            btAnswer4.setOnClickListener(onClickListener);
            TFImage4  = (ImageView)findViewById(R.id.check_TF_4);

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
