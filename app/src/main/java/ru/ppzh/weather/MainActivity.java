package ru.ppzh.weather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);

        if (savedInstanceState == null) {
            addNewFragment();
        }
    }

    private void addNewFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, MasterFragment.newInstance(), MasterFragment.TAG)
                .commit();
    }
}
