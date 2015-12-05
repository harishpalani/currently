package com.hp.currently.model.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by HP Labs on 8/29/2015.
 */
public class Daily implements Parcelable {
    private long mTime;
    private String mFormattedTime;
    private String mSummary;
    private double mMaxTemperature;
    private double mMinTemperature;
    private String mIcon;
    private String mTimezone;
    private String mDayOfWeek;

    public Daily() {  }

    private Daily(Parcel in) {
        mTime = in.readLong();
        mTimezone = in.readString();
        mFormattedTime = in.readString();
        mSummary = in.readString();
        mMaxTemperature = in.readDouble();
        mIcon = in.readString();
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

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public int getMaxTemperature() {
        return (int) Math.round(mMaxTemperature);
    }

    public void setMaxTemperature(double maxTemperature) {
        mMaxTemperature = maxTemperature;
    }

    public double getMinTemperature() {
        return mMinTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        mMinTemperature = minTemperature;
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

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public String getDayOfWeek() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE");
        dateFormatter.setTimeZone(TimeZone.getTimeZone(mTimezone));
        Date day = new Date(mTime * 1000);
        return dateFormatter.format(day);
    }

    public void setDayOfWeek(String dayOfWeek) {
        mDayOfWeek = dayOfWeek;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTime);
        dest.writeString(mTimezone);
        dest.writeString(mFormattedTime);
        dest.writeString(mSummary);
        dest.writeDouble(mMaxTemperature);
        dest.writeString(mIcon);
    }

    public static final Creator<Daily> CREATOR = new Creator<Daily>() {
        @Override
        public Daily createFromParcel(Parcel source) {
            return new Daily(source);
        }

        @Override
        public Daily[] newArray(int size) {
            return new Daily[size];
        }
    };
}
