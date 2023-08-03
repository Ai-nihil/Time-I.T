package com.example.ojtproject;

public class ReadWriteUserDetails {
    public String birthDate,
                  gender,
                  mobileNumber;

    public ReadWriteUserDetails(String textBirthDate, String textGender, String textMobileNumber) {
        this.birthDate = textBirthDate;
        this.gender = textGender;
        this.mobileNumber = textMobileNumber;
    }
}
