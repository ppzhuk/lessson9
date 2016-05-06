package ru.ppzh.weather;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

public class DBUpdater {
    public static final String TAG = "DBUpdater";

    Context context;

    public DBUpdater(Context context) {
        this.context = context;
    }

    public void updateAllEntries(ForecastCursor oldEntriesCursor) {
        WeatherFetcher fetcher = new WeatherFetcher(context);
        Forecast[] oldForecasts;
        int count = 0;
        oldEntriesCursor.moveToFirst();
        oldForecasts = new Forecast[oldEntriesCursor.getCount()];
        while (!oldEntriesCursor.isAfterLast()) {
            oldForecasts[count++] = oldEntriesCursor.getForecast();
            oldEntriesCursor.moveToNext();
        }

        for (int i = 0; i < count; i++) {
            JSONObject data = fetcher.getJSON(oldForecasts[i].getCity());
            Forecast newForecast = null;
            if (data != null) {
                newForecast = fetcher.getForecast(data);
                if (newForecast == null) {
                    Log.e(TAG, context.getString(R.string.forecast_parse_error) +
                            " (City: " + oldForecasts[i].getCity() + "). Update stopped");
                    break;
                } else {
                    Uri uri = ContentUris.withAppendedId(MasterFragment.FORECASTS_URI, oldForecasts[i].getId());
                    ContentValues cv = newForecast.getContentValues();
                    context.getContentResolver().update(uri, cv, null, null);
                }
            } else {
                Log.e(TAG, context.getString(R.string.forecast_download_error) +
                        " (City: " + oldForecasts[i].getCity() + "). Update stopped.");
                break;
            }
        }
    }
}
