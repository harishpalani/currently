package com.hp.currently.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hp.currently.R;
import com.hp.currently.adapters.HourlyAdapter;
import com.hp.currently.model.weather.Hourly;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HourlyForecastActivity extends AppCompatActivity {

    private Hourly[] mHourly;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST);

        mHourly = Arrays.copyOf(parcelables, parcelables.length, Hourly[].class);

        HourlyAdapter adapter = new HourlyAdapter(mHourly);
        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
}

}
