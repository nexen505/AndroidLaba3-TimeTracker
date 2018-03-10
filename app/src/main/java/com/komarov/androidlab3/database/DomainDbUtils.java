package com.komarov.androidlab3.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.komarov.androidlab3.domain.Category;
import com.komarov.androidlab3.domain.Photo;
import com.komarov.androidlab3.domain.Record;
import com.komarov.androidlab3.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Ilia on 09.12.2017.
 */

public class DomainDbUtils extends DbUtils {

    public DomainDbUtils(Context context) {
        super(context);
    }

    public List<Record> getRecords(SQLiteDatabase database) {
        List<Record> res = new ArrayList<>();
        Record record;
        int i = 0;
        Cursor cursor = getAllFromTable(database, RECORD_TABLE);
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(RECORD_ID);
            int descriptionIndex = cursor.getColumnIndex(DESCRIPTION);
            int startDateIndex = cursor.getColumnIndex(START_TIME);
            int endDateIndex = cursor.getColumnIndex(END_TIME);
            int segmentIndex = cursor.getColumnIndex(TIME_SEGMENT);
            int categoryIndex = cursor.getColumnIndex(CATEGORY_ID_REF);
            do {
                Integer idRec = cursor.getInt(idIndex);
                String desc = cursor.getString(descriptionIndex);
                long begin = cursor.getLong(startDateIndex);
                long end = cursor.getLong(endDateIndex);
                long segment = cursor.getLong(segmentIndex);
                Integer idCat = cursor.getInt(categoryIndex);
                String titleCat = getCategoryTitleById(idCat, database);
                List<Photo> photoRecords = getPhotoListByRecordId(database, idRec);
                record = new Record(desc, segment, begin, end, idCat, titleCat, photoRecords);
                res.add(record);
                i++;
            } while (cursor.moveToNext());
            cursor.close();
        }
        return res;
    }

    private List<Photo> getPhotoListByRecordId(SQLiteDatabase database, int recordId) {
        List<Photo> photos = new LinkedList<>();
        Cursor cursor = database.query(TIME_PHOTO_TABLE, null, RECORD_ID_REF + "=" + String.valueOf(recordId - 1), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int idPhotoIdx = cursor.getColumnIndex(DbUtils.PHOTO_ID_REF);
            do {
                Integer idValProtoID = cursor.getInt(idPhotoIdx);
                photos.add(getPhotoById(database, idValProtoID));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return photos;
    }

    private Photo getPhotoById(SQLiteDatabase database, int id) {
        Photo photo = null;
        Cursor cursor = database.query(PHOTO_TABLE, null, PHOTO_ID + "=" + String.valueOf(id), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int idIdx = cursor.getColumnIndex(DbUtils.PHOTO_ID);
            int photoIdx = cursor.getColumnIndex(DbUtils.IMAGE);
            do {
                int idCat = cursor.getInt(idIdx);
                Bitmap bmp = Utils.getImage(cursor.getBlob(photoIdx));
                photo = new Photo(idCat, bmp);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return photo;
    }

    private Category getCategoryById(SQLiteDatabase database, int id) {
        Category category = null;
        Cursor cursor = database.query(CATEGORY_TABLE, null, CATEGORY_ID + "=" + String.valueOf(id), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            final List<Category> categories = parseCategories(cursor);
            category = categories.isEmpty() ? null : categories.get(0);
        }
        return category;
    }

    private Integer getNextRecordId(SQLiteDatabase database) {
        String sqlQuery = String.format("SELECT * FROM %s WHERE %s = (SELECT MAX(%s)  FROM %s)", RECORD_TABLE, RECORD_ID, RECORD_ID, RECORD_TABLE);
        Cursor cursor = database.rawQuery(sqlQuery, null);
        int idIdx;
        Integer idVal = 1;
        if (cursor != null && cursor.moveToFirst()) {
            idIdx = cursor.getColumnIndex(DbUtils.RECORD_ID);
            idVal = cursor.getInt(idIdx);
            cursor.close();
        }
        return idVal;
    }

    public void insertRecord(SQLiteDatabase database, Record record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DESCRIPTION, record.getDesc());
        contentValues.put(START_TIME, record.getBegin());
        contentValues.put(END_TIME, record.getEnd());
        contentValues.put(TIME_SEGMENT, record.getInterval());
        contentValues.put(CATEGORY_ID_REF, record.getCategoryRef());
        insert(database, contentValues, DbUtils.RECORD_TABLE);
        final ContentValues recordPhotos = new ContentValues();
        Integer id = getNextRecordId(database);
        recordPhotos.put(RECORD_ID_REF, id);
        record.getPhotos().forEach(p -> {
            recordPhotos.put(PHOTO_ID_REF, p.getId());
        });
        Category category = getCategoryById(database, record.getCategoryRef());
        if (category != null) {
            Photo photo = category.getPhoto();
            if (photo != null) {
                recordPhotos.put(PHOTO_ID_REF, photo.getId());
            }
        }
        insert(database, recordPhotos, DbUtils.TIME_PHOTO_TABLE);
    }

    public int updateRecord(int recordId, Record data, SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DESCRIPTION, data.getDesc());
        contentValues.put(START_TIME, data.getBegin());
        contentValues.put(END_TIME, data.getEnd());
        contentValues.put(TIME_SEGMENT, data.getInterval());
        contentValues.put(CATEGORY_ID_REF, data.getCategoryRef());
        int updCount = database.update(RECORD_TABLE, contentValues, RECORD_ID + " =?", new String[]{String.valueOf(recordId)});

        ContentValues cvR = new ContentValues();
        data.getPhotos().forEach(p -> cvR.put(PHOTO_ID_REF, p.getId()));
        database.update(TIME_PHOTO_TABLE, cvR, RECORD_ID_REF + " =?", new String[]{String.valueOf(data.getId())});
        return updCount;
    }

    public int deleteRecordById(SQLiteDatabase database, int recordId) {
        final String rsID = String.valueOf(recordId);
        delete(database, TIME_PHOTO_TABLE, RECORD_ID, new String[]{rsID});
        return delete(database, RECORD_TABLE, RECORD_ID, new String[]{rsID});
    }

    private String getCategoryTitleById(int id, SQLiteDatabase database) {
        Optional<Category> category = getAllCategories(database).stream().filter(c -> Objects.equals(c.getId(), id)).findAny();
        return category.map(Category::getTitle).orElse("");
    }

    public Record getRecordById(SQLiteDatabase database, int id) {
        Optional<Record> category = getRecords(database).stream().filter(r -> Objects.equals(r.getId(), id)).findAny();
        return category.orElse(null);
    }

    public List<Category> getAllCategories(SQLiteDatabase database) {
        Cursor cursor = database.query(CATEGORY_TABLE, null, null, null, null, null, null);
        return parseCategories(cursor);
    }

    public void insertCameraImage(SQLiteDatabase database, Bitmap bitmap) {
        byte[] image = Utils.getBytes(bitmap);
        ContentValues contentValues = new ContentValues();
        contentValues.put(IMAGE, image);
        insert(database, contentValues, PHOTO_TABLE);
    }

    public List<Photo> getAllPhoto(SQLiteDatabase database) {
        List<Photo> res = new ArrayList<>();
        Cursor cursor = getAllFromTable(database, PHOTO_TABLE);
        if (cursor != null && cursor.moveToFirst()) {
            int idIdx = cursor.getColumnIndex(PHOTO_ID);
            int imageIdx = cursor.getColumnIndex(IMAGE);
            do {
                int id = cursor.getInt(idIdx);
                final Bitmap bitmap = Utils.getImage(cursor.getBlob(imageIdx));
                final Photo photo = new Photo(id, bitmap);
                res.add(photo);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return res;
    }

    public List<Category> parseCategories(Cursor cursor) {
        List<Category> listCategories = new ArrayList<>();
        Category category;
        if (cursor != null && cursor.moveToFirst()) {
            int idId = cursor.getColumnIndex(DbUtils.CATEGORY_ID);
            int categoryId = cursor.getColumnIndex(DbUtils.CATEGORY_TITLE);
            int photoId = cursor.getColumnIndex(DbUtils.PHOTO_ID_REF);
            do {
                category = new Category(cursor.getInt(idId), cursor.getString(categoryId), getPhotoById(getReadableDatabase(), cursor.getInt(photoId)));
                listCategories.add(category);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return listCategories;
    }

    public Long getStatsTimeForCategory(SQLiteDatabase database, Category category) {
        String sql = String.format("select sum(%s) from %s where %s=?", TIME_SEGMENT, RECORD_TABLE, CATEGORY_ID_REF),
                str = "";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(category.getId())}, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                for (String cn : cursor.getColumnNames())
                    str = cursor.getString(cursor.getColumnIndex(cn));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return str != null ? Long.valueOf(str) : 0;
    }

    public Long getStatsTimeForCategory(SQLiteDatabase database, Category category, Long startDate, Long endDate) {
        String sql = String.format("select sum(%s) from %s where %s=? and %s>=? and %s<=?", TIME_SEGMENT, RECORD_TABLE, CATEGORY_ID_REF, START_TIME, END_TIME),
                str = "";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(category.getId()), String.valueOf(startDate), String.valueOf(endDate)}, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                for (String cn : cursor.getColumnNames())
                    str = cursor.getString(cursor.getColumnIndex(cn));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return str != null ? Long.valueOf(str) : 0;
    }

    public void deleteCascadeCategory(SQLiteDatabase database, Category category) {
        database.delete(CATEGORY_TABLE, String.format("%s=?", CATEGORY_TITLE), new String[]{category.getTitle()});
        String sqlQuery = String.format("select %s from %s where %s = ?", RECORD_ID, RECORD_TABLE, CATEGORY_ID_REF);
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{String.valueOf(category.getId())}, null);
        if (cursor != null && cursor.moveToFirst()) {
            final int columnIndex = cursor.getColumnIndex(RECORD_ID);
            do {
                deleteRecordById(database, cursor.getInt(columnIndex));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public int deleteCascadePhoto(SQLiteDatabase database, Photo photo) {
        delete(database, TIME_PHOTO_TABLE, PHOTO_ID_REF, new String[]{String.valueOf(photo.getId())});
        return delete(database, PHOTO_TABLE, DbUtils.PHOTO_ID, new String[]{String.valueOf(photo.getId())});
    }
}
