package com.hp.currently.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.currently.R;
import com.hp.currently.adapters.DailyAdapter;
import com.hp.currently.model.weather.Daily;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DailyForecastActivity extends Activity {

    private Daily[] mDaily;
    @Bind(android.R.id.list) ListView mListView;
    @Bind(android.R.id.empty) TextView mEmptyTextView;
    @Bind(R.id.relativeLayout) RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);

        mDaily = Arrays.copyOf(parcelables, parcelables.length, Daily[].class);

        DailyAdapter adapter = new DailyAdapter(this, mDaily);
        mListView.setAdapter(adapter);
        mListView.setEmptyView(mEmptyTextView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dayOfWeek = mDaily[position].getDayOfWeek();
                String summary = mDaily[position].getSummary().toLowerCase();
                int maxTemp = mDaily[position].getMaxTemperature();
                String message = String.format("%s | Highs of %s degrees and %s",
                        dayOfWeek.toUpperCase(), maxTemp, summary);
                Snackbar.make(mRelativeLayout, message, Snackbar.LENGTH_INDEFINITE).show();
                //  Toast.makeText(DailyForecastActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        String today = "Today";
        String summary = mDaily[0].getSummary().toLowerCase();
        int maxTemp = mDaily[0].getMaxTemperature();
        String message = String.format("%s | Highs of %s degrees and %s",
                                        today.toUpperCase(), maxTemp, summary);
        Snackbar.make(mRelativeLayout, message, Snackbar.LENGTH_INDEFINITE).show();
    }
}
