package com.hp.currently.model.weather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by hp on 8/29/15.
 */
public class Currently {

    private String mIcon;
    private long mTime;
    private String mFormattedTime;
    private double mTemperature;
    private double mHumidity;
    private double mChanceOfPrecipitation;
    private String mSummary;
    private String mTimezone;
    private String mLocation;

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getIconID() {
        return Forecast.getIconID(mIcon);
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getFormattedTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimezone()));
        Date dateTime = new Date(getTime()*1000);
        mFormattedTime = formatter.format(dateTime);

        return mFormattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        mFormattedTime = formattedTime;
    }

    public int getTemperature() {
        return (int) Math.round(mTemperature);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int getChanceOfPrecipitation() {
        double precipPercentage = mChanceOfPrecipitation * 100;
        return (int) Math.round(precipPercentage);
    }

    public void setChanceOfPrecipitation(double chanceOfPrecipitation) {
        mChanceOfPrecipitation = chanceOfPrecipitation;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }
}
