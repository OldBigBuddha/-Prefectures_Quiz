package com.ubuntu.inschool.oji.memorizationquiz_prefectures;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by oji on 16/10/22.
 */
public class MapAdapter extends BaseAdapter {

    private final List list = new ArrayList<>();

    public MapAdapter(Map<String, String> map) {
        list.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int i) {
        return (Map.Entry) list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int postion, View view, ViewGroup parent) {

        final View result;

        if (view == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw, parent, false);
        } else {
            result = view;
        }

        Map.Entry<String, String> item = getItem(postion);

        ((TextView) result.findViewById(R.id.prefectureName)).setText(item.getKey());
        ((TextView) result.findViewById(R.id.cityName)).setText(item.getValue());

        return result;
    }
}
