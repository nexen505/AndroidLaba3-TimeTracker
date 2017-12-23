package com.komarov.androidlab3;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.komarov.androidlab3.adapter.CategoryAdapter;
import com.komarov.androidlab3.database.DbUtils;
import com.komarov.androidlab3.database.DomainDbUtils;
import com.komarov.androidlab3.domain.Category;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.categories));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((view) -> {
            Intent intent = new Intent(MainActivity.this, CategoryAddActivity.class);
            startActivity(intent);
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RecyclerView mCategories = findViewById(R.id.category_list);
        mCategories.setHasFixedSize(true);
        mCategories.setLayoutManager(new LinearLayoutManager(this));

        DomainDbUtils dbUtils = new DomainDbUtils(this);
        SQLiteDatabase database = dbUtils.getWritableDatabase();
        List<Category> listCategories = dbUtils.parseCategories(dbUtils.getAllFromTable(database, DbUtils.CATEGORY_TABLE));
        CategoryAdapter mAdapter = new CategoryAdapter(listCategories, MainActivity.this);
        mCategories.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.restart) {
            finish();
            startActivity(new Intent(MainActivity.this, getClass()));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_records) {
            Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_make_photo) {
            Intent intent = new Intent(MainActivity.this, PhotoAddActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_statistics) {
            Intent intent = new Intent(MainActivity.this, StatsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle("Вы уверены, что хотите очистить базу данных?");
            alertDialogBuilder.setView(item.getActionView());
            alertDialogBuilder.setCancelable(true)
                    .setPositiveButton("Ок", (dialog, which) -> {
                        this.deleteDatabase(DbUtils.DATABASE_NAME);
                        finish();
                        startActivity(new Intent(MainActivity.this, getClass()));
                        dialog.cancel();
                    })
                    .setNegativeButton("Отмена", (dialog, which) -> {
                        dialog.cancel();
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
