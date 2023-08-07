package com.example.ojtproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HomePageActivity extends AppCompatActivity {

    private Button homePageActivityButtonLogout,
                   homePageActivityButtonClockIn;
    private TextClock homePageActivityTextClockActualDate;
    private TextClock homePageActivityTextClockActualClock;
    private TextView homePageActivityTextViewActualTime;
    private FirebaseAuth authProfile;
    private FirebaseUser currentUser;
    private static final String TAG= "RegisterActivity";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        setContentView(R.layout.activity_home_page);

        authProfile = FirebaseAuth.getInstance();
        homePageActivityButtonLogout = findViewById(R.id.homePageActivityButtonLogout);

        homePageActivityButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                authProfile.signOut();
                finish();
            }
        });


//            Calendar calendar = Calendar.getInstance();
//            String currentDate = DateFormat.getDateInstance(android.icu.text.DateFormat.FULL).format(calendar.getTime());
//            homePageActivityTextViewActualTime = findViewById(R.id.homePageActivityTextViewActualDate);
//            homePageActivityTextViewActualTime.setText(currentDate);

        homePageActivityTextClockActualClock = findViewById(R.id.homePageActivityTextClockActualClock);
        homePageActivityTextClockActualDate = findViewById(R.id.homePageActivityTextClockActualDate);
        homePageActivityButtonClockIn = findViewById(R.id.homePageActivityButtonClockIn);
        homePageActivityButtonClockIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser = authProfile.getCurrentUser();
//                if(currentUser != null) {
//                    String uid = currentUser.getUid();
//
//                    //Get the current date and time in Singapore time zone
//                    TimeZone singaporeTimeZone = TimeZone.getTimeZone("Asia/Singapore");
//                    Calendar calendar = Calendar.getInstance(singaporeTimeZone);
//                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy - HH:mm:ss a", Locale.getDefault());
//                    String currentDateTime = dateTimeFormat.format(calendar.getTime());
//
//                    calendar.set(Calendar.HOUR_OF_DAY, 0); //Set the cut-off hour to 10 PM (22:00)
//                    calendar.set(Calendar.MINUTE, 07);
//                    calendar.set(Calendar.SECOND, 0);
//
//                    String status;
//                    if (calendar.getTimeInMillis() < System.currentTimeMillis()){
//                        //The current time is beyond 10 PM Singapore time, so the person is late
//                        status = "Late";
//                        Toast.makeText(HomePageActivity.this, "You are late!", Toast.LENGTH_SHORT).show();
//                    } else{
//                        //The person is on-time
//                        status = "On-time";
//                        Toast.makeText(HomePageActivity.this, "Congratulations! You made it on time!", Toast.LENGTH_SHORT).show();
//                    }
//
//                    //Create a new ReadWriteUserTimeDetails object for time record of user
//                    ReadWriteUserTimeDetails readWriteUserTimeDetails = new ReadWriteUserTimeDetails(currentDateTime, status);
//
//                    //Write the current date and time to the Firebase Realtime Database under the "attendance" node with the user's UID as the key
//                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//                    databaseReference.child("Attendance").child(uid).push().setValue(readWriteUserTimeDetails);
//                } else {
//                    Toast.makeText(HomePageActivity.this, "Please login to continue.", Toast.LENGTH_SHORT).show();
//                }



                    if (currentUser != null) {
                        String uid = currentUser.getUid();



                        try {
                            // Get the current date and time from the TextClocks
                            String currentTime = homePageActivityTextClockActualClock.getText().toString();
                            String currentDate = homePageActivityTextClockActualDate.getText().toString();
                            String currentDateAndTime = new String(currentDate + " - " + currentTime);

                            // Create a new TimeRecord object
//                            ReadWriteUserTimeDetails readWriteUserTimeDetails = new ReadWriteUserTimeDetails();
//                            readWriteUserTimeDetails.setDateTime(currentDateAndTime);

                            SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                            Date dateTime = inputFormat.parse(currentTime);
                            String militaryTimeFormat = outputFormat.format(dateTime);

                            ReadWriteUserTimeDetails readWriteUserTimeDetails = new ReadWriteUserTimeDetails();
                            readWriteUserTimeDetails.setDateTime(currentDateAndTime);

                            // Split the time string into hours, minutes, and seconds
                            String[] timeParts = militaryTimeFormat.split(":");
                            int currentHour = Integer.parseInt(timeParts[0]);
                            int currentMinute = Integer.parseInt(timeParts[1]);
                            int currentSecond = Integer.parseInt(timeParts[2]);

                            System.out.println(currentHour + ":" + currentMinute + ":" + currentSecond);

                            String status;
                            if (currentHour > 15 || (currentHour == 15 && currentMinute > 2) || (currentHour == 15 && currentMinute == 2 && currentSecond > 0)) {
                                status = "Late";
                                Toast.makeText(HomePageActivity.this, "You are late!", Toast.LENGTH_SHORT).show();
                            } else {
                                status = "On-time";
                                Toast.makeText(HomePageActivity.this, "Congratulations! You made it on time!", Toast.LENGTH_SHORT).show();
                            }
                            readWriteUserTimeDetails.setStatus(status);

                            // Write the time record to the Firebase Realtime Database under the "attendance" node with the user's UID as the key
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Attendance").child(uid).push().setValue(readWriteUserTimeDetails);
                            Log.d("Military Time", militaryTimeFormat);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }


                    } else {
                        // User is not logged in, handle the situation accordingly
                        Toast.makeText(HomePageActivity.this, "Please login to continue.", Toast.LENGTH_SHORT).show();
                        //Open Login Activity after successful registration
                        Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                        //To Prevent User from returning back to Home Page Activity on pressing back button after registration
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();//to close Register Activity
                    }


            }
        });




    }
}