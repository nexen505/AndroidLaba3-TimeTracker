package com.komarov.androidlab3.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.komarov.androidlab3.MainActivity;
import com.komarov.androidlab3.R;
import com.komarov.androidlab3.RecordUpdateActivity;
import com.komarov.androidlab3.database.DomainDbUtils;
import com.komarov.androidlab3.domain.Category;
import com.komarov.androidlab3.domain.Photo;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories = new ArrayList<>();
    private Context context;
    private DomainDbUtils dbUtils;
    private SQLiteDatabase database;

    public CategoryAdapter(List<Category> categories, Context context) {
        this.categories = categories;
        this.context = context;
        dbUtils = new DomainDbUtils(context);
        database = dbUtils.getWritableDatabase();
    }

    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View mVewCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_category_row, parent, false);
        return new CategoryViewHolder(mVewCard);
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.CategoryViewHolder holder, final int position) {
        final Category category = categories.get(position);
        holder.mTitle.setText(category.getTitle());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, RecordUpdateActivity.class);
            intent.putExtra("categoryId", category.getId());
            intent.putExtra("title", category.getTitle());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        final Photo photo = category.getPhoto();
        if (photo != null)
            holder.mImageView.setImageBitmap(photo.getImage());

        holder.mDeleteButton.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
            alertDialogBuilder.setTitle("Вы уверены, что хотите удалить категорию?");
            alertDialogBuilder.setView(v);
            alertDialogBuilder.setCancelable(true)
                    .setPositiveButton("Да", (dialog, which) -> {
                        dbUtils.deleteCascadeCategory(database, category);
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        dialog.cancel();
                    })
                    .setNegativeButton("Нет", (dialog, id) -> {
                        dialog.cancel();
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        Button mDeleteButton;
        ImageView mImageView;

        CategoryViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.category_icon);
            mTitle = itemView.findViewById(R.id.title_row_category);
            mDeleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
