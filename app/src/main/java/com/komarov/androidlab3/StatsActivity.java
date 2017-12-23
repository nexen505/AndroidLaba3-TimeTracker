package com.komarov.androidlab3;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.komarov.androidlab3.database.DomainDbUtils;
import com.komarov.androidlab3.domain.Category;
import com.komarov.androidlab3.domain.Record;
import com.komarov.androidlab3.domain.StatsInfo;
import com.komarov.androidlab3.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class StatsActivity extends AppCompatActivity {

    private SQLiteDatabase database;
    private DomainDbUtils utils;

    private ListView mRecordsListView;
    private PieChart mPieChart;
    public static final String SELECTED_ITEMS_KEY = "SELECT_ITEM";

    private ArrayAdapter<String> adapter;
    private List<Category> allCategories = new ArrayList<>();

    private DateFormat mDayFormat = new SimpleDateFormat("dd"),
            mMonthFormat = new SimpleDateFormat("MM"),
            mYearFormat = new SimpleDateFormat("yyyy");

    private int mBeginDay, mBeginMonth, mBeginYear;
    private int mEndDay, mEndMonth, mEndYear;
    private boolean isMonthTimeFlag = false, isUserTimeFlag = false, isAllTimeFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        setTitle("Статистика");

        utils = new DomainDbUtils(this);
        database = utils.getWritableDatabase();

        mRecordsListView = findViewById(R.id.categoriesListView);

        allCategories = utils.getAllCategories(database);
        showCategories(allCategories);
        initializeAllStats(allCategories);
    }

    private void initializeAllStats(List<Category> categories) {
        showSummaryCategoriesTime(categories.parallelStream().map(Category::getTitle).collect(Collectors.toList()));
        viewOftenCategoriesList();
    }

    private void showSummaryCategoriesTime(List<String> categories) {
        List<String> result;
//        List<String> categories = (List<String>) getIntent().getSerializableExtra(StatsActivity.SELECTED_ITEMS_KEY);
        if (categories == null || categories.isEmpty()) {
            result = allCategories.stream()
                    .map(category -> String.format("%s: %s минут", category.getTitle(), String.valueOf(utils.getStatsTimeForCategory(database, category))))
                    .collect(Collectors.toList());
        } else {
            result = allCategories.parallelStream()
                    .flatMap(category ->
                            categories.parallelStream()
                                    .filter(s -> Objects.equals(s, category.getTitle()))
                                    .map(s -> String.format("%s: %s минут", category.getTitle(), String.valueOf(utils.getStatsTimeForCategory(database, category)))))
                    .collect(Collectors.toList());

        }
        ListView mList = findViewById(R.id.categoryTimeList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, result);
        mList.setAdapter(adapter);
    }

    public void showCategories(List<Category> categories) {
        if (categories == null || categories.isEmpty()) return;
        List<String> titles = categories.parallelStream().map(Category::getTitle).collect(Collectors.toList());
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, titles);
        mRecordsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mRecordsListView.setAdapter(adapter);
    }


    public void viewOftenCategoriesList() {
        List<Record> records = utils.getRecords(database);
        Map<String, Integer> mapCount = new HashMap<>();

        for (Record r : records) {
            int beginMonth = Integer.parseInt(mMonthFormat.format(new Date(r.getBegin())));
            int beginYear = Integer.parseInt(mYearFormat.format(new Date(r.getBegin())));

            int endMonth = Integer.parseInt(mMonthFormat.format(new Date(r.getEnd())));
            int endYear = Integer.parseInt(mYearFormat.format(new Date(r.getEnd())));

            if (isMonthTimeFlag &&
                    beginMonth == mBeginMonth && mBeginYear == beginYear &&
                    mEndMonth == endMonth && mEndYear == endYear) {
                mapCount.merge(r.getCategoryTitle(), 1, (a, b) -> a + b);
            }

            if (isUserTimeFlag &&
                    mBeginMonth <= beginMonth && mBeginYear <= beginYear &&
                    endMonth <= mEndMonth && endYear <= mEndYear) {
                mapCount.merge(r.getCategoryTitle(), 1, (a, b) -> a + b);
            }

            if (isAllTimeFlag) {
                mapCount.merge(r.getCategoryTitle(), 1, (a, b) -> a + b);
            }

        }

        mapCount = Utils.sortByValue(mapCount);
        List<String> titles = mapCount.entrySet().parallelStream()
                .map(stringIntegerEntry -> String.format("%s: %s", stringIntegerEntry.getKey(), String.valueOf(stringIntegerEntry.getValue())))
                .collect(Collectors.toList());

        ListView mList = findViewById(R.id.categoryOftenList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        mList.setAdapter(adapter);
    }

    private void drawPie(List<Category> allCategories) {
        Random random = new Random();
        List<StatsInfo> times = allCategories.parallelStream()
                .map(category -> new StatsInfo(category.getTitle(), utils.getStatsTimeForCategory(database, category)))
                .collect(Collectors.toList());
        times.parallelStream()
                .filter(statsInfo -> statsInfo.getTime() != 0)
                .map(statsInfo -> new Segment(statsInfo.getCategory(), statsInfo.getTime()))
                .forEach(segment -> {
                    mPieChart.addSeries(segment, new SegmentFormatter(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)), Color.BLACK, Color.BLACK, Color.BLACK));
                });
        PieRenderer pieRenderer = mPieChart.getRenderer(PieRenderer.class);
        pieRenderer.setDonutSize((float) 0 / 100, PieRenderer.DonutMode.PERCENT);
    }

    public void showStatisticRecord() {
        SparseBooleanArray mCheckedPosition = mRecordsListView.getCheckedItemPositions();
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < mCheckedPosition.size(); i++) {
            int position = mCheckedPosition.keyAt(i);
            if (mCheckedPosition.valueAt(i))
                selected.add(adapter.getItem(position));
        }
        showSummaryCategoriesTime(selected);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.statistic_settings) {
            showStatisticRecord();
        }
        if (id == R.id.month_time_settings) {
            isMonthTimeFlag = true;
            isUserTimeFlag = false;
            isAllTimeFlag = false;
            mBeginMonth = mEndMonth = Integer.parseInt(mMonthFormat.format(new Date()));
            mBeginYear = mEndYear = Integer.parseInt(mYearFormat.format(new Date()));
            viewOftenCategoriesList();
        }
        if (id == R.id.all_time_settings) {
            isMonthTimeFlag = false;
            isUserTimeFlag = false;
            isAllTimeFlag = true;
            viewOftenCategoriesList();
        }
        if (id == R.id.user_time_settings) {
            isMonthTimeFlag = false;
            isUserTimeFlag = true;
            isAllTimeFlag = false;
            onClickDatePicker();
        }
        if (id == R.id.pie_stats) {
            onClickShowPie();
        }

        return super.onOptionsItemSelected(item);
    }

    private void onClickShowPie() {
        LayoutInflater layoutInflater = LayoutInflater.from(StatsActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.pie_layout, null);
        mPieChart = promptView.findViewById(R.id.pie);
        mPieChart.getBackgroundPaint().setColor(Color.WHITE);
        drawPie(allCategories);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StatsActivity.this);
        alertDialogBuilder.setTitle(R.string.pie_title);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false)
                .setNegativeButton("Ok",
                        (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void onClickDatePicker() {

        LayoutInflater layoutInflater = LayoutInflater.from(StatsActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.content_add_time_statistic, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StatsActivity.this);
        alertDialogBuilder.setTitle("Выберите период");
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Ok",
                        (dialog, id) -> {
                            try {
                                EditText mBegin = promptView.findViewById(R.id.edit_begin);
                                EditText mEnd = promptView.findViewById(R.id.edit_end);

                                String begin = mBegin.getText().toString();
                                String end = mEnd.getText().toString();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

                                long time = simpleDateFormat.parse(begin).getTime();

                                mBeginDay = Integer.parseInt(mDayFormat.format(new Date(time)));
                                mBeginMonth = Integer.parseInt(mMonthFormat.format(new Date(time)));
                                mBeginYear = Integer.parseInt(mYearFormat.format(new Date(time)));

                                time = simpleDateFormat.parse(end).getTime();
                                mEndDay = Integer.parseInt(mDayFormat.format(new Date(time)));
                                mEndMonth = Integer.parseInt(mMonthFormat.format(new Date(time)));
                                mEndYear = Integer.parseInt(mYearFormat.format(new Date(time)));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            viewOftenCategoriesList();
                            dialog.cancel();
                        })
                .setNegativeButton("Cancel",
                        (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
