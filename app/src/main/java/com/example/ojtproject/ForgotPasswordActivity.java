package com.example.ojtproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

    //Global variables
    private Button forgotPasswordActivityButtonPasswordReset;
    private EditText forgotPasswordActivityEditTextPasswordResetEmail;
    private ProgressBar forgotPasswordActivityProgressBar;
    private FirebaseAuth authProfile;
    private final static String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Activity title initialization
        getSupportActionBar().setTitle("Forgot Password");

        forgotPasswordActivityEditTextPasswordResetEmail = findViewById(R.id.forgotPasswordActivityEditTextPasswordResetEmail);
        forgotPasswordActivityButtonPasswordReset = findViewById(R.id.forgotPasswordActivityButtonPasswordReset);
        forgotPasswordActivityProgressBar = findViewById(R.id.forgotPasswordActivityProgressBar);

        forgotPasswordActivityButtonPasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = forgotPasswordActivityEditTextPasswordResetEmail.getText().toString();

                //Validations when password reset is tapped
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                    forgotPasswordActivityEditTextPasswordResetEmail.setError("Email is required");
                    forgotPasswordActivityEditTextPasswordResetEmail.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                    forgotPasswordActivityEditTextPasswordResetEmail.setError("Valid email is required");
                    forgotPasswordActivityEditTextPasswordResetEmail.requestFocus();
                } else {
                    forgotPasswordActivityProgressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });
    }

    //Reset password method when validations are met
    private void resetPassword(String email) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your inbox", Toast.LENGTH_SHORT).show();
                    //Clear stack to prevent user coming back to ForgotPasswordActivity
                    Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); //Close ForgetPasswordActivity
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        forgotPasswordActivityEditTextPasswordResetEmail.setError("Email does not exist or is no longer. Please register again.");
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                forgotPasswordActivityProgressBar.setVisibility(View.GONE);
            }
        });
    }
}