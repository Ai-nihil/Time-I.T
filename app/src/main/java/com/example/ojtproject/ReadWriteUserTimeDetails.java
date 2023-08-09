package com.example.ojtproject;

public class ReadWriteUserTimeDetails {
    private String dateTime,dateDay,status;

    public ReadWriteUserTimeDetails() {
    }

    public ReadWriteUserTimeDetails (String dateTime, String dateDay, String status) {
        this.dateTime = dateTime;
        this.dateDay = dateDay;
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

    public String getDateDay() { return dateDay; }

    public void setDateDay(String dateDay) { this.dateDay = dateDay; }
}
