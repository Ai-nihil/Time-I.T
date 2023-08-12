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
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String storedDate = sharedPreferences.getString("lastTappedDate", "");
        String storedTime = sharedPreferences.getString("lastTappedTime", "");

        Calendar currentCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String currentDate = dateFormat.format(currentCalendar.getTime());
        System.out.println(storedDate);

        String[] storedTimeParts = storedTime.split(":");
        int storedHour = Integer.parseInt(storedTimeParts[0]);
        int storedMinute = Integer.parseInt(storedTimeParts[1]);

        // Check if the user tapped the button for today
        if ((!storedDate.equals(currentDate)) || (storedDate.equals(currentDate) && (storedHour < 8 || (storedHour == 8 && storedMinute < 45)))) {
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
                            ReadWriteUserTimeDetails readWriteUserTimeDetails = new ReadWriteUserTimeDetails("11:12:00 PM", currentDate, "Absent");
                            userAttendanceRef.push().setValue(readWriteUserTimeDetails);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });

            }
        }
    }
}
