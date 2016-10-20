package com.ubuntu.inschool.oji.memorizationquiz_prefectures;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;

public class MainActivity extends AppCompatActivity {

    private Button btOne    = null;
    private Button btTwo    = null;
    private Button btThree  = null;
    private Button btAll    = null;

    private Intent intent   = null;

    public static final String  INTENTKEY           = "question";
    public static final String  BR                  = System.getProperty("line.separator");
    public static final int     PREFECTURSNUMBER    = 47;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btOne   = (Button)findViewById(R.id.oneButton);
        btOne.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra(INTENTKEY, 10);
                startActivity(intent);
            }
        });
        btTwo   = (Button)findViewById(R.id.twoButton);
        btTwo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra(INTENTKEY, 20);
                startActivity(intent);
            }
        });

        btThree = (Button)findViewById(R.id.threeButton);
        btThree.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra(INTENTKEY, 30);
                startActivity(intent);
            }
        });

        btAll   = (Button)findViewById(R.id.allButton);
        btAll.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra(INTENTKEY, MainActivity.PREFECTURSNUMBER);
                startActivity(intent);
            }
        });
    }
}
