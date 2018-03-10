package com.komarov.androidlab3.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.komarov.androidlab3.R;
import com.komarov.androidlab3.domain.Photo;

import java.util.List;

public class CustomPhotoAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Photo> data;
    private int resource;

    public CustomPhotoAdapter(Context context, int resource, List<Photo> objects) {
        this.resource = resource;
        this.data = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Nullable
    @Override
    public Photo getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(resource, parent, false);
        Photo photo = getItem(position);
        if (photo != null) {
            ImageView container = row.findViewById(R.id.bmp);
            container.setImageBitmap(photo.getImage());
            TextView textContainer = row.findViewById(R.id.name);
            textContainer.setText(photo.getName());
        }
        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return data.size();
    }
}
