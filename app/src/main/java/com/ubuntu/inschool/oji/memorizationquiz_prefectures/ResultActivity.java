package com.ubuntu.inschool.oji.memorizationquiz_prefectures;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private Intent              intent   = null;
    private Map<String, String> missMap  = null;
    private List<String>        keyArray = new ArrayList<>();

    private TextView tvAllQuestionsCount, tvMissQuestionsCount, tvAnswerRate;
    private Button   btBackHome;
    private ListView missList;
    private MapAdapter adapter;
    private int        missCount, allCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        missCount = 0;

        intent = getIntent();
        if (intent.getSerializableExtra(QuizActivity.INTENTKEY_MAP) != null) {
            missMap = (Map)intent.getSerializableExtra(QuizActivity.INTENTKEY_MAP);
            for (String key:missMap.keySet()) {
                keyArray.add(key);
            }
            allCount             = intent.getIntExtra(QuizActivity.INTENTKEY_NUM, 10);
            missCount            = missMap.size();
            missList             = (ListView)findViewById(R.id.missList);
            adapter              = new MapAdapter(missMap);
            missList.setAdapter(adapter);
        }

        float succeed = allCount - missCount;

        tvAllQuestionsCount  = (TextView)findViewById(R.id.allQuestionsCount);
        tvAllQuestionsCount.setText(allCount + "");
        tvMissQuestionsCount = (TextView)findViewById(R.id.missQuestionsCount);
        tvMissQuestionsCount.setText((int)succeed+ "");
        tvAnswerRate         = (TextView)findViewById(R.id.answerRate);
        float rate = (succeed / allCount) * 100 ;
        tvAnswerRate.setText((int)rate + "%");
        btBackHome           = (Button)findViewById(R.id.returnHome);
        btBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
