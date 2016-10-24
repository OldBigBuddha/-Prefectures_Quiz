package com.ubuntu.inschool.oji.memorizationquiz_prefectures.layout;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ubuntu.inschool.oji.memorizationquiz_prefectures.R;

public class MainActivity extends AppCompatActivity {

    private LinearLayout    linearLayout = null;
    private Button          btStartQuiz  = null;
    private Button          btStartStudy = null;

    private Intent  intent   = null;
    private int     count    = 0;
    private boolean isCancel = false;

    public static final String  INTENTKEY           = "question";
    public static final String  BR                  = System.getProperty("line.separator");
    public static final int     PREFECTURSNUMBER    = 47;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        btStartQuiz  = (Button)findViewById(R.id.quizButton);
        btStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent    = new Intent(MainActivity.this, QuizActivity.class);
                final CharSequence[] ITEMS  = {"10問","20問","30問","40問","47問"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("何問解きますか？");
                builder.setItems(ITEMS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        switch (which) {
                            case 0:
                                count = 10;
                                break;
                            case 1:
                                count = 20;
                                break;
                            case 2:
                                count = 30;
                                break;
                            case 3:
                                count = 40;
                                break;
                            case 4:
                                count = 47;
                                break;
                        }
                        intent.putExtra(INTENTKEY, count);
                        startActivity(intent);
                    }

                });
                builder.setNegativeButton("やっぱやめる", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //キャンセル
                    }
                });
                builder.create().show();
            }
        });
        btStartStudy = (Button)findViewById(R.id.studyButton);
        btStartStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(linearLayout, "ごめんなさい！、未実装です\nしばらく待っててね！", Snackbar.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, SelectLocalWhichStudyActivity.class);
//                startActivity(intent);
            }
        });

    }
}
