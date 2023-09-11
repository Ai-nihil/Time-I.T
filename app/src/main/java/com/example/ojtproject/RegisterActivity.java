package com.example.ojtproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerActivityEditTextFullName,
                     registerActivityEditTextEmail,
                     registerActivityEditTextBirthdate,
                     registerActivityEditTextMobileNumber,
                     registerActivityEditTextPassword,
                     registerActivityEditTextConfirmPassword;
    private ProgressBar registerActivityProgressBar;
    private RadioGroup registerActivityRadioGroupGender;
    private RadioButton registerActivityRadioButtonGenderSelected;
    private DatePickerDialog picker;
    private Boolean changesMadeFullName = false,
                    changesMadeBirthdate = false,
                    changesMadeMobileNumber = false,
                    changesMadeGender = false,
                    changesMadeEmail = false,
                    changesMadePassword = false,
                    changesMadeConfirmPassword = false;
    private Boolean registerActivityRadioButtonMaleIsChecked = false,
                    registerActivityRadioButtonFemaleIsChecked = false;
    private static final String TAG= "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toast.makeText(RegisterActivity.this,"You can register now", Toast.LENGTH_LONG).show();

        registerActivityProgressBar = findViewById(R.id.registerActivityProgressBar);
        registerActivityEditTextFullName = findViewById(R.id.registerActivityEditTextFullName);
        registerActivityEditTextFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Check if the text has changed
                if (!charSequence.toString().isEmpty()) {
                    changesMadeFullName = true;
                } else {
                    changesMadeFullName = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        registerActivityEditTextEmail = findViewById(R.id.registerActivityEditTextEmail);
        registerActivityEditTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Check if the text has changed
                if (!charSequence.toString().isEmpty()) {
                    changesMadeEmail = true;
                } else {
                    changesMadeEmail = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        registerActivityEditTextBirthdate = findViewById(R.id.registerActivityEditTextBirthdate);
        registerActivityEditTextBirthdate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Check if the text has changed
                if (!charSequence.toString().isEmpty()) {
                    changesMadeBirthdate = true;
                } else {
                    changesMadeBirthdate = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        registerActivityEditTextMobileNumber = findViewById(R.id.registerActivityEditTextMobileNumber);
        registerActivityEditTextMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Check if the text has changed
                if (!charSequence.toString().isEmpty()) {
                    changesMadeMobileNumber = true;
                } else {
                    changesMadeMobileNumber = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        registerActivityEditTextPassword = findViewById(R.id.registerActivityEditTextPassword);
        registerActivityEditTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Check if the text has changed
                if (!charSequence.toString().isEmpty()) {
                    changesMadePassword = true;
                } else {
                    changesMadePassword = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        registerActivityEditTextConfirmPassword = findViewById(R.id.registerActivityEditTextConfirmPassword);
        registerActivityEditTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Check if the text has changed
                if (!charSequence.toString().isEmpty()) {
                    changesMadeConfirmPassword = true;
                } else {
                    changesMadeConfirmPassword = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //RadioButton for Gender
        registerActivityRadioGroupGender = findViewById(R.id.registerActivityRadioGroupGender);
        registerActivityRadioGroupGender.clearCheck();

        // Set a listener for the RadioGroup to track changes
        registerActivityRadioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Check which RadioButton is selected
                if (checkedId == R.id.registerActivityRadioButtonMale) {
                    registerActivityRadioButtonMaleIsChecked = true;
                    registerActivityRadioButtonFemaleIsChecked = false;
                } else if (checkedId == R.id.registerActivityRadioButtonFemale) {
                    registerActivityRadioButtonFemaleIsChecked = true;
                    registerActivityRadioButtonMaleIsChecked = false;
                }

                if(registerActivityRadioButtonMaleIsChecked != false || registerActivityRadioButtonFemaleIsChecked != false){
                    changesMadeGender = true;
                }
            }
        });



        //Setting up DatePicker on EditText
        registerActivityEditTextBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //Date Picker Dialog
                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        registerActivityEditTextBirthdate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button registerActivityButtonRegister = findViewById(R.id.registerActivityButtonRegister);
        registerActivityButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedGenderId = registerActivityRadioGroupGender.getCheckedRadioButtonId();
                registerActivityRadioButtonGenderSelected = findViewById(selectedGenderId);

                //Obtain the entered data
                String textFullName = registerActivityEditTextFullName.getText().toString();
                String textEmail = registerActivityEditTextEmail.getText().toString();
                String textBirthdate = registerActivityEditTextBirthdate.getText().toString();
                String textMobileNumber = registerActivityEditTextMobileNumber.getText().toString();
                String textPassword = registerActivityEditTextPassword.getText().toString();
                String textConfirmPassword = registerActivityEditTextConfirmPassword.getText().toString();
                String textGender; // Define 1st since it can't obtain the value before verifying if a button was selected or not

                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(RegisterActivity.this,"Please enter your full name", Toast.LENGTH_LONG).show();
                    registerActivityEditTextFullName.setError("Full Name is required");
                    registerActivityEditTextFullName.requestFocus();
                } else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(RegisterActivity.this,"Please enter your email", Toast.LENGTH_LONG).show();
                    registerActivityEditTextEmail.setError("Email is required");
                    registerActivityEditTextEmail.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(RegisterActivity.this,"Please re-enter your email", Toast.LENGTH_LONG).show();
                    registerActivityEditTextEmail.setError("Valid Email is required");
                    registerActivityEditTextEmail.requestFocus();
                } else if(TextUtils.isEmpty(textBirthdate)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your birthdate", Toast.LENGTH_LONG).show();
                    registerActivityEditTextBirthdate.setError("Birthdate is required");
                    registerActivityEditTextBirthdate.requestFocus();
                } else if(registerActivityRadioGroupGender.getCheckedRadioButtonId() == -1){
                    Toast.makeText(RegisterActivity.this,"Please select your gender", Toast.LENGTH_LONG).show();
                    registerActivityRadioButtonGenderSelected.setError("Gender is required");
                    registerActivityRadioButtonGenderSelected.requestFocus();
                } else if(TextUtils.isEmpty(textMobileNumber)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your mobile number", Toast.LENGTH_LONG).show();
                    registerActivityEditTextMobileNumber.setError("Mobile Number is required");
                    registerActivityEditTextMobileNumber.requestFocus();
                } else if(textMobileNumber.length() != 11) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your mobile number", Toast.LENGTH_LONG).show();
                    registerActivityEditTextMobileNumber.setError("Mobile Number should be 11 digits");
                    registerActivityEditTextMobileNumber.requestFocus();
                } else if(TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    registerActivityEditTextPassword.setError("Password is required");
                    registerActivityEditTextPassword.requestFocus();
                } else if(textPassword.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "Password should be at least 8 characters", Toast.LENGTH_LONG).show();
                    registerActivityEditTextPassword.setError("Password too weak");
                    registerActivityEditTextPassword.requestFocus();
                } else if(TextUtils.isEmpty(textConfirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                    registerActivityEditTextConfirmPassword.setError("Password confirmation is required");
                    registerActivityEditTextConfirmPassword.requestFocus();
                } else if(!textConfirmPassword.equals(textPassword)) {
                    Toast.makeText(RegisterActivity.this, "Password entries do not match", Toast.LENGTH_LONG).show();
                    registerActivityEditTextConfirmPassword.setError("Password confirmation is required");
                    registerActivityEditTextConfirmPassword.requestFocus();
                    //Clear the entered password
                    registerActivityEditTextPassword.clearComposingText();
                    registerActivityEditTextConfirmPassword.clearComposingText();
                } else {
                    textGender = registerActivityRadioButtonGenderSelected.getText().toString();
                    registerActivityProgressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textBirthdate, textGender, textMobileNumber, textPassword, textConfirmPassword);
                }
            }
        });
    }

    //Register User using the credentials given
    private void registerUser(String textFullName, String textEmail, String textBirthdate, String textGender, String textMobileNumber, String textPassword, String textConfirmPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //Create User Profile
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            //Update Display Name of User
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            //Enter User Data into the Firebase Realtime Database.
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textBirthdate, textGender, textMobileNumber);

                            //Extracting User reference from Database for "Registered Users"
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                            referenceProfile.child(firebaseUser.getUid()).child("userType").get().addOnCompleteListener(recordedUserType -> {
                                if(recordedUserType.isSuccessful()) {
                                    if(recordedUserType.getResult().getValue() == null) {
                                        referenceProfile.child(firebaseUser.getUid()).child("userType").setValue("user");
                                    }
                                }
                            });

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        //Send Verification Email
                                        firebaseUser.sendEmailVerification();

                                        Toast.makeText(RegisterActivity.this,"User registered successfully. Please verify your email", Toast.LENGTH_LONG).show();

                                    } else {
                                        Toast.makeText(RegisterActivity.this,"User registered failed. Please try again", Toast.LENGTH_LONG).show();

                                    }
                                    //Hide Progress Bar whether User creation is successful or failed
                                    registerActivityProgressBar.setVisibility(View.GONE);

                                }
                            });


                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                registerActivityEditTextPassword.setError("Your password is too weak. Kindly use a mix of alphabets, numbers, and special characters.");
                                registerActivityEditTextPassword.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                registerActivityEditTextEmail.setError("User is already registered with this email. Please use another email.");
                                registerActivityEditTextEmail.requestFocus();
                                // Set the delay time in milliseconds (5 seconds in this case)
                                int delayTimeMillis = 5000; // 5000 milliseconds = 5 seconds

                                // Create a Handler to post a Runnable with a delay
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Remove the error message after the delay time
                                        registerActivityEditTextEmail.setError(null);
                                    }
                                }, delayTimeMillis);
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                registerActivityEditTextEmail.requestFocus();

                                // Set the delay time in milliseconds (5 seconds in this case)
                                int delayTimeMillis = 5000; // 5000 milliseconds = 5 seconds

                                // Create a Handler to post a Runnable with a delay
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Remove the error message after the delay time
                                        registerActivityEditTextEmail.setError(null);
                                    }
                                }, delayTimeMillis);
                            }
                        }
                        //Hide Progress Bar whether User creation is successful or failed
                        registerActivityProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    //When any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        if (changesMadeFullName == true || changesMadeBirthdate == true || changesMadeEmail == true || changesMadeGender == true || changesMadeMobileNumber == true || changesMadePassword == true || changesMadeConfirmPassword == true) {
            // Changes have been made, show confirmation dialog
            showExitConfirmationDialog();
        }
        else {
            // No changes, perform default back action
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
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