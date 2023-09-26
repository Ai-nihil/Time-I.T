package com.example.ojtproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
    private String fullName, birthday, gender, mobileNumber,
                   fullNameOld, birthdayOld, genderOld, mobileNumberOld;
    private FirebaseAuth authProfile;
    private ProgressBar userUpdateProfileActivityProgressBar;
    private Boolean changesMadeFullName = false,
                    changesMadeBirthdate = false,
                    changesMadeMobileNumber = false,
                    changesMadeGender = false;
    private Boolean initialRadioButtonState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_profile);

        getSupportActionBar().setTitle("Update Profile Information");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                intent.putExtra("openedFrom", "UserUpdateProfileActivity");
                startActivity(intent);
                finish();
            }
        });

        //Setting up DatePicker on EditText
        userUpdateProfileActivityEditTextBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Extracting saved dd, m, yyyy into different variables by creating an array delimited by "/"
                birthday = userUpdateProfileActivityEditTextBirthdate.getText().toString();
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

        //Create new password
        Button userUpdateProfileActivityButtonUploadChangePassword = findViewById(R.id.userUpdateProfileActivityButtonUploadChangePassword);
        userUpdateProfileActivityButtonUploadChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserUpdateProfileActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
    }
    
    //Update Profile
    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID = userUpdateProfileActivityRadioGroupGender.getCheckedRadioButtonId();
        userUpdateProfileActivityRadioButtonGenderSelected = findViewById(selectedGenderID);

        gender = userUpdateProfileActivityRadioButtonGenderSelected.getText().toString();
        fullName = userUpdateProfileActivityEditTextFullName.getText().toString();
        birthday = userUpdateProfileActivityEditTextBirthdate.getText().toString();
        mobileNumber = userUpdateProfileActivityEditTextMobileNumber.getText().toString();

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

            //Enter User Data into the Firebase Realtime Database. Set up dependencies
            ReadWriteUserDetails readWriteUserDetails = new ReadWriteUserDetails(fullName, birthday, gender, mobileNumber);

            //Extract User reference from Database for "Registered Users"
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

            String userID = firebaseUser.getUid();

            userUpdateProfileActivityProgressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).child("userType").get().addOnCompleteListener(existingUserType -> {
                if(existingUserType.isSuccessful()) {
                    if(existingUserType.getResult().getValue() == null) {
                        // Child "userType" does not exist, create it with the initial value of userType
                        referenceProfile.child(userID).child("userType").setValue("user");
                    }
                }
            });

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
                        intent.putExtra("openedFrom", "UserProfileFragment");
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
                    fullNameOld = firebaseUser.getDisplayName();
                    birthdayOld = readWriteUserDetails.getBirthDate();
                    genderOld = readWriteUserDetails.getGender();
                    mobileNumberOld = readWriteUserDetails.getMobileNumber();

                    userUpdateProfileActivityEditTextFullName.setText(fullNameOld);
                    userUpdateProfileActivityEditTextFullName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            // Check if the text has changed
                            if (!charSequence.toString().equals(fullNameOld)) {
                                changesMadeFullName = true;
                            } else {
                                changesMadeFullName = false;
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    userUpdateProfileActivityEditTextBirthdate.setText(birthdayOld);
                    userUpdateProfileActivityEditTextBirthdate.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            // Check if the text has changed
                            if (!charSequence.toString().equals(birthdayOld)) {
                                changesMadeBirthdate = true;
                            } else {
                                changesMadeBirthdate = false;
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    userUpdateProfileActivityEditTextMobileNumber.setText(mobileNumberOld);
                    userUpdateProfileActivityEditTextMobileNumber.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            // Check if the text has changed
                            if (!charSequence.toString().equals(mobileNumberOld)) {
                                changesMadeMobileNumber = true;
                            } else {
                                changesMadeMobileNumber = false;
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    //Show Gender through Radio Button
                    if(genderOld.equals("Male")){
                        userUpdateProfileActivityRadioButtonGenderSelected = findViewById(R.id.userUpdateProfileActivityRadioButtonMale);
                    } else {
                        userUpdateProfileActivityRadioButtonGenderSelected = findViewById(R.id.userUpdateProfileActivityRadioButtonFemale);
                    }
                    userUpdateProfileActivityRadioButtonGenderSelected.setChecked(true);
                    initialRadioButtonState = userUpdateProfileActivityRadioButtonGenderSelected.isChecked();
                    userUpdateProfileActivityRadioButtonGenderSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            // Check if the state of the RadioButton has changed
                            if (isChecked != initialRadioButtonState) {
                                changesMadeGender = true;
                            }
                        }
                    });

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

    //Creating ActionBar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //When any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.commonMenuItemContactAdmin:
                openEmailAppChooser();
                break;
            case R.id.commonMenuItemLogout:
                authProfile = FirebaseAuth.getInstance();
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);//So when phone is opened, user will be redirected to the MainFragment instead of HomePageActivity
                editor.apply();
                Intent intent = new Intent(UserUpdateProfileActivity.this, MainActivity.class);

                //Don't let user go back to HomePageActivity or any activities related opened after logout button was tapped if back button of phone is tapped
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                authProfile.signOut();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openEmailAppChooser() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"borjy46@gmail.com"}); // Replace with admin's email address
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact Admin");

        // Check if there's an app that can handle the email intent
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, "Choose an email client:"));
        } else {
            // If no email app is available, open browser to Gmail website
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com/"));
            startActivity(browserIntent);
        }
    }

    public void onBackPressed() {
        if (changesMadeFullName == true || changesMadeBirthdate == true || changesMadeGender == true || changesMadeMobileNumber == true) {
            // Changes have been made, show confirmation dialog
            showExitConfirmationDialog();
        }
        else {
            // No changes, perform default back action
            Intent intent = new Intent(UserUpdateProfileActivity.this, HomeActivity.class);
            intent.putExtra("openedFrom", "UserProfileFragment");
            startActivity(intent);
        }
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_exit, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        Button yesButton = dialogView.findViewById(R.id.dialog_button_yes);
        Button noButton = dialogView.findViewById(R.id.dialog_button_no);

        yesButton.setOnClickListener(view -> {
            // User confirmed, exit the activity
            finish();
        });

        noButton.setOnClickListener(view -> {
            // User canceled, dismiss the dialog
            dialog.dismiss();
        });

        dialog.show();
    }
}