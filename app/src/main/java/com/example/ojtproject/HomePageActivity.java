package com.example.ojtproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HomePageActivity extends AppCompatActivity {

    private Button homePageActivityButtonLogout,
                   homePageActivityButtonClockIn,
                   homePageActivityButtonViewRecords;
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

//        // In your main activity's onCreate or another appropriate location
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(HomePageActivity.this, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(HomePageActivity.this, 0, intent, PendingIntent.FLAG_MUTABLE);
//
//        //Set the alarm to trigger every few minutes (adjust as needed)
//        long intervalMillis = 5000; // Every 30 minutes
//        long initialMillis = SystemClock.elapsedRealtime() + intervalMillis;
//        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, initialMillis, intervalMillis, pendingIntent);

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

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Attendance").child(uid);

                        // Get the current date and time from the TextClocks
                        String currentTime = homePageActivityTextClockActualClock.getText().toString();
                        String currentDate = homePageActivityTextClockActualDate.getText().toString();
                        String currentDateAndTime = new String(currentDate + " - " + currentTime);

                        // Get the current date and time
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("lastTappedDate", currentDate);
                        editor.putString("lastTappedTime", currentTime);
                        editor.apply();

                        String[] currentDateParts = currentDate.split(",");
                        String currentDayOfWeek = currentDateParts[0];


                        databaseReference.orderByChild("dateDay").equalTo(currentDate).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // A record for today already exists, prevent clock-in
                                    Toast.makeText(HomePageActivity.this, "You already have a status for your clock-in today.", Toast.LENGTH_SHORT).show();
                                } else {
                                    try {


                                        // Create a new TimeRecord object
//                            ReadWriteUserTimeDetails readWriteUserTimeDetails = new ReadWriteUserTimeDetails();
//                            readWriteUserTimeDetails.setDateTime(currentDateAndTime);

                                        SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
                                        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                                        Date dateTime = inputFormat.parse(currentTime);
                                        String militaryTimeFormat = outputFormat.format(dateTime);

                                        ReadWriteUserTimeDetails readWriteUserTimeDetails = new ReadWriteUserTimeDetails();
                                        readWriteUserTimeDetails.setDateTime(currentTime);
                                        readWriteUserTimeDetails.setDateDay(currentDate);

                                        // Split the time string into hours, minutes, and seconds
                                        String[] currentTimeParts = militaryTimeFormat.split(":");
                                        int currentHour = Integer.parseInt(currentTimeParts[0]);
                                        int currentMinute = Integer.parseInt(currentTimeParts[1]);
                                        int currentSecond = Integer.parseInt(currentTimeParts[2]);

                                        String[] currentDateParts = currentDate.split(",");
                                        String currentDayOfWeek = currentDateParts[0];

                                        System.out.println(currentHour + ":" + currentMinute + ":" + currentSecond);

                                        String status;
                                        if(currentDayOfWeek.equals("Thursday")){
                                            if ((currentHour > 23 || (currentHour == 23 && currentMinute > 40) || (currentHour == 23 && currentMinute == 40 && currentSecond > 0))){
                                                Toast.makeText(HomePageActivity.this, "Attendance not yet available. Please wait until the designated '\"'on-time'\"' start time of 8:45 AM - 9 to mark your attendance.", Toast.LENGTH_SHORT).show();
                                            } else if ((currentHour > 23 || (currentHour == 23 && currentMinute > 12) || (currentHour == 23 && currentMinute == 12 && currentSecond > 0))) {
                                                status = "Absent";
                                                readWriteUserTimeDetails.setStatus(status);
                                                // Write the time record to the Firebase Realtime Database under the "attendance" node with the user's UID as the key
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                                databaseReference.child("Attendance").child(uid).push().setValue(readWriteUserTimeDetails);
                                                Log.d("Military Time", militaryTimeFormat);
                                                Toast.makeText(HomePageActivity.this, "You are marked absent for today!", Toast.LENGTH_SHORT).show();
                                            } else if (currentHour > 15 || (currentHour == 15 && currentMinute > 2) || (currentHour == 15 && currentMinute == 2 && currentSecond > 0)) {
                                                status = "Late";
                                                readWriteUserTimeDetails.setStatus(status);
                                                // Write the time record to the Firebase Realtime Database under the "attendance" node with the user's UID as the key
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                                databaseReference.child("Attendance").child(uid).push().setValue(readWriteUserTimeDetails);
                                                Log.d("Military Time", militaryTimeFormat);
                                                Toast.makeText(HomePageActivity.this, "You are late!", Toast.LENGTH_SHORT).show();
                                            } else if (currentHour > 8 || (currentHour == 8 && currentMinute > 45) || (currentHour == 8 && currentMinute == 45 && currentSecond >= 0)){
                                                status = "On-time";
                                                readWriteUserTimeDetails.setStatus(status);
                                                // Write the time record to the Firebase Realtime Database under the "attendance" node with the user's UID as the key
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                                databaseReference.child("Attendance").child(uid).push().setValue(readWriteUserTimeDetails);
                                                Log.d("Military Time", militaryTimeFormat);
                                                Toast.makeText(HomePageActivity.this, "Congratulations! You made it on time!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HomePageActivity.this, "Attendance not yet available. Please wait until the designated '\"'on-time'\"' start time of 8:45 AM - 9 to mark your attendance.", Toast.LENGTH_SHORT).show();
                                            }



                                        } else {
                                            Toast.makeText(HomePageActivity.this, "Get some rest. There is no work today!", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    } catch (java.text.ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });




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



        homePageActivityButtonViewRecords = findViewById(R.id.homePageActivityButtonViewRecords);
        homePageActivityButtonViewRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, UserAttendanceRecordsActivity.class);
                startActivity(intent);
//                finish();
            }
        });

        // Calculate the time difference between the current time and the desired trigger time
        // For example, let's trigger at 2:31 AM
        int desiredHour = 2;
        int desiredMinute = 37;

        // Get the current time in the desired timezone
        android.icu.util.Calendar currentTime = android.icu.util.Calendar.getInstance(android.icu.util.TimeZone.getTimeZone("Asia/Singapore"));

        long timeDifferenceMillis = calculateTimeDifference(currentTime, desiredHour, desiredMinute);
        setupAlarm(timeDifferenceMillis);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupAlarm(long timeDifferenceMillis) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        // Set up the AlarmManager to trigger at the calculated time
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeDifferenceMillis, pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private long calculateTimeDifference(android.icu.util.Calendar currentTime, int desiredHour, int desiredMinute) {
        android.icu.util.Calendar triggerTime = (android.icu.util.Calendar) currentTime.clone();
        triggerTime.set(android.icu.util.Calendar.HOUR_OF_DAY, desiredHour);
        triggerTime.set(android.icu.util.Calendar.MINUTE, desiredMinute);
        triggerTime.set(android.icu.util.Calendar.SECOND, 0);
        triggerTime.set(android.icu.util.Calendar.MILLISECOND, 0);

        return triggerTime.getTimeInMillis() - currentTime.getTimeInMillis();
    }

}