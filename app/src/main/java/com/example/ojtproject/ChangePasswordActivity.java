package com.example.ojtproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText changePasswordActivityEditTextCurrentPassword,
                     changePasswordActivityEditTextNewPassword,
                     changePasswordActivityEditTextConfirmPassword;
    private Button changePasswordActivityButtonAuthenticate,
                   changePasswordActivityButtonConfirmPassword,
                   changePasswordActivityButtonCancel;
    private TextView changePasswordActivityTextViewUpdateEmailAuthenticated;
    private String userCurrentPassword;
    private Boolean changesMadeCurrentPassword = false,
                    changesMadeAuthentication = false;
    private ProgressBar changePasswordActivityProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changePasswordActivityEditTextCurrentPassword = findViewById(R.id.changePasswordActivityEditTextCurrentPassword);
        changePasswordActivityEditTextCurrentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!charSequence.toString().isEmpty()) {
                    changesMadeCurrentPassword = true;
                } else {
                    changesMadeCurrentPassword = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        changePasswordActivityEditTextNewPassword = findViewById(R.id.changePasswordActivityEditTextNewPassword);
        changePasswordActivityEditTextConfirmPassword = findViewById(R.id.changePasswordActivityEditTextConfirmPassword);
        changePasswordActivityButtonAuthenticate = findViewById(R.id.changePasswordActivityButtonAuthenticate);
        if(!changePasswordActivityButtonAuthenticate.isEnabled()){
            changesMadeAuthentication = true;
        } else {
            changesMadeAuthentication = false;
        }
        changePasswordActivityButtonConfirmPassword = findViewById(R.id.changePasswordActivityButtonConfirmPassword);
        changePasswordActivityButtonCancel = findViewById(R.id.changePasswordActivityButtonCancel);
        changePasswordActivityTextViewUpdateEmailAuthenticated = findViewById(R.id.changePasswordActivityTextViewUpdateEmailAuthenticated);
        changePasswordActivityProgressBar = findViewById(R.id.changePasswordActivityProgressBar);

        //Disable editText for New Password, Confirm New Password and Make Change Password Button inactive until
        //user is authenticated
        changePasswordActivityEditTextNewPassword.setEnabled(false);
        changePasswordActivityEditTextConfirmPassword.setEnabled(false);
        changePasswordActivityButtonConfirmPassword.setEnabled(false);
        changePasswordActivityButtonCancel.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(this, "Something went wrong! User's details not available", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, HomeActivity.class);
            intent.putExtra("loadFragment", 1);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }
    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        changePasswordActivityButtonAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userCurrentPassword = changePasswordActivityEditTextCurrentPassword.getText().toString();

                if(TextUtils.isEmpty(userCurrentPassword)){
                    Toast.makeText(ChangePasswordActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                    changePasswordActivityEditTextCurrentPassword.setError("Please enter your current password to authenticate");
                    changePasswordActivityEditTextCurrentPassword.requestFocus();
                } else {
                    changePasswordActivityProgressBar.setVisibility(View.VISIBLE);

                    //ReAuthenticate User now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userCurrentPassword);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                changePasswordActivityProgressBar.setVisibility(View.GONE);

                                //Disable editText for Current Password. Enable EditText for New Password and Confirm New Password
                                changePasswordActivityEditTextCurrentPassword.setText("");
                                changePasswordActivityEditTextCurrentPassword.setEnabled(false);
                                changePasswordActivityEditTextNewPassword.setEnabled(true);
                                changePasswordActivityEditTextConfirmPassword.setEnabled(true);
                                changePasswordActivityButtonCancel.setEnabled(true);

                                //Enable change password button and disable authenticate button
                                changePasswordActivityButtonAuthenticate.setEnabled(false);
                                if(!changePasswordActivityButtonAuthenticate.isEnabled()){
                                    changesMadeAuthentication = true;
                                } else {
                                    changesMadeAuthentication = false;
                                }
                                changePasswordActivityButtonConfirmPassword.setEnabled(true);

                                //Set TextView to show User is authenticated/verified
                                changePasswordActivityTextViewUpdateEmailAuthenticated.setText("You are authenticated/verified." +
                                        "You can change your password now!");
                                Toast.makeText(ChangePasswordActivity.this, "Password has been verified." +
                                        "Change your password now.", Toast.LENGTH_SHORT).show();
                                //Change color of change password button
                                changePasswordActivityButtonConfirmPassword.setBackgroundTintList(ContextCompat.getColorStateList(
                                        ChangePasswordActivity.this, R.color.dark_green));
                                changePasswordActivityButtonConfirmPassword.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePassword(firebaseUser);
                                    }
                                });
                                changePasswordActivityButtonCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onBackPressed();
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e){
                                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            changePasswordActivityProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePassword(FirebaseUser firebaseUser){
        String userNewPassword = changePasswordActivityEditTextNewPassword.getText().toString();
        String userNewConfirmPassword = changePasswordActivityEditTextConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(userNewPassword)){
            Toast.makeText(this, "New Password is needed!", Toast.LENGTH_SHORT).show();
            changePasswordActivityEditTextNewPassword.setError("Please enter your new password!");
            changePasswordActivityEditTextNewPassword.requestFocus();
        } else if (TextUtils.isEmpty(userNewConfirmPassword)){
            Toast.makeText(this, "Please confirm your new password!", Toast.LENGTH_SHORT).show();
            changePasswordActivityEditTextConfirmPassword.setError("Please re-enter your new password!");
            changePasswordActivityEditTextConfirmPassword.requestFocus();
        } else if (!userNewPassword.matches(userNewConfirmPassword)) {
            Toast.makeText(this, "Password did not match!", Toast.LENGTH_SHORT).show();
            changePasswordActivityEditTextConfirmPassword.setError("Please make sure confirm and new password fields have the same value!");
            changePasswordActivityEditTextConfirmPassword.requestFocus();
        } else if (userCurrentPassword.matches(userNewPassword)){
            Toast.makeText(this, "New Password cannot be the same as the old password!", Toast.LENGTH_SHORT).show();
            changePasswordActivityEditTextNewPassword.setError("Please enter your new password!");
            changePasswordActivityEditTextNewPassword.requestFocus();
        } else {
            changePasswordActivityProgressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChangePasswordActivity.this, "Password has been changed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePasswordActivity.this, HomeActivity.class);
                        intent.putExtra("loadFragment", 1);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        changePasswordActivityProgressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
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

            case R.id.commonMenuItemContactAdmin:
                openEmailAppChooser();
                break;
            case R.id.commonMenuItemLogout:
                authProfile = FirebaseAuth.getInstance();
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);//So when phone is opened, user will be redirected to the MainFragment instead of HomePageActivity
                editor.apply();
                Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);

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
        if (changesMadeCurrentPassword == true || changesMadeAuthentication == true) {
            // Changes have been made, show confirmation dialog
            showExitConfirmationDialog();
        }
        else {
            // No changes, perform default back action
            Intent intent = new Intent(ChangePasswordActivity.this, UserUpdateProfileActivity.class);
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