package com.komarov.androidlab3;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.komarov.androidlab3.adapter.CustomPhotoAdapter;
import com.komarov.androidlab3.database.DomainDbUtils;
import com.komarov.androidlab3.domain.Photo;
import com.komarov.androidlab3.domain.Record;
import com.komarov.androidlab3.utils.StringDateTime;
import com.komarov.androidlab3.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecordUpdateActivity extends AppCompatActivity {

    public static String DESC = "";

    private EditText mDesc;
    private TimePicker mTimePicker;

    private DatePicker startDatePicker, endDatePicker;
    private TimePicker startTimePicker, endTimePicker;
    private int startYear, startMonth, startDay, startHours, startMinutes;
    private int endYear, endMonth, endDay, endHours, endMinutes;
    private TextView mStartDateTextView, mStartTimeTextView, mEndDateTextView, mEndTimeTextView,
            mTitle;
    private DomainDbUtils dbUtils;
    private SQLiteDatabase database;

    private List<Photo> allPhoto = new ArrayList<>(), selectedListPhotos = new ArrayList<>();
    private Photo selectedPhoto;
    private Record record;
    private Integer recordId;
    private Integer categoryId;

    private long begin;
    private long end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_update);

        dbUtils = new DomainDbUtils(this);
        database = dbUtils.getWritableDatabase();
        mStartDateTextView = findViewById(R.id.startDate);
        mStartTimeTextView = findViewById(R.id.startTime);
        mEndDateTextView = findViewById(R.id.endDate);
        mEndTimeTextView = findViewById(R.id.endTime);
        mDesc = findViewById(R.id.add_desc);
        mTitle = findViewById(R.id.cat_title);

        String recordIdString = getIntent().getStringExtra("recordId");
        if (recordIdString != null) {
            recordId = Integer.valueOf(recordIdString);
            record = dbUtils.getRecordById(database, recordId);
            mTitle.setText(record.getCategoryTitle());
            mDesc.setText(record.getDesc());
        } else {
            recordId = null;
            record = null;
            mTitle.setText(getIntent().getStringExtra("title"));
            categoryId = getIntent().getIntExtra("categoryId", 0);
        }

        Button mAddButton = findViewById(R.id.add_record_button);
        mAddButton.setOnClickListener(view -> {
            begin = parseTime(mStartDateTextView.getText().toString(), mStartTimeTextView.getText().toString());
            end = parseTime(mEndDateTextView.getText().toString(), mEndTimeTextView.getText().toString());
            long interval = getInterval(begin, end);
            String desc = mDesc.getText().toString();
            if (record != null) {
                record.setDesc(desc);
                record.setBegin(begin);
                record.setEnd(end);
                record.setPhotos(allPhoto);
                record.setInterval(interval);
                updateRecord(record);
            } else {
                final Record record = new Record(selectedListPhotos, desc, interval, begin, end, categoryId);
                addRecord(record);
            }
        });

        allPhoto = dbUtils.getAllPhoto(database);
        Spinner mPhotoSpinner = findViewById(R.id.photoSpinner);
        if (allPhoto.size() > 0) {
            CustomPhotoAdapter customPhotoAdapter = new CustomPhotoAdapter(RecordUpdateActivity.this, R.layout.content_photo, allPhoto);
            mPhotoSpinner.setAdapter(customPhotoAdapter);
            mPhotoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedPhoto = (Photo) adapterView.getItemAtPosition(i);
                    selectedListPhotos.add(selectedPhoto);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            mPhotoSpinner.setVisibility(View.GONE);
        }

        if (record != null) {
            StringDateTime dt = new StringDateTime(record.getBegin());
            String d = dt.getDate(),
                    t = dt.getTime();
            String[] parts = d.split(StringDateTime.DATE_DELIMITER);
            setDateToTextView(mStartDateTextView, parts[0], parts[1], parts[2]);
            parts = t.split(StringDateTime.TIME_DELIMITER);
            setTimeToTextView(mStartTimeTextView, parts[0], parts[1]);

            dt = new StringDateTime(record.getEnd());
            d = dt.getDate();
            t = dt.getTime();
            parts = d.split(StringDateTime.DATE_DELIMITER);
            setDateToTextView(mEndDateTextView, parts[0], parts[1], parts[2]);
            parts = t.split(StringDateTime.TIME_DELIMITER);
            setTimeToTextView(mEndTimeTextView, parts[0], parts[1]);
        } else {
            final Calendar c = Calendar.getInstance();

            startYear = c.get(Calendar.YEAR);
            startMonth = c.get(Calendar.MONTH) + 1;
            startDay = c.get(Calendar.DAY_OF_MONTH);
            startHours = c.get(Calendar.HOUR_OF_DAY);
            startMinutes = c.get(Calendar.MINUTE);

            endYear = c.get(Calendar.YEAR);
            endMonth = c.get(Calendar.MONTH) + 1;
            endDay = c.get(Calendar.DAY_OF_MONTH);
            endHours = c.get(Calendar.HOUR_OF_DAY);
            endMinutes = c.get(Calendar.MINUTE);

            setDateToTextView(mStartDateTextView, String.valueOf(startDay), String.valueOf(startMonth), String.valueOf(startYear));
            setTimeToTextView(mStartTimeTextView, String.valueOf(startHours), String.valueOf(startMinutes));
            setDateToTextView(mEndDateTextView, String.valueOf(endDay), String.valueOf(endMonth), String.valueOf(endYear));
            setTimeToTextView(mEndTimeTextView, String.valueOf(endHours), String.valueOf(endMinutes));
        }
    }

    public void updateRecord(Record record) {
        dbUtils.updateRecord(recordId, record, database);

        Intent intent = new Intent(RecordUpdateActivity.this, RecordsActivity.class);
        startActivity(intent);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void addRecord(Record record) {
        dbUtils.insertRecord(database, record);
        Intent intent = new Intent(RecordUpdateActivity.this, RecordsActivity.class);
        startActivity(intent);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onChooseStartDate(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.date_dialog, null);
        startDatePicker = promptView.findViewById(R.id.datePicker);
        String startDate = ((TextView) view).getText().toString();
        String[] parts = startDate.split(StringDateTime.DATE_DELIMITER);
        startYear = Integer.parseInt(parts[2]);
        startMonth = Integer.parseInt(parts[1]);
        startDay = Integer.parseInt(parts[0]);
        openDateDialog(startDay, startMonth, startYear, promptView, startDatePicker, mStartDateTextView);
    }

    public void onChooseEndDate(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.date_dialog, null);
        endDatePicker = promptView.findViewById(R.id.datePicker);
        String endDate = ((TextView) view).getText().toString();
        String[] parts = endDate.split(StringDateTime.DATE_DELIMITER);
        endYear = Integer.parseInt(parts[2]);
        endMonth = Integer.parseInt(parts[1]);
        endDay = Integer.parseInt(parts[0]);
        openDateDialog(endDay, endMonth, endYear, promptView, endDatePicker, mEndDateTextView);
    }

    public void openDateDialog(final int d, final int m, final int y,
                               final View promptView, final DatePicker datePicker, final TextView dateTextView) {
        datePicker.init(y, m - 1, d, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton(R.string.button_ok, (dialog, id) -> {
                    String day1 = String.valueOf(datePicker.getDayOfMonth());
                    String month1 = String.valueOf(datePicker.getMonth() + 1);
                    String year1 = String.valueOf(datePicker.getYear());
                    setDateToTextView(dateTextView, day1, month1, year1);
                    dialog.cancel();
                })
                .setNegativeButton(R.string.button_cancel, (dialog, id) -> dialog.cancel());
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void setDateToTextView(final TextView textView, final String day, final String month, final String year) {
        final String date = Utils.join(StringDateTime.DATE_DELIMITER, day, month, year);
        textView.setText(date);
    }

    public void onChooseStartTime(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.time_dialog, null);
        startTimePicker = promptView.findViewById(R.id.timePicker);
        String startTime = ((TextView) view).getText().toString();
        String[] parts = startTime.split(StringDateTime.TIME_DELIMITER);
        startHours = Integer.parseInt(parts[0]);
        startMinutes = Integer.parseInt(parts[1]);
        openTimeDialog(startHours, startMinutes, promptView, startTimePicker, mStartTimeTextView);
    }

    public void onChooseEndTime(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.time_dialog, null);
        endTimePicker = promptView.findViewById(R.id.timePicker);
        String endTime = ((TextView) view).getText().toString();
        String[] parts = endTime.split(StringDateTime.TIME_DELIMITER);
        endHours = Integer.parseInt(parts[0]);
        endMinutes = Integer.parseInt(parts[1]);
        openTimeDialog(endHours, endMinutes, promptView, endTimePicker, mEndTimeTextView);
    }

    public void openTimeDialog(final int h, final int m,
                               final View promptView, final TimePicker timePicker, final TextView timeTextView) {
        timePicker.setHour(h);
        timePicker.setMinute(m);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton(R.string.button_ok, (dialog, id) -> {
                    String hour = String.valueOf(timePicker.getHour());
                    String minute = String.valueOf(timePicker.getMinute());
                    setTimeToTextView(timeTextView, hour, minute.length() < 2 ? "0" + minute : minute);
                    dialog.cancel();
                });
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void setTimeToTextView(final TextView textView, final String hours, final String minutes) {
        final String time = Utils.join(
                StringDateTime.TIME_DELIMITER,
                hours.length() < 2 ? "0" + hours : hours,
                minutes.length() < 2 ? "0" + minutes : minutes);
        textView.setText(time);
    }

    private static long parseTime(String date, String time) {
        try {
            final String dateTime = String.format("%s %s", date, time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(StringDateTime.DATE_TIME_PATTERN);
            return simpleDateFormat.parse(dateTime).getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public long getInterval(long begin, long end) {
        long diff = end - begin;
        if (diff < 0) {
            Toast toast = Toast.makeText(this, R.string.incorrect_time, Toast.LENGTH_LONG);
            toast.show();
            return 0;
        }
        return diff;

    }
}
