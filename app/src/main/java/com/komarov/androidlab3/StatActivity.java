package com.komarov.androidlab3;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.komarov.androidlab3.database.DomainDbUtils;
import com.komarov.androidlab3.domain.Category;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);


        DomainDbUtils utils = new DomainDbUtils(this);
        SQLiteDatabase database = utils.getWritableDatabase();


    }

}
