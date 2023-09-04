package com.example.ojtproject;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomePageFragment extends Fragment {

    private Button homePageFragmentButtonLogout,
            homePageFragmentButtonClockIn,
            homePageFragmentButtonClockOut,
            homePageFragmentButtonViewRecords;
    private TextClock homePageFragmentTextClockActualDate;
    private TextClock homePageFragmentTextClockActualClock;
    private FirebaseAuth authProfile;
    private FirebaseUser currentUser;
    private ReadWriteUserTimeDetails readWriteUserTimeDetails;
    private Boolean homePageFragmentButtonClockInClicked;
    private FragmentManager fragmentManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        homePageFragmentButtonClockInClicked = false;

        authProfile = FirebaseAuth.getInstance();

        homePageFragmentButtonLogout = rootView.findViewById(R.id.homePageFragmentButtonLogout);
        homePageFragmentButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);//So when phone is opened, user will be redirected to the MainFragment instead of HomePageActivity
                editor.apply();
                Intent intent = new Intent(getActivity(), MainActivity.class);

                //Don't let user go back to HomePageActivity or any activities related opened after logout button was tapped if back button of phone is tapped
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                authProfile.signOut();
            }
        });

        homePageFragmentTextClockActualClock = rootView.findViewById(R.id.homePageFragmentTextClockActualClock);
        homePageFragmentTextClockActualDate = rootView.findViewById(R.id.homePageFragmentTextClockActualDate);

        //Clock-In button logic starts here
        homePageFragmentButtonClockIn = rootView.findViewById(R.id.homePageFragmentButtonClockIn);
        homePageFragmentButtonClockIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser = authProfile.getCurrentUser();

                //Check if there is a user logged-in
                if (currentUser != null) {
                    String uid = currentUser.getUid();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Attendance").child(uid);

                    // Get the current date and time from the TextClocks
                    String currentClockInTime = homePageFragmentTextClockActualClock.getText().toString();
                    String currentClockInDate = homePageFragmentTextClockActualDate.getText().toString();

                    databaseReference.orderByChild("dateDay").equalTo(currentClockInDate).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // A record for today already exists, prevent clock-in
                                Toast.makeText(getActivity(), "You already have a status for your clock-in today.", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                                    //Turn the clock-in time from nonmilitary time to military time format
                                    Date dateClockInTime = inputFormat.parse(currentClockInTime);
                                    String militaryTimeFormat = outputFormat.format(dateClockInTime);

                                    readWriteUserTimeDetails = new ReadWriteUserTimeDetails();
                                    readWriteUserTimeDetails.setDateClockInTime(currentClockInTime);
                                    readWriteUserTimeDetails.setDateDay(currentClockInDate);

                                    // Split the time string into hours, minutes, and seconds
                                    String[] currentClockInTimeParts = militaryTimeFormat.split(":");
                                    int currentClockInHour = Integer.parseInt(currentClockInTimeParts[0]);
                                    int currentClockInMinute = Integer.parseInt(currentClockInTimeParts[1]);
                                    int currentClockInSecond = Integer.parseInt(currentClockInTimeParts[2]);

                                    //Extract the day of the week from currentDate
                                    String[] currentClockInDateParts = currentClockInDate.split(",");
                                    String currentClockInDayOfWeek = currentClockInDateParts[0];

                                    System.out.println(currentClockInHour + ":" + currentClockInMinute + ":" + currentClockInSecond);

                                    String clockInStatus;
                                    //Exclude weekends from schedule
                                    if (!currentClockInDayOfWeek.equals("Saturday") && !currentClockInDayOfWeek.equals("Sunday")) {
                                        //Exclude 9:30:01 AM onwards from attendance windows AlarmReceiver class will set the status instead
                                        if (currentClockInHour > 9 || (currentClockInHour == 9 && currentClockInMinute > 45) || (currentClockInHour == 9 && currentClockInMinute == 45 && currentClockInSecond > 0)) {
                                            Toast.makeText(getActivity(), "Attendance not yet available. Please wait until the designated time.", Toast.LENGTH_SHORT).show();
                                        }
                                        //Employee absent during the 9:30:01-9:45:00 AM
                                        else if ((currentClockInHour == 9 && currentClockInMinute > 30 && currentClockInMinute < 45) || (currentClockInHour == 9 && currentClockInMinute == 30 && currentClockInSecond > 0) || (currentClockInHour == 9 && currentClockInMinute == 45 && currentClockInSecond == 0)) {
                                            clockInStatus = "Absent";
                                            readWriteUserTimeDetails.setClockInStatus(clockInStatus);
                                            // Write the time record to the Firebase Realtime Database under the "attendance" node with the user's UID as the key
                                            ReadWriteUserTimeDetails readWriteUserTimeDetailsAbsent = new ReadWriteUserTimeDetails(currentClockInDate, currentClockInTime, clockInStatus, "9:45", "Absent");
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.child("Attendance").child(uid).push().setValue(readWriteUserTimeDetailsAbsent);
                                            Log.d("Military Time", militaryTimeFormat);
                                            Toast.makeText(getActivity(), "You are marked absent for today!", Toast.LENGTH_SHORT).show();
                                        }
                                        //Employee late during the 9:15:01-9:30:00 AM
                                        else if ((currentClockInHour == 9 && currentClockInMinute > 15 && currentClockInMinute < 30) || (currentClockInHour == 9 && currentClockInMinute == 15 && currentClockInSecond > 0) || (currentClockInHour == 9 && currentClockInMinute == 30 && currentClockInSecond == 0)) {
                                            clockInStatus = "Late";
                                            readWriteUserTimeDetails.setClockInStatus(clockInStatus);
                                            homePageFragmentButtonClockInClicked = true;
                                            // Get the current date and time of the when the clock-in button was last tapped to determine if user is absent because of forgetting to tap the button for example
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("lastTappedClockInDate", currentClockInDate);
                                            editor.putString("lastTappedClockInTime", militaryTimeFormat);
                                            editor.putString("lastTappedClockInStatus", clockInStatus);
                                            editor.putBoolean("clockInButtonTapped", true);
                                            editor.apply();
                                            Log.d("Military Time", militaryTimeFormat);
                                            Toast.makeText(getActivity(), "You are late!", Toast.LENGTH_SHORT).show();
                                        }
                                        //Employee present during the 8:45:00-9:15:00 AM
                                        else if ((currentClockInHour == 8 && currentClockInMinute >= 45) || (currentClockInHour == 9 && currentClockInMinute < 15) || (currentClockInHour == 9 && currentClockInMinute == 15 && currentClockInSecond == 0)) {
                                            clockInStatus = "On-time";
                                            readWriteUserTimeDetails.setClockInStatus(clockInStatus);
                                            homePageFragmentButtonClockInClicked = true;
                                            // Get the current date and time of the when the clock-in button was last tapped to determine if user is absent because of forgetting to tap the button for example
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("lastTappedClockInDate", currentClockInDate);
                                            editor.putString("lastTappedClockInTime", militaryTimeFormat);
                                            editor.putString("lastTappedClockInStatus", clockInStatus);
                                            editor.putBoolean("clockInButtonTapped", true);
                                            editor.apply();
                                            Log.d("Military Time", militaryTimeFormat);
                                            Toast.makeText(getActivity(), "Congratulations! You made it on time!", Toast.LENGTH_SHORT).show();
                                        }
                                        //From midnight until 8:44:59 AM, no status will be given
                                        else {
                                            Toast.makeText(getActivity(), "Attendance not yet available. Please wait until the designated '\"'on-time'\"' start time of 8:45 AM - 9:15 AM to mark your attendance.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    //Message when it's the weekends
                                    else {
                                        Toast.makeText(getActivity(), "Get some rest. There is no work today!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "Please login to continue.", Toast.LENGTH_SHORT).show();
                    //Open Login Activity after successful registration
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    //To Prevent User from returning back to Home Page Activity on pressing back button after registration
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        //Clock-out logic starts here
        homePageFragmentButtonClockOut = rootView.findViewById(R.id.homePageFragmentButtonClockOut);
        homePageFragmentButtonClockOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser = authProfile.getCurrentUser();

                //Check if there is a user logged-in
                if (homePageFragmentButtonClockInClicked == true) {
                    if (currentUser != null) {
                        String uid = currentUser.getUid();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Attendance").child(uid);

                        // Get the current date and time from the TextClocks
                        String currentClockOutTime = homePageFragmentTextClockActualClock.getText().toString();
                        String currentClockOutDate = homePageFragmentTextClockActualDate.getText().toString();

                        // Get the current date and time of the when the clock-in button was last tapped to determine if user is absent because of forgetting to tap the button for example
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("lastTappedClockOutDate", currentClockOutDate);
                        editor.putString("lastTappedClockOutTime", currentClockOutTime);
                        editor.apply();

                        databaseReference.orderByChild("dateDay").equalTo(currentClockOutDate).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // A record for today already exists, prevent clock-in
                                    Toast.makeText(getActivity(), "You already have a status for your clock-out today.", Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
                                        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                                        //Turn the clock-in time from nonmilitary time to military time format
                                        Date dateClockOutTime = inputFormat.parse(currentClockOutTime);
                                        String militaryTimeFormat = outputFormat.format(dateClockOutTime);

                                        readWriteUserTimeDetails.setDateClockOutTime(currentClockOutTime);

                                        // Split the time string into hours, minutes, and seconds
                                        String[] currentClockOutTimeParts = militaryTimeFormat.split(":");
                                        int currentClockOutHour = Integer.parseInt(currentClockOutTimeParts[0]);
                                        int currentClockOutMinute = Integer.parseInt(currentClockOutTimeParts[1]);
                                        int currentClockOutSecond = Integer.parseInt(currentClockOutTimeParts[2]);

                                        //Extract the day of the week from currentDate
                                        String[] currentClockOutDateParts = currentClockOutDate.split(",");
                                        String currentClockOutDayOfWeek = currentClockOutDateParts[0];

                                        System.out.println(currentClockOutHour + ":" + currentClockOutMinute + ":" + currentClockOutSecond);

                                        String clockOutStatus;
                                        //Exclude weekends from schedule
                                        if (!currentClockOutDayOfWeek.equals("Saturday") && !currentClockOutDayOfWeek.equals("Sunday")) {
                                            //Exclude 6:30:01 PM onwards from attendance windows AlarmReceiver class will set the status instead
                                            if (currentClockOutHour > 18 || (currentClockOutHour == 18 && currentClockOutMinute > 30) || (currentClockOutHour == 18 && currentClockOutMinute == 30 && currentClockOutSecond > 0)) {
                                                Toast.makeText(getActivity(), "Attendance not yet available. Please wait until the designated '\"'on-time'\"' start time of 8:45 AM - 9 to mark your attendance.", Toast.LENGTH_SHORT).show();
                                            }
                                            //Employee late during the 6:00:00-6:30:00 PM
                                            else if ((currentClockOutHour == 18 && currentClockOutMinute >= 0 && currentClockOutMinute < 30) || (currentClockOutHour == 18 && currentClockOutMinute == 30 && currentClockOutSecond == 0)) {
                                                clockOutStatus = "On-time";
                                                readWriteUserTimeDetails.setClockOutStatus(clockOutStatus);
                                                //Write the time record to the Firebase Realtime Database under the "attendance" node with the user's UID as the key
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                                databaseReference.child("Attendance").child(uid).push().setValue(readWriteUserTimeDetails);
                                                homePageFragmentButtonClockInClicked = false;
                                                Log.d("Military Time", militaryTimeFormat);
                                                Toast.makeText(getActivity(), "You successfully clocked-out!", Toast.LENGTH_SHORT).show();
                                            }
                                            //Employee present during the 8:45:00-5:59:59 PM
                                            else if ((currentClockOutHour == 8 && currentClockOutMinute >= 45) || (currentClockOutHour > 8 && currentClockOutHour < 18 && currentClockOutMinute >= 0)) {
                                                clockOutStatus = "Undertime";
                                                readWriteUserTimeDetails.setClockOutStatus(clockOutStatus);
                                                // Write the time record to the Firebase Realtime Database under the "attendance" node with the user's UID as the key
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                                databaseReference.child("Attendance").child(uid).push().setValue(readWriteUserTimeDetails);
                                                homePageFragmentButtonClockInClicked = false;
                                                Log.d("Military Time", militaryTimeFormat);
                                                Toast.makeText(getActivity(), "You successfully clocked-out earlier than expected!", Toast.LENGTH_SHORT).show();
                                            }
                                            //From midnight until 8:44:59 AM, no status will be given
                                            else {
                                                Toast.makeText(getActivity(), "Attendance not yet available. Please wait until the designated '\"'on-time'\"' start time of 8:45 AM - 9:15 AM to mark your attendance.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        //Message when it's the weekends
                                        else {
                                            Toast.makeText(getActivity(), "Get some rest. There is no work today!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Please login to continue.", Toast.LENGTH_SHORT).show();
                        //Open Login Activity after successful registration
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        //To Prevent User from returning back to Home Page Activity on pressing back button after registration
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                }
                //User must have a clock-in status of either on-time or late to use the clock-out button
                else {
                    Toast.makeText(getActivity(), "Please, check if you successfully clocked-in first before clocking-out!", Toast.LENGTH_LONG).show();
                }
            }
        });

        //View Records logic starts here
        homePageFragmentButtonViewRecords = rootView.findViewById(R.id.homePageFragmentButtonViewRecords);
        homePageFragmentButtonViewRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getActivity().getSupportFragmentManager();
                UserAttendanceRecordsFragment userAttendanceRecordsFragment = new UserAttendanceRecordsFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.homeFragmentContainer, userAttendanceRecordsFragment);
                transaction.commit();

                BottomNavigationView homeFragmentBottomNavigationView = getActivity().findViewById(R.id.homeFragmentBottomNavigationView);
                homeFragmentBottomNavigationView.setSelectedItemId(R.id.bottomNavigationMenuItemAttendanceRecords);
            }
        });

        // Calculate the time difference between the current time and the desired trigger time
        // Trigger at 6:30:00 PM
        int desiredHour = 18;
        int desiredMinute = 30;

        // Get the current time in the desired timezone
        android.icu.util.Calendar currentTime = android.icu.util.Calendar.getInstance(android.icu.util.TimeZone.getTimeZone("Asia/Singapore"));

        //Calculate the time difference of set time and current time
        long timeDifferenceMillis = calculateTimeDifference(currentTime, desiredHour, desiredMinute);
        //Call AlarmReceiver class at 9:45:00 AM:
        setupAlarm(timeDifferenceMillis);

        // Inflate the layout for this fragment
        return rootView;
    }

    //setupAlarm() method logic starts here
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupAlarm(long timeDifferenceMillis) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_MUTABLE);

        // Set up the AlarmManager to trigger at the calculated time
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeDifferenceMillis, pendingIntent);
    }

    //calculateTimeDifference() method logic starts here
    @RequiresApi(api = Build.VERSION_CODES.N)
    private long calculateTimeDifference(android.icu.util.Calendar currentTime, int desiredHour, int desiredMinute) {
        android.icu.util.Calendar triggerTime = (android.icu.util.Calendar) currentTime.clone();
        triggerTime.set(android.icu.util.Calendar.HOUR_OF_DAY, desiredHour);
        triggerTime.set(android.icu.util.Calendar.MINUTE, desiredMinute);
        triggerTime.set(android.icu.util.Calendar.SECOND, 0);
        triggerTime.set(android.icu.util.Calendar.MILLISECOND, 0);

        //Return time difference of 9:45:00 AM and whatever the current time is
        return triggerTime.getTimeInMillis() - currentTime.getTimeInMillis();
    }

    private BroadcastReceiver updateFlagReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Read the flag value from shared preferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean clockInButtonTapped = sharedPreferences.getBoolean("clockInButtonTapped", false);

            // Update your flag variable in HomePageActivity
            homePageFragmentButtonClockInClicked = clockInButtonTapped;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(updateFlagReceiver, new IntentFilter("your_action_here"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(updateFlagReceiver);
    }

}