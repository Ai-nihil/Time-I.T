package com.example.ojtproject;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText loginActivityEditTextEmail,loginActivityEditTextPassword;
    private ProgressBar loginActivityProgressBar;
    private FirebaseAuth authProfile;
    private FirebaseDatabase database;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");

        loginActivityEditTextEmail = findViewById(R.id.loginActivityEditTextEmail);
        loginActivityEditTextPassword = findViewById(R.id.loginActivityEditTextPassword);
        loginActivityProgressBar = findViewById(R.id.loginActivityProgressBar);

        authProfile = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //Reset Password
        TextView loginActivityTextViewForgotPassword = findViewById(R.id.loginActivityTextViewForgotPassword);
        loginActivityTextViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "You can reset your password now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        //Hide password by default
        loginActivityEditTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        //Show Hide Password using Eye Icon
        ImageView loginActivityImageViewShowHidePassword = findViewById(R.id.loginActivityImageViewShowHidePassword);
        loginActivityImageViewShowHidePassword.setImageResource(R.drawable.ic_show_password);
        loginActivityImageViewShowHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginActivityEditTextPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If password is not visible show it
                    loginActivityEditTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Change Icon
                    loginActivityImageViewShowHidePassword.setImageResource(R.drawable.ic_show_password);
                } else {
                    //If password is visible hide it
                    loginActivityEditTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    //Change Icon
                    loginActivityImageViewShowHidePassword.setImageResource(R.drawable.ic_hide_password);
                }
            }
        });

        //Login User
        Button loginActivityButtonLogin = findViewById(R.id.loginActivityButtonLogin);
        loginActivityButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = loginActivityEditTextEmail.getText().toString();
                String textPassword = loginActivityEditTextPassword.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    loginActivityEditTextEmail.setError("Email is required");
                    loginActivityEditTextEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(LoginActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    loginActivityEditTextEmail.setError("Valid email is required");
                    loginActivityEditTextEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)){
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    loginActivityEditTextPassword.setError("Password is required");
                    loginActivityEditTextPassword.requestFocus();
                } else {
                    loginActivityProgressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPassword);
                }
            }
        });
    }

    private void loginUser(String email, String password){
        authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Get instance of the current User
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    String userId = firebaseUser.getUid();

                    //Check if email is verified before user can access their profile
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(LoginActivity.this, "You are logged in now", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();
                    } else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut(); //Sign out user
                        showAlertDialog();
                    }

                    if (firebaseUser != null) {
                        DatabaseReference userRef = database.getReference().child("Registered Users").child(userId);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Intent intent;
                                    String userType = snapshot.child("userType").getValue(String.class);
                                    assert userType != null;
                                    // Identifies if the person loggin in is an admin or a user
                                    switch (userType.toString().toLowerCase(Locale.ROOT)) {
                                        case "admin":
                                            intent = new Intent(LoginActivity.this, AdminView.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            break;
                                        case "user":
                                            intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            break;
                                        default:
                                            Toast.makeText(LoginActivity.this, userType, Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "onCancelled: ", error.toException());
                                Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        loginActivityEditTextEmail.setError("User does not exist or is no longer valid. Please register again.");
                        loginActivityEditTextEmail.requestFocus();
                    } catch(FirebaseAuthInvalidCredentialsException e){
                        loginActivityEditTextEmail.setError("Invalid credentials. Kindly, check and re-enter.");
                        loginActivityEditTextEmail.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                loginActivityProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        //Setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You cannot login without your email verification.");

        //Open Email Apps if User clicks/taps Continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //To email app in new window and not within the app
                startActivity(intent);

            }
        });

        //Cancel dialog box
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        //Show the AlertDialog
        alertDialog.show();
    }

// check if User is already logged in. Also, used to send admins and users to their respective pages
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(authProfile.getCurrentUser() != null){
//            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        } else{
//            Toast.makeText(LoginActivity.this, "You can log in now!", Toast.LENGTH_SHORT).show();
//        }
//    }
}