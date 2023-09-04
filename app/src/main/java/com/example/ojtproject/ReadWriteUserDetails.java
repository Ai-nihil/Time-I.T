package com.example.ojtproject;

public class ReadWriteUserDetails {
    public String fullName,
                  birthDate,
                  gender,
                  mobileNumber,
                  imageUrl,
                  userId;

    //Empty Constructor
    public ReadWriteUserDetails() {
    }

    public ReadWriteUserDetails(String textFullName, String textBirthDate, String textGender, String textMobileNumber) {
        this.fullName = textFullName;
        this.birthDate = textBirthDate;
        this.gender = textGender;
        this.mobileNumber = textMobileNumber;
    }

    public String getFullName() { return fullName; }

    public void setFullName(String fullName) { this.fullName = fullName; }

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
