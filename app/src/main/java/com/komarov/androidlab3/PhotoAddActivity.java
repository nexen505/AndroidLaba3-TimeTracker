package com.komarov.androidlab3;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.komarov.androidlab3.adapter.PhotoCameraAdapter;
import com.komarov.androidlab3.database.DomainDbUtils;
import com.komarov.androidlab3.domain.Photo;

import java.util.ArrayList;
import java.util.List;

public class PhotoAddActivity extends AppCompatActivity {

    private final int CAMERA_RESULT_ADD = 0;
    private List<Photo> allPhoto = new ArrayList<>();
    private PhotoCameraAdapter adapter;
    private SQLiteDatabase database;
    private DomainDbUtils dbUtils;
    private Photo p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_add);

        ListView mListPhoto = findViewById(R.id.list_photo);

        dbUtils = new DomainDbUtils(this);
        database = dbUtils.getWritableDatabase();

        allPhoto = dbUtils.getAllPhoto(database);
        adapter = new PhotoCameraAdapter(this, R.layout.content_photo, allPhoto);
        mListPhoto.setAdapter(adapter);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_RESULT_ADD);

        mListPhoto.setOnItemClickListener((adapterView, view, i, l) -> p = (Photo) adapterView.getItemAtPosition(i));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_RESULT_ADD) {
            try {
                Bitmap thumbnailBitmap = (Bitmap) data.getExtras().get("data");
                dbUtils.insertCameraImage(database, thumbnailBitmap);
                allPhoto.add(new Photo(thumbnailBitmap));
                adapter.notifyDataSetChanged();
            } catch (NullPointerException e) {
                Toast toast = Toast.makeText(this, "Добавление отменено", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.photo_del) {
            dbUtils.deleteCascadePhoto(database, p);
            allPhoto.remove(p);
            adapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }
}
