package com.example.ojtproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UserUpdateProfileActivity extends AppCompatActivity {

    private EditText userUpdateProfileActivityEditTextFullName,
                     userUpdateProfileActivityEditTextBirthdate,
                     userUpdateProfileActivityEditTextMobileNumber;
    private RadioGroup userUpdateProfileActivityRadioGroupGender;
    private RadioButton userUpdateProfileActivityRadioButtonGenderSelected;
    private String fullName, email, birthday, gender, mobileNumber;
    private FirebaseAuth authProfile;
    private ProgressBar userUpdateProfileActivityProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_profile);

        userUpdateProfileActivityProgressBar = findViewById(R.id.userUpdateProfileActivityProgressBar);
        userUpdateProfileActivityEditTextFullName = findViewById(R.id.userUpdateProfileActivityEditTextFullName);
        userUpdateProfileActivityEditTextBirthdate = findViewById(R.id.userUpdateProfileActivityEditTextBirthdate);
        userUpdateProfileActivityEditTextMobileNumber = findViewById(R.id.userUpdateProfileActivityEditTextMobileNumber);

        userUpdateProfileActivityRadioGroupGender = findViewById(R.id.userUpdateProfileActivityRadioGroupGender);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        //Show Profile Data
        showProfile(firebaseUser);

        //Upload Profile Pic
        Button userUpdateProfileActivityButtonUploadProfilePic =  findViewById(R.id.userUpdateProfileActivityButtonUploadProfilePic);
        userUpdateProfileActivityButtonUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserUpdateProfileActivity.this, UploadProfilePictureActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Setting up DatePicker on EditText
        userUpdateProfileActivityEditTextBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Extracting saved dd, m, yyyy into different variables by creating an array delimited by "/"
                String currentBirthdate[] = birthday.split("/");

                int day = Integer.parseInt(currentBirthdate[0]);
                int month = Integer.parseInt(currentBirthdate[1]) - 1; //to take care of month index starting from 0
                int year = Integer.parseInt(currentBirthdate[2]);

                DatePickerDialog picker;

                //Date Picker Dialog
                picker = new DatePickerDialog(UserUpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        userUpdateProfileActivityEditTextBirthdate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        //Update Profile
        Button userUpdateProfileActivityButtonUpdateInfo = findViewById(R.id.userUpdateProfileActivityButtonUpdateInfo);
        userUpdateProfileActivityButtonUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });

        Button userUpdateProfileActivityButtonUploadChangePassword = findViewById(R.id.userUpdateProfileActivityButtonUploadChangePassword);
        userUpdateProfileActivityButtonUploadChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserUpdateProfileActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
    
    //Update Profile
    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID = userUpdateProfileActivityRadioGroupGender.getCheckedRadioButtonId();
        userUpdateProfileActivityRadioButtonGenderSelected = findViewById(selectedGenderID);

        if(TextUtils.isEmpty(fullName)){
            Toast.makeText(UserUpdateProfileActivity.this,"Please enter your full name", Toast.LENGTH_LONG).show();
            userUpdateProfileActivityEditTextFullName.setError("Full Name is required");
            userUpdateProfileActivityEditTextFullName.requestFocus();
        } else if(TextUtils.isEmpty(birthday)) {
            Toast.makeText(UserUpdateProfileActivity.this, "Please enter your birthdate", Toast.LENGTH_LONG).show();
            userUpdateProfileActivityEditTextBirthdate.setError("Birthdate is required");
            userUpdateProfileActivityEditTextBirthdate.requestFocus();
        } else if(TextUtils.isEmpty(userUpdateProfileActivityRadioButtonGenderSelected.getText())){
            Toast.makeText(UserUpdateProfileActivity.this,"Please select your gender", Toast.LENGTH_LONG).show();
            userUpdateProfileActivityRadioButtonGenderSelected.setError("Gender is required");
            userUpdateProfileActivityRadioButtonGenderSelected.requestFocus();
        } else if(TextUtils.isEmpty(mobileNumber)) {
            Toast.makeText(UserUpdateProfileActivity.this, "Please enter your mobile number", Toast.LENGTH_LONG).show();
            userUpdateProfileActivityEditTextMobileNumber.setError("Mobile Number is required");
            userUpdateProfileActivityEditTextMobileNumber.requestFocus();
        } else if(mobileNumber.length() != 11) {
            Toast.makeText(UserUpdateProfileActivity.this, "Please re-enter your mobile number", Toast.LENGTH_LONG).show();
            userUpdateProfileActivityEditTextMobileNumber.setError("Mobile Number should be 11 digits");
            userUpdateProfileActivityEditTextMobileNumber.requestFocus();
        } else {
            gender = userUpdateProfileActivityRadioButtonGenderSelected.getText().toString();
            fullName = userUpdateProfileActivityEditTextFullName.getText().toString();
            birthday = userUpdateProfileActivityEditTextBirthdate.getText().toString();
            mobileNumber = userUpdateProfileActivityEditTextMobileNumber.getText().toString();

            //Enter User Data into the Firebase Realtime Database. Set up dependencies
            ReadWriteUserDetails readWriteUserDetails = new ReadWriteUserDetails(birthday, gender, mobileNumber);

            //Extract User reference from Database for "Registered Users"
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

            String userID = firebaseUser.getUid();

            userUpdateProfileActivityProgressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(readWriteUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        //Setting new display Name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName).build();
                        firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(UserUpdateProfileActivity.this, "Update Successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(UserUpdateProfileActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("loadFragment", 1);
                        startActivity(intent);

                    } else {
                        try{
                            throw task.getException();
                        } catch (Exception e){
                            Toast.makeText(UserUpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    userUpdateProfileActivityProgressBar.setVisibility(View.GONE);
                }
            });
        }

    }

    //Fetch data from Firebase and display
    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        //Extracting User Reference from Database for "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

        userUpdateProfileActivityProgressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readWriteUserDetails != null){
                    fullName = firebaseUser.getDisplayName();
                    birthday = readWriteUserDetails.getBirthDate();
                    gender = readWriteUserDetails.getGender();
                    mobileNumber = readWriteUserDetails.getMobileNumber();

                    userUpdateProfileActivityEditTextFullName.setText(fullName);
                    userUpdateProfileActivityEditTextBirthdate.setText(birthday);
                    userUpdateProfileActivityEditTextMobileNumber.setText(mobileNumber);

                    //Show Gender through Radio Button
                    if(gender.equals("Male")){
                        userUpdateProfileActivityRadioButtonGenderSelected = findViewById(R.id.userUpdateProfileActivityRadioButtonMale);
                    } else {
                        userUpdateProfileActivityRadioButtonGenderSelected = findViewById(R.id.userUpdateProfileActivityRadioButtonFemale);
                    }
                    userUpdateProfileActivityRadioButtonGenderSelected.setChecked(true);
                } else {
                    Toast.makeText(UserUpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                userUpdateProfileActivityProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserUpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                userUpdateProfileActivityProgressBar.setVisibility(View.GONE);
            }
        });
    }
}