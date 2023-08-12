package com.example.ojtproject;

import android.os.Parcel;
import android.os.Parcelable;

public class ReadWriteUserTimeDetails implements Parcelable {
    private String dateTime, dateDay, status;

    public ReadWriteUserTimeDetails() {
    }

    public ReadWriteUserTimeDetails(String dateTime, String dateDay, String status) {
        this.dateTime = dateTime;
        this.dateDay = dateDay;
        this.status = status;
    }

    protected ReadWriteUserTimeDetails(Parcel in) {
        dateTime = in.readString();
        dateDay = in.readString();
        status = in.readString();
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        dest.writeString(dateTime);
        dest.writeString(dateDay);
        dest.writeString(status);
    }
}