package ru.ppzh.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ForecastDatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = "ForecastDatabaseHelper";
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "simple_weather.db";

    private static final String SQL_CREATE_FORECASTS_TABLE =
            "CREATE TABLE " + ForecastsTable.TABLE_NAME
                    + " ("
                    + ForecastsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ForecastsTable.COLUMN_CITY + " TEXT, "
                    + ForecastsTable.COLUMN_COUNTRY + " TEXT, "
                    + ForecastsTable.COLUMN_UPDATE_DATE + " TEXT, "
                    + ForecastsTable.COLUMN_DESCRIPTION + " TEXT, "
                    + ForecastsTable.COLUMN_HUMIDITY + " INTEGER, "
                    + ForecastsTable.COLUMN_PRESSURE + " INTEGER, "
                    + ForecastsTable.COLUMN_TEMPERATURE + " DOUBLE, "
                    + ForecastsTable.COLUMN_IMAGE_ID + " BIGINT, "
                    + ForecastsTable.COLUMN_IMAGE_SUNSET + " BIGINT, "
                    + ForecastsTable.COLUMN_IMAGE_SUNRISE + " BIGINT "
                    + ")";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ForecastsTable.TABLE_NAME;

    public ForecastDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FORECASTS_TABLE);

        Log.i(TAG, "Database created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
