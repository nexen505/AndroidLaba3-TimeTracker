package com.komarov.androidlab3;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.komarov.androidlab3.database.DbUtils;
import com.komarov.androidlab3.database.DomainDbUtils;
import com.komarov.androidlab3.domain.Category;

public class CategoryAddActivity extends AppCompatActivity {

    private EditText mTitle;

    private DomainDbUtils utils;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_add);

        mTitle = findViewById(R.id.title_category);

        utils = new DomainDbUtils(this);
        database = utils.getWritableDatabase();

        Button mAddButton = findViewById(R.id.add_category_button);
        mAddButton.setOnClickListener(view -> {
            String title = mTitle.getText().toString();
            utils.insertCategories(database, new Category(title));
            Intent intent = new Intent();
            intent.putExtra("cat", new Category(title));
            setResult(RESULT_OK, intent);
            finish();
            intent = new Intent(CategoryAddActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });


    }

}
