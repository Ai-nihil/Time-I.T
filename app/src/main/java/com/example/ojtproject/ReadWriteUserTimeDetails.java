package com.example.ojtproject;

public class ReadWriteUserTimeDetails {
    private String dateTime,status;

    public ReadWriteUserTimeDetails() {
    }

    public ReadWriteUserTimeDetails (String dateTime, String status) {
        this.dateTime = dateTime;
        this.status = status;
    }

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
}
