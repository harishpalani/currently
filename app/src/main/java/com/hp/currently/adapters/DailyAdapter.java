package com.hp.currently.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hp.currently.R;
import com.hp.currently.model.weather.Daily;

/**
 * Created by HP Labs on 9/2/2015.
 */
public class DailyAdapter extends BaseAdapter {
    private Context mContext;
    private Daily[] mDaily;

    public DailyAdapter(Context context, Daily[] daily) {
        mContext = context;
        mDaily = daily;
    }

    @Override
    public int getCount() {
        return mDaily.length;
    }

    @Override
    public Object getItem(int position) {
        return mDaily[position];
    }

    @Override
    public long getItemId(int position) {
        return 0; // not using this method. normally used to tag items for easy reference.
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            // convertView == null indicates it's brand new & we need to create everything
            convertView = LayoutInflater.from(mContext).inflate(R.layout.custom_daily_forecast_list, null);
            viewHolder = new ViewHolder();

            viewHolder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
            viewHolder.dayLabel = (TextView) convertView.findViewById(R.id.dayLabel);
            viewHolder.temperatureLabel = (TextView) convertView.findViewById(R.id.temperatureLabel);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Daily daily = mDaily[position];
        viewHolder.iconImageView.setImageResource(daily.getIconID());
        if (position == 0) {
            viewHolder.dayLabel.setText("Today");
        } else {
            viewHolder.dayLabel.setText(daily.getDayOfWeek());
        }
        viewHolder.temperatureLabel.setText(daily.getMaxTemperature() + "");

        return convertView;
    }

    private static class ViewHolder {
        public ImageView iconImageView;
        public TextView dayLabel;
        public TextView temperatureLabel;
    }
}
