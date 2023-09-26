package com.example.ojtproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if the user is logged in
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        String userType = sharedPreferences.getString("userRole", null);

        // If logged in, redirect to HomePageActivity
        if (isLoggedIn) {
            if(userType.equals("admin")) {
                Intent intent = new Intent(MainActivity.this, AdminView.class);
                startActivity(intent);
                finish(); // Optional: To prevent the user from coming back to the MainActivity using the back button
            } else {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Optional: To prevent the user from coming back to the MainActivity using the back button
            }
        }
        setContentView(R.layout.activity_main);

        //Set the title
        getSupportActionBar().setTitle("IT Time");

        //Open Login Activity
        Button mainActivityButtonLogin = findViewById(R.id.mainActivityButtonLogin);
        mainActivityButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //Open Register Activity
        TextView mainActivityTextViewRegister = findViewById(R.id.mainActivityTextViewRegister);
        mainActivityTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}