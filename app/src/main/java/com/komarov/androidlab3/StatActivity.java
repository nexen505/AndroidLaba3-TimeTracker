package com.komarov.androidlab3;

        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;

        import com.komarov.androidlab3.database.DomainDbUtils;
        import com.komarov.androidlab3.domain.Category;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Objects;
        import java.util.stream.Collectors;

public class StatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        ListView mList = findViewById(R.id.statList);

        DomainDbUtils utils = new DomainDbUtils(this);
        SQLiteDatabase database = utils.getWritableDatabase();

        List<Category> allCategories = utils.getAllCategories(database);

        List<String> result;
        List<String> selectedItems = (List<String>) getIntent().getSerializableExtra(StatsActivity.SELECTED_ITEMS_KEY);
        if (selectedItems.size() == 0) {
            result = allCategories.stream().map(category -> category.getTitle() + ": " + String.valueOf(utils.getStatsTimeForCategory(database, category))).collect(Collectors.toList());
        } else {
            result = allCategories.parallelStream()
                    .flatMap(category ->
                            selectedItems.parallelStream()
                                    .filter(s -> Objects.equals(s, category.getTitle()))
                                    .map(s -> category.getTitle() + ": " + String.valueOf(utils.getStatsTimeForCategory(database, category))))
                    .collect(Collectors.toList());

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, result);
        mList.setAdapter(adapter);
    }

}
