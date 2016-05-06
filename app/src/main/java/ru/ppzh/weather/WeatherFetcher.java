package ru.ppzh.weather;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherFetcher {
    public static final String TAG = "WeatherFetcher";

    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";

    private Context context;

    public WeatherFetcher(Context context) {
        this.context = context;
    }

    public JSONObject getJSON(String city) {
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API,
                    URLEncoder.encode(city, Charset.defaultCharset().name())));
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("x-api-key",
                    context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();
            connection.disconnect();

            JSONObject data = new JSONObject(json.toString());

            // 404 for unsuccessful request
            if (data.getInt("cod") != 200) {
                Log.e(TAG, "Unsuccessful request.");
                return null;
            }

            return data;
        } catch (Exception e) {
            Log.e(TAG, "Unknown error while downloading.");
            return null;
        }
    }

    public Forecast getForecast(JSONObject json) {
        try {
            Forecast forecast = new Forecast();
            forecast.setCity(json.getString("name").toUpperCase(Locale.US));
            forecast.setCountry(json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            forecast.setDescription(details.getString("description").toUpperCase(Locale.US));
            forecast.setHumidity(main.getInt("humidity"));
            forecast.setPressure(main.getInt("pressure"));

            forecast.setTemperature(main.getDouble("temp"));

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date());
            forecast.setUpdated(updatedOn);

            forecast.setForecastId(details.getInt("id"));
            forecast.setSunrise(json.getJSONObject("sys").getLong("sunrise") * 1000);
            forecast.setSunset(json.getJSONObject("sys").getLong("sunset") * 1000);

            return forecast;
        } catch (Exception e) {
            Log.e(TAG, "One or more fields not found in the JSON data");
        }
        return null;
    }
}

