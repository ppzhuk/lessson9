package ru.ppzh.weather;

import android.database.Cursor;
import android.database.CursorWrapper;

public class ForecastCursor extends CursorWrapper {

    public ForecastCursor(Cursor cursor) {
        super(cursor);
    }

    public Forecast getForecast() {
        if (isBeforeFirst() || isAfterLast()) {
            return null;
        }
        Forecast f = new Forecast();
        f.setId(getLong(getColumnIndex(ForecastsTable._ID)));
        f.setCity(getString(getColumnIndex(ForecastsTable.COLUMN_CITY)));
        f.setCountry(getString(getColumnIndex(ForecastsTable.COLUMN_COUNTRY)));
        f.setUpdated(getString(getColumnIndex(ForecastsTable.COLUMN_UPDATE_DATE)));
        f.setDescription(getString(getColumnIndex(ForecastsTable.COLUMN_DESCRIPTION)));
        f.setHumidity(getInt(getColumnIndex(ForecastsTable.COLUMN_HUMIDITY)));
        f.setPressure(getInt(getColumnIndex(ForecastsTable.COLUMN_PRESSURE)));
        f.setTemperature(getDouble(getColumnIndex(ForecastsTable.COLUMN_TEMPERATURE)));
        f.setForecastId(getLong(getColumnIndex(ForecastsTable.COLUMN_IMAGE_ID)));
        f.setSunrise(getLong(getColumnIndex(ForecastsTable.COLUMN_IMAGE_SUNRISE)));
        f.setSunset(getLong(getColumnIndex(ForecastsTable.COLUMN_IMAGE_SUNSET)));
        return f;
    }
}
