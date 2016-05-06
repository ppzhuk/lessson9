package ru.ppzh.weather;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Date;

public class WeatherFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = "WeatherFragment";
    public static final String EXTRA_ID = "ru.ppzh.id";

    public static final String CELSIUS = " °C";

    private View root;
    private TextView cityField;
    private TextView updatedField;
    private TextView detailsField;
    private TextView humidityField;
    private TextView pressureField;
    private TextView currentTemperatureField;
    private ImageView weatherIcon;

    private WeatherFetcher fetcher;

    private Forecast forecast;
    private long forecastID = -1;

    public static WeatherFragment newInstance(long id) {
        WeatherFragment weatherFragment = new WeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_ID, id);
        weatherFragment.setArguments(bundle);
        return weatherFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fetcher = new WeatherFetcher(getActivity());
        forecastID = getArguments().getLong(EXTRA_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        cityField = (TextView) rootView.findViewById(R.id.city_field);
        updatedField = (TextView) rootView.findViewById(R.id.updated_field);
        detailsField = (TextView) rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (ImageView) rootView.findViewById(R.id.weather_icon);
        humidityField = (TextView) rootView.findViewById(R.id.humidity_field);
        pressureField = (TextView) rootView.findViewById(R.id.pressure_field);

        root = rootView;
        getActivity().getSupportLoaderManager().initLoader(1, null, this);

        return rootView;
    }

    private void renderForecast() {
        cityField.setText(forecast.getCity() + ", " + forecast.getCountry());
        updatedField.setText(forecast.getUpdated());
        detailsField.setText(forecast.getDescription());
        humidityField.setText(getString(R.string.humidity) + " " + forecast.getHumidity() + " %");
        pressureField.setText(getString(R.string.pressure) + " " + forecast.getPressure() + " hPa");
        double t = forecast.getTemperature();
        currentTemperatureField.setText((t >= 0 ? "+" : "") +
                String.format("%.2f", t) + CELSIUS);
        renderIcon(forecast.getForecastId(), forecast.getSunrise(), forecast.getSunset());
    }

    private void renderIcon(long actualId, long sunrise, long sunset) {
        int id = (int) actualId / 100;
        int icon = R.drawable.cloudy;
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = R.drawable.sunny;
            } else {
                icon = R.drawable.clear_night;
            }
        } else {
            switch (id) {
                case 2:
                    icon = R.drawable.thunder;
                    break;
                case 3:
                    icon = R.drawable.drizzle;
                    break;
                case 5:
                    icon = R.drawable.rainy;
                    break;
                case 6:
                    icon = R.drawable.snowy;
                    break;
                case 7:
                    icon = R.drawable.foggy;
                    break;
                case 8:
                    icon = R.drawable.cloudy;
                    break;
            }
        }
        weatherIcon.setImageResource(icon);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getContext(),
                MasterFragment.FORECASTS_URI,
                null,
                "_ID = " + Long.toString(forecastID),
                null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ForecastCursor forecastCursor = new ForecastCursor(data);
        forecastCursor.moveToFirst();
        forecast = forecastCursor.getForecast();

        renderForecast();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private class DownloadForecast extends AsyncTask<String, Void, Forecast> {

        @Override
        protected Forecast doInBackground(String... params) {
            JSONObject data = fetcher.getJSON(params[0]);
            Forecast forecast = null;
            if (data != null) {
                forecast = fetcher.getForecast(data);

                if (forecast != null) {

                    Uri uri = ContentUris.withAppendedId(
                            MasterFragment.FORECASTS_URI,
                            Long.parseLong(params[1])
                    );
                    ContentValues cv = forecast.getContentValues();
                    getActivity().getContentResolver().update(uri, cv, null, null);
                }

                return forecast;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Forecast f) {
            if (f == null) {
                Log.e(TAG, getString(R.string.forecast_download_error));
                Snackbar.make(root, R.string.forecast_download_error, Snackbar.LENGTH_LONG).show();
            } else {
                forecast = f;
                renderForecast();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_weather_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_weather_action_update:
                new DownloadForecast()
                        .execute(forecast.getCity(), Long.toString(forecast.getId()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
