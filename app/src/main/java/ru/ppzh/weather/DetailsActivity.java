package ru.ppzh.weather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class DetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);

        if (savedInstanceState == null) {
            addNewFragment();
        }
    }

    private void addNewFragment() {

        long id = getIntent().getLongExtra(WeatherFragment.EXTRA_ID, -1);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, WeatherFragment.newInstance(id), WeatherFragment.TAG)
                .commit();

    }

}
