package com.example.ojtproject;


import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AlarmReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String storedClockInDate = sharedPreferences.getString("lastTappedClockInDate", "");
        String storedClockInTime = sharedPreferences.getString("lastTappedClockInTime", "");
        String storedClockInStatus = sharedPreferences.getString("lastTappedClockInStatus", "");
        Boolean storedClockInButtonTapped = sharedPreferences.getBoolean("clockInButtonTapped", false);
        System.out.println(storedClockInButtonTapped);

        Calendar currentCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String currentDate = dateFormat.format(currentCalendar.getTime());

        String[] currentDateParts = currentDate.split(",");
        String currentDayOfWeek = currentDateParts[0];

        currentCalendar = Calendar.getInstance();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String currentTime = dateTimeFormat.format(currentCalendar.getTime());

        String[] currentTimeParts = currentTime.split(":");
        int currentHour = Integer.parseInt(currentTimeParts[0]);
        int currentMinute = Integer.parseInt(currentTimeParts[1]);
        int currentSecond = Integer.parseInt(currentTimeParts[2]);

        System.out.println(storedClockInDate);
        System.out.println(storedClockInTime);

        // Check if the user tapped the button for today
        if (!currentDayOfWeek.equals("Saturday") && !currentDayOfWeek.equals("Sunday")) {
            if (currentHour >= 18 && currentMinute >= 30) {
                if (storedClockInButtonTapped == false) {
                    // Update status to "Absent" for today
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (currentUser != null) {
                        String uid = currentUser.getUid();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                        DatabaseReference userAttendanceRef = databaseReference.child("Attendance").child(uid);

                        userAttendanceRef.orderByChild("dateDay").equalTo(currentDate).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    // Update the status to "Absent"
                                    // Update the status to "Absent" with ServerValue.TIMESTAMP
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("dateDay", currentDate);
                                    data.put("dateClockInTime", "9:45:00 am");
                                    data.put("clockInStatus", "Absent");
                                    data.put("dateClockOutTime", "9:45:00 am");
                                    data.put("clockOutStatus", "Absent");
                                    data.put("timestamp", ServerValue.TIMESTAMP);
                                    userAttendanceRef.push().setValue(data);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error
                            }
                        });
                    }
                } else {
                    // Update clock-out status to "On-time" for today
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (currentUser != null) {
                        String uid = currentUser.getUid();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                        DatabaseReference userAttendanceRef = databaseReference.child("Attendance").child(uid);

                        userAttendanceRef.orderByChild("dateDay").equalTo(currentDate).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());

                                    //Turn the clock-in time from military time to nonmilitary time format
                                    Date dateClockInTime = null;
                                    try {
                                        dateClockInTime = inputFormat.parse(storedClockInTime);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    String amPmTimeFormat = outputFormat.format(dateClockInTime);

                                    // Update the clock-out status to "On-Time"
//                                    ReadWriteUserTimeDetails readWriteUserTimeDetails = new ReadWriteUserTimeDetails(currentDate, amPmTimeFormat, storedClockInStatus, "5:59:59 PM", "Undertime");
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("dateDay", currentDate);
                                    data.put("dateClockInTime", amPmTimeFormat);
                                    data.put("clockInStatus", storedClockInStatus);
                                    data.put("dateClockOutTime", "5:59:59 pm");
                                    data.put("clockOutStatus", "Undertime");
                                    data.put("timestamp", ServerValue.TIMESTAMP);
                                    userAttendanceRef.push().setValue(data);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error
                            }
                        });
                    }
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("clockInButtonTapped", false);
                editor.apply();
            } else {
                System.out.println("It's not yet time to check if user is absent or not for the weekday!");
            }
        } else {
            System.out.println("It's the weekends!");
        }
    }

}
