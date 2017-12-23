package com.komarov.androidlab3.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.komarov.androidlab3.domain.Category;

public class DbUtils extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String LOG_TAG = "DbUtils";

    public static final String DATABASE_NAME = "timeTracker";
    public static final String CATEGORY_TABLE = "category";
    static final String PHOTO_TABLE = "photos";
    static final String RECORD_TABLE = "records";
    static final String TIME_PHOTO_TABLE = "photo_records";

    static final String CATEGORY_ID = "_id";
    static final String CATEGORY_TITLE = "title";

    static final String RECORD_ID_REF = "recordId";
    static final String PHOTO_ID_REF = "photoId";

    static final String PHOTO_ID = "_id";
    static final String IMAGE = "image";

    static final String RECORD_ID = "_id";
    static final String CATEGORY_ID_REF = "categoryId";
    static final String DESCRIPTION = "description";
    static final String START_TIME = "startTime";
    static final String END_TIME = "endTime";
    static final String TIME_SEGMENT = "segment";

    private static final String CREATE_CATEGORY_QUERY = String.format("CREATE TABLE %s (" +
            "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
            "%s INTEGER," +
            "%s TEXT," +
            "FOREIGN KEY(%s) REFERENCES %s(%s) ON UPDATE CASCADE);",
            CATEGORY_TABLE,
            CATEGORY_ID, PHOTO_ID_REF, CATEGORY_TITLE,
            PHOTO_ID_REF, PHOTO_TABLE, PHOTO_ID);
    private static final String CREATE_PHOTO_QUERY = String.format("CREATE TABLE %s (" +
            "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
            "%s BLOB);",
            PHOTO_TABLE,
            PHOTO_ID, IMAGE);
    private static final String CREATE_RECORD_QUERY = String.format("CREATE TABLE %s (" +
            "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
            "%s INTEGER," +
            "%s TEXT," +
            "%s NUMERIC," +
            "%s NUMERIC," +
            "%s NUMERIC," +
            "FOREIGN KEY(%s) REFERENCES %s(%s) ON UPDATE CASCADE);",
            RECORD_TABLE,
            RECORD_ID, CATEGORY_ID_REF, DESCRIPTION, START_TIME, END_TIME, TIME_SEGMENT,
            CATEGORY_ID_REF, CATEGORY_TABLE, CATEGORY_ID);
    private static final String CREATE_PHOTO_RECORD_TABLE_QUERY = String.format("CREATE TABLE %s (" +
            "%s INTEGER," +
            "%s INTEGER," +
            " FOREIGN KEY(%s) REFERENCES %s(%s) ON UPDATE CASCADE ON DELETE CASCADE," +
            " FOREIGN KEY(%s) REFERENCES %s(%s) ON UPDATE CASCADE ON DELETE CASCADE);",
            TIME_PHOTO_TABLE,
            RECORD_ID_REF, PHOTO_ID_REF,
            RECORD_ID_REF, CATEGORY_TABLE, CATEGORY_ID,
            PHOTO_ID_REF, TIME_PHOTO_TABLE, PHOTO_ID);

    private String sqlQuery = "";

    public DbUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PHOTO_QUERY);
        sqLiteDatabase.execSQL(CREATE_CATEGORY_QUERY);
        sqLiteDatabase.execSQL(CREATE_RECORD_QUERY);
        sqLiteDatabase.execSQL(CREATE_PHOTO_RECORD_TABLE_QUERY);
        insertCategories(sqLiteDatabase, new Category("Работа"));
        insertCategories(sqLiteDatabase, new Category("Обед"));
        insertCategories(sqLiteDatabase, new Category("Отдых"));
        insertCategories(sqLiteDatabase, new Category("Уборка"));
        insertCategories(sqLiteDatabase, new Category("Сон"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        final String tableDrop = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(tableDrop + CATEGORY_TABLE);
        sqLiteDatabase.execSQL(tableDrop + PHOTO_TABLE);
        sqLiteDatabase.execSQL(tableDrop + RECORD_TABLE);
        sqLiteDatabase.execSQL(tableDrop + TIME_PHOTO_TABLE);
        onCreate(sqLiteDatabase);
    }

    public Cursor getAllFromTable(SQLiteDatabase database, String tableName) {
        return database.query(tableName, null, null, null, null, null, null);
    }

    protected long insert(SQLiteDatabase database, ContentValues values, String table) {
        long result = 0;
        database.beginTransaction();
        try {
            result = database.insert(table, null, values);
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            database.endTransaction();
        }
        return result;
    }

    protected int delete(SQLiteDatabase database, String table, String causeColumn, String[] causeArgs) {
        int result = 0;
        database.beginTransaction();
        try {
            result = database.delete(table, causeColumn + "=?", causeArgs);
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            database.endTransaction();
        }
        return result;
    }

    public void insertCategories(SQLiteDatabase database, Category data) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORY_TITLE, data.getTitle());

        database.beginTransaction();
        try {
            database.insert(DbUtils.CATEGORY_TABLE, null, contentValues);
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }


}
