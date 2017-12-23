package com.komarov.androidlab3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.komarov.androidlab3.R;
import com.komarov.androidlab3.domain.Category;

import java.util.List;

public class StatisticAdapter extends BaseAdapter {
    private List<Category> data;
    private int layResId;
    private LayoutInflater inflater;

    public StatisticAdapter(List<Category> data, Context ctx, int layResId) {
        this.data = data;
        this.layResId = layResId;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = inflater.inflate(layResId, viewGroup, false);
        Category currEntity = (Category) getItem(i);
        if (currEntity!=null) {
            TextView monthName = row.findViewById(R.id.category_statistic);
            monthName.setText(currEntity.getTitle());
        }
        return row;
    }

}
