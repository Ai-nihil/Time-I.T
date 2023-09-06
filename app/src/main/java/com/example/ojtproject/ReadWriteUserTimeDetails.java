package com.example.ojtproject;

import android.os.Parcel;
import android.os.Parcelable;

public class ReadWriteUserTimeDetails implements Parcelable {
    private String dateClockInTime, dateDay, clockInStatus, dateClockOutTime, clockOutStatus;
    private Long timestamp;

    public ReadWriteUserTimeDetails() {
    }

    public ReadWriteUserTimeDetails(String dateDay, String dateClockInTime, String clockInStatus, String dateClockOutTime, String clockOutStatus) {
        this.dateDay = dateDay;
        this.dateClockInTime = dateClockInTime;
        this.clockInStatus = clockInStatus;
        this.dateClockOutTime = dateClockOutTime;
        this.clockOutStatus = clockOutStatus;
    }

    protected ReadWriteUserTimeDetails(Parcel in) {
        dateDay = in.readString();
        dateClockInTime = in.readString();
        clockInStatus = in.readString();
        dateClockOutTime = in.readString();
        clockOutStatus = in.readString();
    }

    public static final Creator<ReadWriteUserTimeDetails> CREATOR = new Creator<ReadWriteUserTimeDetails>() {
        @Override
        public ReadWriteUserTimeDetails createFromParcel(Parcel in) {
            return new ReadWriteUserTimeDetails(in);
        }

        @Override
        public ReadWriteUserTimeDetails[] newArray(int size) {
            return new ReadWriteUserTimeDetails[size];
        }
    };

    public String getDateClockInTime() {
        return dateClockInTime;
    }

    public void setDateClockInTime(String dateClockInTime) {
        this.dateClockInTime = dateClockInTime;
    }

    public String getClockInStatus() {
        return clockInStatus;
    }

    public void setClockInStatus(String clockInStatus) {
        this.clockInStatus = clockInStatus;
    }

    public String getDateClockOutTime() {
        return dateClockOutTime;
    }

    public void setDateClockOutTime(String dateClockOutTime) {
        this.dateClockOutTime = dateClockOutTime;
    }

    public String getClockOutStatus() {
        return clockOutStatus;
    }

    public void setClockOutStatus(String clockOutStatus) {
        this.clockOutStatus = clockOutStatus;
    }

    public String getDateDay() {
        return dateDay;
    }

    public void setDateDay(String dateDay) {
        this.dateDay = dateDay;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateDay);
        dest.writeString(dateClockInTime);
        dest.writeString(clockInStatus);
        dest.writeString(dateClockOutTime);
        dest.writeString(clockOutStatus);
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}