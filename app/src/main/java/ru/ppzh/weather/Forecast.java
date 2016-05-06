package ru.ppzh.weather;

import android.content.ContentValues;

public class Forecast {
    private long id;
    private String city;
    private String country;
    private String updated;
    private String description;
    private int humidity;
    private int pressure;
    private double temperature;

    // for displaying weather icon
    private long forecastId;
    private long sunrise;
    private long sunset;

    public Forecast(String city, String description, double temperature, String updated) {
        this.city = city;
        this.description = description;
        this.temperature = temperature;
        this.updated = updated;
    }

    public Forecast() {
        this.forecastId = 0;
        this.city = "";
        this.description = "";
        this.sunrise = 0;
        this.sunset = 0;
        this.temperature = 0;
        this.updated = "";
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public long getForecastId() {
        return forecastId;
    }

    public void setForecastId(long forecastId) {
        this.forecastId = forecastId;
    }

    public long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }
    
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(ForecastsTable.COLUMN_CITY, getCity());
        values.put(ForecastsTable.COLUMN_COUNTRY, getCountry());

        values.put(ForecastsTable.COLUMN_UPDATE_DATE, getUpdated());

        values.put(ForecastsTable.COLUMN_DESCRIPTION, getDescription());
        values.put(ForecastsTable.COLUMN_HUMIDITY, getHumidity());
        values.put(ForecastsTable.COLUMN_PRESSURE, getPressure());
        values.put(ForecastsTable.COLUMN_TEMPERATURE, getTemperature());
        values.put(ForecastsTable.COLUMN_IMAGE_ID, getForecastId());
        values.put(ForecastsTable.COLUMN_IMAGE_SUNSET, getSunset());
        values.put(ForecastsTable.COLUMN_IMAGE_SUNRISE, getSunrise());

        return values;
    }
}
