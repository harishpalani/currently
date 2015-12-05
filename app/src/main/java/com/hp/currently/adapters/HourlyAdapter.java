package com.hp.currently.adapters;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hp.currently.R;
import com.hp.currently.model.weather.Hourly;

import butterknife.Bind;

/**
 * Created by HP Labs on 9/5/2015.
 */
public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder> {

    private Hourly[] mHourly;
    @Bind(R.id.relativeLayout)
    RelativeLayout mRelativeLayout;

    public HourlyAdapter(Hourly[] hourly) {
        mHourly = hourly;
    }

    @Override
    public HourlyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_hourly_forecast_list, parent, false);
        HourlyViewHolder viewHolder = new HourlyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HourlyViewHolder holder, int position) {
        holder.bindHourly(mHourly[position]);
    }

    @Override
    public int getItemCount() {
        return mHourly.length;
    }

    public class HourlyViewHolder extends RecyclerView.ViewHolder {

        public TextView mHourLabel;
        public ImageView mIconImageView;
        public TextView mTemperatureLabel;
        public TextView mSummaryLabel;

        public HourlyViewHolder(View itemView) {
            super(itemView);
            mHourLabel = (TextView) itemView.findViewById(R.id.hourLabel);
            mIconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);
            mTemperatureLabel = (TextView) itemView.findViewById(R.id.temperatureLabel);
            mSummaryLabel = (TextView) itemView.findViewById(R.id.summaryLabel);
        }

        public void bindHourly(Hourly hourly) {
            mHourLabel.setText(hourly.getHour() + "");
            mIconImageView.setImageResource(hourly.getIconID());
            mTemperatureLabel.setText(hourly.getTemperature() + "");
            mSummaryLabel.setText(hourly.getSummary());
        }
    }
}