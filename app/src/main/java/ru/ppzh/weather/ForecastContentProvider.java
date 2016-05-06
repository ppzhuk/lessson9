package ru.ppzh.weather;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class ForecastContentProvider extends ContentProvider {
    public static final String TAG = "ForecastContentProvider";

    public static final String AUTHORITY = "ru.ppzh.weather.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final int FORECASTS = 1;
    public static final int FORECASTS_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, "/forecasts", FORECASTS);
        uriMatcher.addURI(AUTHORITY, "/forecasts/#", FORECASTS_ID);
    }

    private ForecastDatabaseHelper helper;

    public ForecastContentProvider() {
        helper = new ForecastDatabaseHelper(getContext());
    }

    @Override
    public boolean onCreate() {
        helper = new ForecastDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(TAG, "Insertion: " + values.toString());

        int match = uriMatcher.match(uri);
        String tableName;
        switch (match) {
            case FORECASTS:
                tableName = ForecastsTable.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        long rowId = helper.getWritableDatabase().insert(tableName, null, values);

        Log.d(TAG, "Inserted ID: " + rowId);

        Uri inserted = ContentUris.withAppendedId(uri, rowId);
        getContext().getContentResolver().notifyChange(inserted, null);
        return inserted;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        Log.d(TAG, "Delete");

        int match = uriMatcher.match(uri);
        String tableName;
        switch (match) {
            case FORECASTS_ID:
                tableName = ForecastsTable.TABLE_NAME;
                if (selection == null) {
                    selection = "";
                }
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        int count = helper.getWritableDatabase().delete(tableName, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        Log.d(TAG, "Updating: " + values.toString());

        int match = uriMatcher.match(uri);
        String tableName;
        switch (match) {
            case FORECASTS_ID:
                tableName = ForecastsTable.TABLE_NAME;
                if (selection == null) {
                    selection = "";
                }
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        int count = helper.getWritableDatabase().update(tableName, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "Query");

        int match = uriMatcher.match(uri);
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (match) {
            case FORECASTS:
                builder.setTables(ForecastsTable.TABLE_NAME);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
}
