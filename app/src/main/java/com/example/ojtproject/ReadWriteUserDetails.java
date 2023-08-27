package com.example.ojtproject;

public class ReadWriteUserDetails {
    public String birthDate,
                  gender,
                  mobileNumber;

    //Empty Constructor
    public ReadWriteUserDetails() {
    }

    public ReadWriteUserDetails(String textBirthDate, String textGender, String textMobileNumber) {
        this.birthDate = textBirthDate;
        this.gender = textGender;
        this.mobileNumber = textMobileNumber;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
