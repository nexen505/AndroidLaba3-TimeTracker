package com.komarov.androidlab3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.komarov.androidlab3.R;
import com.komarov.androidlab3.domain.Photo;
import com.komarov.androidlab3.domain.Record;
import com.komarov.androidlab3.utils.StringDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RecordAdapter extends BaseAdapter {

    private static final String LOG_TAG = "TimeRecordAdapter";
    private List<Record> data;
    private LayoutInflater inflater;
    private Context ctx;
    private int resource;
    private DateFormat date;

    public RecordAdapter(Context context, int resource, List<Record> objects) {
        this.ctx = context;
        this.resource = resource;
        this.data = objects;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.date = new SimpleDateFormat(StringDateTime.DATE_TIME_PATTERN);
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
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = inflater.inflate(resource, viewGroup, false);
        Record record = (Record) getItem(i);

        TextView recordDesc = row.findViewById(R.id.record_row_title);
        recordDesc.setText(String.format("%s%s", recordDesc.getText(), record.getDesc()));

        TextView startDate = row.findViewById(R.id.record_row_start);
        Date date = new Date(record.getBegin());
        startDate.setText(String.format("%s%s", startDate.getText(), this.date.format(date)));

        TextView recordCategory = row.findViewById(R.id.record_row_category);
        recordCategory.setText(String.format("%s%s", recordCategory.getText(), record.getCategoryTitle()));

        TextView endTime = row.findViewById(R.id.record_row_end);
        date = new Date(record.getEnd());
        endTime.setText(String.format("%s%s", endTime.getText(), this.date.format(date)));

        TextView segment = row.findViewById(R.id.record_row_interval);
        segment.setText(String.format("%s%s минут", segment.getText(), String.valueOf((int) (record.getInterval() / 1000 / 60))));

        LinearLayout linearLayout = row.findViewById(R.id.photos_layout);
        final List<Photo> photos = record.getPhotos().stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (!photos.isEmpty()) {
            photos.forEach(photo -> {
                ImageView imageView = new ImageView(ctx);
                imageView.setImageBitmap(photo.getImage());
                imageView.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                );
                linearLayout.addView(imageView);
            });
        } else {
            linearLayout.setVisibility(View.GONE);
            TextView photo = row.findViewById(R.id.photo);
            photo.setVisibility(View.GONE);
        }
        return row;
    }

}
