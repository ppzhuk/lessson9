package ru.ppzh.weather;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity
        implements MasterFragment.Callbacks, WeatherFragment.Callbacks {
    public static final int MASTER_FRAGMENT_LOADER_ID = 0;
    public static final int DETAILS_PAGER_ACTIVITY_LOADER_ID = 1;

    @Override
    protected Fragment createFragment() {
        return MasterFragment.newInstance();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCitySelected(long id) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = new Intent(this, DetailsPagerActivity.class);
            intent.putExtra(WeatherFragment.EXTRA_ID, id);
            startActivity(intent);
        } else {
            addCityFragment(id);
        }
    }

    @Override
    public void updateCity(long id) {
        if (id == -1 || findViewById(R.id.detail_fragment_container) == null) {
            return;
        }
        addCityFragment(id);
    }

    private void addCityFragment(long id) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment oldFragment = fm.findFragmentById(R.id.detail_fragment_container);
        Fragment newFragment = WeatherFragment.newInstance(id);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.add(R.id.detail_fragment_container, newFragment).commit();
    }

    @Override
    public void onCityDeleted(long deletedForecastId) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment oldFragment = fm.findFragmentById(R.id.detail_fragment_container);
        if (oldFragment != null) {
            long currentForecastId =
                    oldFragment.getArguments().getLong(WeatherFragment.EXTRA_ID, -1);
            if (currentForecastId == deletedForecastId) {
                ft.remove(oldFragment).commit();
            }
        }
    }

    @Override
    public void updateCurrentItem(long pos) {
        // Nothing to do here
    }

    // In two-pane mode application displays options menu for both
    // MasterFragment and WeatherFragment. When app switches to one-pane mode
    // WeatherFragment's menu still there but it mustn't. Code below hides it.
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            MenuItem mi = menu.findItem(R.id.fragment_weather_action_update);
            if (mi != null) {
                mi.setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }
}

