package com.komarov.androidlab3;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.komarov.androidlab3.adapter.RecordAdapter;
import com.komarov.androidlab3.database.DomainDbUtils;
import com.komarov.androidlab3.domain.Record;

import java.util.ArrayList;
import java.util.List;

public class RecordsActivity extends AppCompatActivity {

    private ListView mListRecord;
    private SQLiteDatabase database;
    private DomainDbUtils utils;
    private List<Record> allRecords = new ArrayList<>();
    private RecordAdapter adapter;
    private View selectedView;
    private Record selectedRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        setTitle("Отметки времени");

        mListRecord = findViewById(R.id.record_list_view);
        utils = new DomainDbUtils(this);
        database = utils.getWritableDatabase();
        allRecords = utils.getRecords(database);
        adapter = new RecordAdapter(this, R.layout.content_record_row, allRecords);
        mListRecord.setAdapter(adapter);
        mListRecord.setOnItemClickListener((adapterView, view, i, l) -> {
            if (selectedView != null)
                selectedView.setBackgroundColor(0xffffff);
            selectedRecord = (Record) adapterView.getItemAtPosition(i);
            selectedView = view;
            selectedView.setBackgroundColor(0x87cefa);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.records_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.record_del) {
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle(R.string.confirm_deletion);
            ad.setMessage(R.string.confirm_deletion_msg);
            ad.setPositiveButton(R.string.button_ok, (dialog, arg1) -> {
                utils.deleteRecordById(database, selectedRecord.getId());
                allRecords.remove(selectedRecord);
                adapter.notifyDataSetChanged();
            });
            ad.setNegativeButton(R.string.button_cancel, (dialog, arg1) -> {
            });
            ad.show();
        } else if (id == R.id.record_update) {
            if (selectedRecord != null) {
                Intent intent = new Intent(RecordsActivity.this, RecordUpdateActivity.class);
                intent.putExtra("recordId", String.valueOf(selectedRecord.getId()));
                startActivity(intent);
                finish();
                adapter.notifyDataSetChanged();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
