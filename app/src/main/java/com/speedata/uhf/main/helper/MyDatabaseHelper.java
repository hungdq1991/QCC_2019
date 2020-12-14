package com.speedata.uhf.main.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
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
    private Context context;


    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ORDINAL_NUMBERS + " NUMERIC, " +
                COLUMN_INVENTORY_DATE + " NUMERIC, " +
                COLUMN_DEPARTMENT_CODE + " TEXT, " +
                COLUMN_ASSET_CODE + " TEXT, " +
                COLUMN_DEPARTMENT_ASSET_CODE + " TEXT, " +
                COLUMN_DEPARTMENT_ASSET_NAME + " TEXT, " +
                COLUMN_RFID_CODE + " TEXT, " +
                COLUMN_STATUS + " TEXT, " +
                COLUMN_DATE_ACCEPTANCE + " NUMERIC, " +
                COLUMN_DATE_DEPRECIATION + " NUMERIC, " +
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

    public void addMachinery(Integer ordinal_numbers,
                             String inventory_date,
                             String department_code,
                             String asset_code,
                             String department_asset_code,
                             String department_asset_name,
                             String RFID_code,
                             Integer status,
                             String date_acceptance,
                             String date_depreciation,
                             Integer time_used,
                             double original_price,
                             double depreciated_price,
                             String location,
                             String group_code,
                             String inventory_department) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ORDINAL_NUMBERS, ordinal_numbers);
        cv.put(COLUMN_INVENTORY_DATE, inventory_date);
        cv.put(COLUMN_DEPARTMENT_CODE, department_code);
        cv.put(COLUMN_ASSET_CODE, asset_code);
        cv.put(COLUMN_DEPARTMENT_ASSET_CODE, department_asset_code);
        cv.put(COLUMN_DEPARTMENT_ASSET_NAME, department_asset_name);
        cv.put(COLUMN_RFID_CODE, RFID_code);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_DATE_ACCEPTANCE, date_acceptance);
        cv.put(COLUMN_DATE_DEPRECIATION, date_depreciation);
        cv.put(COLUMN_TIME_USED, time_used);
        cv.put(COLUMN_ORIGINAL_PRICE, original_price);
        cv.put(COLUMN_DEPRECIATED_PRICE, depreciated_price);
        cv.put(COLUMN_LOCATION, location);
        cv.put(COLUMN_GROUP_CODE, group_code);
        cv.put(COLUMN_INVENTORY_DEPARTMENT, inventory_department);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Add Successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
