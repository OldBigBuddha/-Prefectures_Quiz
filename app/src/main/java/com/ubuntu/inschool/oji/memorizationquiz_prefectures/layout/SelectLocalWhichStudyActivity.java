package com.ubuntu.inschool.oji.memorizationquiz_prefectures.layout;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ubuntu.inschool.oji.memorizationquiz_prefectures.R;

import java.util.ArrayList;
import java.util.List;

public class SelectLocalWhichStudyActivity extends AppCompatActivity {

    private LayoutInflater inflater     = null;
    private LinearLayout   linear       = null;
    private LinearLayout   scrollLinear = null;
    private ScrollView     scroll       = null;
    private CardView       card         = null;
    private TextView       localName    = null;
    private ImageView      localImage   = null;

    private List<String>   localNameArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_local_which_study);

        linear = (LinearLayout)findViewById(R.id.cardLinear);
        scroll = (ScrollView)findViewById(R.id.scrollView);
        localNameArray.add("北海道・東北地方");
        localNameArray.add("関東地方");
        localNameArray.add("中部地方");
        localNameArray.add("近畿地方");
        localNameArray.add("中国地方");
        localNameArray.add("四国地方");
        localNameArray.add("九州・沖縄地方");

        for (int i = 0; i < localNameArray.size(); i++) {
            inflater        = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
            scrollLinear    = (LinearLayout)inflater.inflate(R.layout.card, null);
            card            = (CardView)scrollLinear.findViewById(R.id.cardView);
            localName       = (TextView)scrollLinear.findViewById(R.id.text);
            localName.setText(localNameArray.get(i));
            localImage      = (ImageView)scrollLinear.findViewById(R.id.image);
            localImage.setBackgroundColor(Color.parseColor("#0f0f0f"));
            card.setTag(i + 1);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(SelectLocalWhichStudyActivity.this, card.getTag() + "", Toast.LENGTH_SHORT).show();
                }
            });
            linear.addView(scrollLinear, i);
        }

    }
}
