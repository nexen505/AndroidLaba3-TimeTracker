package com.komarov.androidlab3;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.komarov.androidlab3.adapter.CustomPhotoAdapter;
import com.komarov.androidlab3.database.DomainDbUtils;
import com.komarov.androidlab3.domain.Category;
import com.komarov.androidlab3.domain.Photo;

import java.util.ArrayList;
import java.util.List;

public class CategoryAddActivity extends AppCompatActivity {

    private EditText mTitle;

    private DomainDbUtils dbUtils;
    private SQLiteDatabase database;
    private List<Photo> selectedListPhotos = new ArrayList<>();
    private Photo selectedPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_add);

        mTitle = findViewById(R.id.title_category);

        dbUtils = new DomainDbUtils(this);
        database = dbUtils.getWritableDatabase();

        List<Photo> allPhoto = dbUtils.getAllPhoto(database);
        Spinner mPhotoSpinner = findViewById(R.id.photoSpinner);
        if (allPhoto.size()> 0) {
            CustomPhotoAdapter customPhotoAdapter = new CustomPhotoAdapter(CategoryAddActivity.this, R.layout.content_photo, allPhoto);
            mPhotoSpinner.setAdapter(customPhotoAdapter);
            mPhotoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedPhoto = (Photo) adapterView.getItemAtPosition(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            mPhotoSpinner.setVisibility(View.GONE);
        }

        Button mAddButton = findViewById(R.id.add_category_button);
        mAddButton.setOnClickListener(view -> {
            String title = mTitle.getText().toString();
            dbUtils.insertCategories(database, new Category(title, selectedPhoto));
            Intent intent = new Intent();
            intent.putExtra("cat", new Category(title, selectedPhoto));
            setResult(RESULT_OK, intent);
            finish();
            intent = new Intent(CategoryAddActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });


    }

}
