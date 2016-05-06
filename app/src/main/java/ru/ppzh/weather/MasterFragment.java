package ru.ppzh.weather;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONObject;

public class MasterFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = "MasterFragment";

    public static final Uri FORECASTS_URI = Uri.withAppendedPath(ForecastContentProvider.CONTENT_URI, "forecasts");

    public static final int _10SEC = 1000 * 10;
    public static final String SHARED_PREFERENCES = "preferences";
    public static final String ALARM_PREFERENCE = "alarm_preference";

    View root;
    ListView list;
    ForecastCursorAdapter adapter;
    WeatherFetcher fetcher;

    public static MasterFragment newInstance() {
        return new MasterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fetcher = new WeatherFetcher(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        root = view;
        list = (ListView) view.findViewById(R.id.listView);
        adapter = new ForecastCursorAdapter(getContext(), null);
        list.setAdapter(adapter);
        registerForContextMenu(list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra(WeatherFragment.EXTRA_ID, id);
                startActivity(intent);
            }
        });

        getActivity().getSupportLoaderManager().initLoader(0, null, this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.fragment_master_action_update:
                updateForecasts();
                return true;
            case R.id.fragment_master_action_add:
                getCityDialog();
                return true;
            case R.id.fragment_master_set_auto_update:
                switchAlarm();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void switchAlarm() {
        SharedPreferences sp =
                getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        boolean isAlarmOn = sp.getBoolean(ALARM_PREFERENCE, false);

        AlarmManager alarmMgr =
                (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getActivity(), UpdateService.class);
        PendingIntent alarmIntent =
                PendingIntent.getService(getActivity(), 0, intent, 0);

        if (isAlarmOn) {
            alarmMgr.cancel(alarmIntent);
            Snackbar.make(root, R.string.alarm_off, Snackbar.LENGTH_LONG).show();

            sp.edit().putBoolean(ALARM_PREFERENCE, false).apply();
        } else {
            alarmMgr.setRepeating(
                    AlarmManager.ELAPSED_REALTIME,
                    AlarmManager.INTERVAL_HOUR,
                    AlarmManager.INTERVAL_HOUR,
//                    _10SEC,
//                    _10SEC,
                    alarmIntent
            );
            Snackbar.make(root, R.string.alarm_on, Snackbar.LENGTH_LONG).show();

            sp.edit().putBoolean(ALARM_PREFERENCE, true).apply();
        }
    }

    private void updateForecasts() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                DBUpdater updater = new DBUpdater(getActivity());

                ForecastCursor oldEntriesCursor = (ForecastCursor) adapter.getCursor();
                if (oldEntriesCursor != null) {
                    updater.updateAllEntries(oldEntriesCursor);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_city:

                Uri uri = ContentUris.withAppendedId(FORECASTS_URI, info.id);
                getActivity().getContentResolver().delete(uri, null, null);

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void getCityDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final EditText edittext = new EditText(getActivity());
        edittext.setSingleLine();
        alert.setMessage(R.string.action_add);
        alert.setTitle(R.string.alert_dialog_title);
        alert.setView(edittext);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String city = edittext.getText().toString();

                new DownloadForecast().execute(city);
            }
        });
        alert.setNegativeButton(android.R.string.cancel, null);
        alert.show();
    }

    public void addData(Forecast f) {
        ContentValues values = f.getContentValues();
        getActivity().getContentResolver().insert(
                FORECASTS_URI,
                values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), FORECASTS_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(new ForecastCursor(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private class DownloadForecast extends AsyncTask<String, Void, Forecast> {

        private boolean duplicate;

        @Override
        protected Forecast doInBackground(String... params) {
            JSONObject data = fetcher.getJSON(params[0]);
            Forecast forecast = null;
            if (data != null) {
                forecast = fetcher.getForecast(data);
                if (forecast == null) {
                    Log.e(TAG, getString(R.string.forecast_parse_error) + " (City: " + params[0] + ").");
                } else {
                    isDuplicate(forecast);
                }
                return forecast;
            }
            Log.e(TAG, getString(R.string.forecast_download_error) + " (City: " + params[0] + ").");
            return null;
        }

        @Override
        protected void onPostExecute(Forecast f) {
            if (f == null) {
                Snackbar.make(root, R.string.forecast_download_error, Snackbar.LENGTH_LONG).show();
            } else {
                if (duplicate) {
                    Snackbar.make(root, R.string.forecast_duplicate_error, Snackbar.LENGTH_LONG).show();
                } else {
                    addData(f);
                }
            }
        }

        void isDuplicate(Forecast forecast) {
            ForecastCursor cursor = (ForecastCursor) adapter.getCursor();
            Forecast f;
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    f = cursor.getForecast();
                    if (forecast.getCity().equals(f.getCity()) &&
                            forecast.getCountry().equals(f.getCountry())) {
                        duplicate = true;
                        break;
                    }
                    cursor.moveToNext();
                }
            }

        }
    }
}
