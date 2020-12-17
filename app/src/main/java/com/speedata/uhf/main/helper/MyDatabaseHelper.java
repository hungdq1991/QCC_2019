package com.speedata.uhf.main.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.Nullable;

import com.speedata.uhf.main.model.MachineryModel;

import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "MyDatabaseHelper";

    private static final String DATABASE_NAME = "qcc.db";
    private static final Integer DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "department_machinery";
    private static final String COLUMN_ORDINAL_NUMBERS = "ordinal_numbers";
    private static final String COLUMN_INVENTORY_DATE = "inventory_date";
    private static final String COLUMN_DEPARTMENT_CODE = "department_code";
    private static final String COLUMN_ASSET_CODE = "asset_code";
    private static final String COLUMN_DEPARTMENT_ASSET_CODE = "department_asset_code";
    private static final String COLUMN_DEPARTMENT_ASSET_NAME = "department_asset_name";
    private static final String COLUMN_RFID_CODE = "RFID_code";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_DATE_ACCEPTANCE = "date_acceptance";
    private static final String COLUMN_DATE_DEPRECIATION = "date_depreciation";
    private static final String COLUMN_TIME_USED = "time_used";
    private static final String COLUMN_ORIGINAL_PRICE = "original_price";
    private static final String COLUMN_DEPRECIATED_PRICE = "depreciated_price";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_GROUP_CODE = "group_code";
    private static final String COLUMN_INVENTORY_DEPARTMENT = "inventory_department";


    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ORDINAL_NUMBERS + " INTEGER, " +
                COLUMN_INVENTORY_DATE + " TEXT, " +
                COLUMN_DEPARTMENT_CODE + " TEXT, " +
                COLUMN_ASSET_CODE + " TEXT, " +
                COLUMN_DEPARTMENT_ASSET_CODE + " TEXT, " +
                COLUMN_DEPARTMENT_ASSET_NAME + " TEXT, " +
                COLUMN_RFID_CODE + " TEXT, " +
                COLUMN_STATUS + " TEXT, " +
                COLUMN_DATE_ACCEPTANCE + " TEXT, " +
                COLUMN_DATE_DEPRECIATION + " TEXT, " +
                COLUMN_TIME_USED + " NUMERIC, " +
                COLUMN_ORIGINAL_PRICE + " NUMERIC, " +
                COLUMN_DEPRECIATED_PRICE + " NUMERIC, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_GROUP_CODE + " TEXT, " +
                COLUMN_INVENTORY_DEPARTMENT + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addMachinery(List<MachineryModel> listMachinery) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        db.beginTransaction();
        try {
            for (MachineryModel machinery_model : listMachinery) {
                cv.put(COLUMN_ORDINAL_NUMBERS, machinery_model.getOrdinal_numbers());
                Log.d(TAG, "ORDINAL_NUMBERS: " + machinery_model.getOrdinal_numbers());
                cv.put(COLUMN_INVENTORY_DATE, machinery_model.getInventory_date());
                cv.put(COLUMN_DEPARTMENT_CODE, machinery_model.getDepartment_code());
                cv.put(COLUMN_ASSET_CODE, machinery_model.getAsset_code());
                cv.put(COLUMN_DEPARTMENT_ASSET_CODE, machinery_model.getDepartment_asset_code());
                cv.put(COLUMN_DEPARTMENT_ASSET_NAME, machinery_model.getDepartment_asset_name());
                cv.put(COLUMN_RFID_CODE, machinery_model.getRFID_code());
                cv.put(COLUMN_STATUS, machinery_model.getStatus());
                cv.put(COLUMN_DATE_ACCEPTANCE, DateFormat.format("dd/MM/yyyy", machinery_model.getDate_acceptance()).toString());
                cv.put(COLUMN_DATE_DEPRECIATION, DateFormat.format("dd/MM/yyyy", machinery_model.getDate_depreciation()).toString());
                cv.put(COLUMN_TIME_USED, machinery_model.getTime_used());
                cv.put(COLUMN_ORIGINAL_PRICE, machinery_model.getOrdinal_numbers());
                cv.put(COLUMN_DEPRECIATED_PRICE, machinery_model.getDepreciated_price());
                cv.put(COLUMN_LOCATION, machinery_model.getLocation());
                cv.put(COLUMN_GROUP_CODE, machinery_model.getGroup_code());
                cv.put(COLUMN_INVENTORY_DEPARTMENT, machinery_model.getInventory_department());
                db.insert(TABLE_NAME, null, cv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateData(String RFID_Code, int max_ordinal_number, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_ORDINAL_NUMBERS, max_ordinal_number);

        db.update(TABLE_NAME, cv, "RFID_code=?", new String[]{RFID_Code});
//        Log.d(TAG, "RFID: " + RFID_Code + " " + max_ordinal_number + " " + status);
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    public Cursor getAllDataSQLite() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }

        return cursor;
    }

    public Cursor checkExistsDataSQLite() {
        String query = "SELECT " + COLUMN_INVENTORY_DATE
                + " ," + COLUMN_INVENTORY_DEPARTMENT
                + " FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }

        return cursor;
    }
}
