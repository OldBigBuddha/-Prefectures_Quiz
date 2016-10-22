package com.ubuntu.inschool.oji.memorizationquiz_prefectures;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.Collections;

/**
 * Created by oji on 16/10/22.
 */
public class SoundPlay {

    private SoundPool soundPool = null;
    private int       soundId;

    public SoundPlay(Context context, final int resourceId) {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundId   = soundPool.load(context, resourceId, 1);
    }

    public void play() {
        soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
    }
}
