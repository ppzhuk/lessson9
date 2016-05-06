package ru.ppzh.weather;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class DetailsPagerActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private ViewPager mViewPager;
    private List<Forecast> forecasts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        getSupportLoaderManager().initLoader(
                MainActivity.DETAILS_PAGER_ACTIVITY_LOADER_ID, null, this);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MasterFragment.FORECASTS_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ForecastCursor forecastCursor = new ForecastCursor(data);
        forecasts = forecastCursor.getAll();
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {

            @Override
            public Fragment getItem(int position) {
                Forecast f = forecasts.get(position);
                return WeatherFragment.newInstance(f.getId());
            }

            @Override
            public int getCount() {
                return forecasts.size();
            }
        });

        long forecastID = getIntent().getLongExtra(WeatherFragment.EXTRA_ID, -1);
        for (int i = 0; i < forecasts.size(); i++) {
            if (forecasts.get(i).getId() == forecastID) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
