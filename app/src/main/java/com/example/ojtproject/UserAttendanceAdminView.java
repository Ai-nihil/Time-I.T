package com.example.ojtproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserAttendanceAdminView extends AppCompatActivity implements UserAttendanceAdminAdapter.OnItemClickListener {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private UserAttendanceAdminAdapter userAttendanceAdminAdapter;
    private List<ReadWriteUserTimeDetails> userTimeDetailsList;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_attendance_admin_view);

        getSupportActionBar().setTitle("Admin Attendance Records");

        searchView = findViewById(R.id.search_view);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userTimeDetailsList = new ArrayList<>();
        userAttendanceAdminAdapter = new UserAttendanceAdminAdapter(userTimeDetailsList, this); // Pass 'this' as the itemClickListener
        recyclerView.setAdapter(userAttendanceAdminAdapter);

        userID = getIntent().getStringExtra("userID");

        // Retrieve the user data from Firebase and add it to the userList
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Attendance").child(userID);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userTimeDetailsList.clear(); // Clear existing data before populating

                // Add readWriteUserTimeDetails objects to the List<readWriteUserTimeDetails>
                for (DataSnapshot recordSnapshot : dataSnapshot.getChildren()) {
                    ReadWriteUserTimeDetails readWriteUserTimeDetails = recordSnapshot.getValue(ReadWriteUserTimeDetails.class);
                    userTimeDetailsList.add(readWriteUserTimeDetails);
                }

                userAttendanceAdminAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
            }
        });

    }

    //Search feature
    private void filterList(String text) {
        List<ReadWriteUserTimeDetails> filteredList = new ArrayList<>();
        for (ReadWriteUserTimeDetails readWriteUserTimeDetails : userTimeDetailsList) {
            if (readWriteUserTimeDetails.getDateDay().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(readWriteUserTimeDetails);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show();
        } else {
            userAttendanceAdminAdapter.setFilteredList(filteredList);
        }

    }

    @Override
    public void onItemClick(ReadWriteUserTimeDetails userTimeDetails) {
        // Handle item click event if needed
    }
}