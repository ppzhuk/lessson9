package ru.ppzh.weather;

import android.provider.BaseColumns;

interface ForecastsTable extends BaseColumns {
    String TABLE_NAME = "forecasts";

    String COLUMN_CITY = "city";
    String COLUMN_COUNTRY = "country";
    String COLUMN_UPDATE_DATE = "update_date";
    String COLUMN_DESCRIPTION = "description";
    String COLUMN_HUMIDITY = "humidity";
    String COLUMN_PRESSURE = "pressure";
    String COLUMN_TEMPERATURE = "temperature";

    String COLUMN_IMAGE_ID = "image_id";
    String COLUMN_IMAGE_SUNSET = "image_sunset";
    String COLUMN_IMAGE_SUNRISE = "image_sunrise";
}
