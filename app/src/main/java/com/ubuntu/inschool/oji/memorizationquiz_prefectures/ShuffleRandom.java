package com.ubuntu.inschool.oji.memorizationquiz_prefectures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by oji on 16/10/20.
 * ランダムな数をIntgerで返すクラス
 */
public class ShuffleRandom {

    private List<Integer> randomList = new ArrayList<>();
    private int min, max;   //下限・上限
    private int count;      //何回返したかを計測

    public ShuffleRandom(int min, int max) {
        this.min    = min;
        this.max    = max;
        this.count  = 0;
        initRandom();
    }

    public Integer getRandomInt() {
        count++;
        if (count > max) return null;
        return randomList.get(count - 1);
    }

    private void initRandom() {
        for (int i = min; i < max; i++) {
            randomList.add(i);
        }
        Collections.shuffle(randomList);
    }
}
