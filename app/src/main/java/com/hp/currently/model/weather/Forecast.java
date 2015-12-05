package com.hp.currently.model.weather;

import com.hp.currently.R;

/**
 * Created by HP Labs on 8/29/2015.
 */
public class Forecast {
    private Currently mCurrently;
    private Hourly[] mHourly;
    private Daily[] mDaily;

    public Currently getCurrently() {
        return mCurrently;
    }

    public void setCurrently(Currently currently) {
        mCurrently = currently;
    }

    public Hourly[] getHourly() {
        return mHourly;
    }

    public void setHourly(Hourly[] hourly) {
        mHourly = hourly;
    }

    public Daily[] getDaily() {
        return mDaily;
    }

    public void setDaily(Daily[] daily) {
        mDaily = daily;
    }

    public static int getIconID(String iconString) {
        // clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night.
        int iconID = R.drawable.clear_day;

        if (iconString.equals("clear-day")) {
            iconID = R.drawable.clear_day;
        } else if (iconString.equals("clear-night")) {
            iconID = R.drawable.clear_night;
        } else if (iconString.equals("rain")) {
            iconID = R.drawable.rain;
        } else if (iconString.equals("snow")) {
            iconID = R.drawable.snow;
        } else if (iconString.equals("sleet")) {
            iconID = R.drawable.sleet;
        } else if (iconString.equals("wind")) {
            iconID = R.drawable.wind;
        } else if (iconString.equals("fog")) {
            iconID = R.drawable.fog;
        } else if (iconString.equals("cloudy")) {
            iconID = R.drawable.cloudy;
        } else if (iconString.equals("partly-cloudy-day")) {
            iconID = R.drawable.partly_cloudy;
        } else if (iconString.equals("partly-cloudy-night")) {
            iconID = R.drawable.cloudy_night;
        }

        return iconID;
    }
}
