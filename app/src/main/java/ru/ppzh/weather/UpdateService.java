package ru.ppzh.weather;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class UpdateService extends IntentService {
    public static final String TAG = "UpdateService";

    public UpdateService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "In " + TAG + " onHandleIntent.");

        DBUpdater updater = new DBUpdater(getApplicationContext());
        Cursor c = getApplicationContext().getContentResolver().query(
                MasterFragment.FORECASTS_URI,
                null, null, null, null
        );
        if (c != null) {
            ForecastCursor oldEntriesCursor = new ForecastCursor(c);
            updater.updateAllEntries(oldEntriesCursor);
            c.close();
        }

    }
}
