package com.example.ojtproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserAttendanceAdminView extends AppCompatActivity implements UserAttendanceAdminAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private UserAttendanceAdminAdapter userAttendanceAdminAdapter;
    private List<ReadWriteUserTimeDetails> userTimeDetailsList;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_attendance_admin_view);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userTimeDetailsList = new ArrayList<>();
        userAttendanceAdminAdapter = new UserAttendanceAdminAdapter(userTimeDetailsList, this, this); // Pass 'this' as the itemClickListener
        recyclerView.setAdapter(userAttendanceAdminAdapter);

        userID = getIntent().getStringExtra("userID");

        // Retrieve the user data from Firebase and add it to the userList
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Attendance").child(userID);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userTimeDetailsList.clear(); // Clear existing data before populating
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    ReadWriteUserTimeDetails userTimeDetails = userSnapshot.getValue(ReadWriteUserTimeDetails.class);
                    userTimeDetailsList.add(userTimeDetails);
                    Toast.makeText(UserAttendanceAdminView.this, userTimeDetails.toString(), Toast.LENGTH_LONG).show();
                }
                userAttendanceAdminAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
            }
        });
    }

    @Override
    public void onItemClick(ReadWriteUserTimeDetails userTimeDetails) {

    }
}