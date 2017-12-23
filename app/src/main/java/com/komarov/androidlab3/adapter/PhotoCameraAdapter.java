package com.komarov.androidlab3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.komarov.androidlab3.R;
import com.komarov.androidlab3.domain.Photo;

import java.util.List;

public class PhotoCameraAdapter extends BaseAdapter {

    private List<Photo> data;
    private int layResId;
    private LayoutInflater inflater;

    public PhotoCameraAdapter() {
    }

    public PhotoCameraAdapter(Context context, int resource, List<Photo> objects) {
        this.layResId = resource;
        this.data = objects;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View row = this.inflater.inflate(layResId, viewGroup, false);
        Photo currMeet = (Photo) getItem(i);
        if (currMeet!=null) {
            ImageView meetName = row.findViewById(R.id.bmp);
            meetName.setImageBitmap(currMeet.getImage());
        }
        return row;
    }

}
