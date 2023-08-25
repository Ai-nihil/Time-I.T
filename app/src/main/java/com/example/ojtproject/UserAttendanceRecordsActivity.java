package com.example.ojtproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ojtproject.databinding.ActivityMainBinding;
import com.example.ojtproject.databinding.ActivityUserAttendanceRecordsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserAttendanceRecordsActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private List<ReadWriteUserTimeDetails> readWriteUserTimeDetailsList;
    private ListAdapter listAdapter;
    private SwipeRefreshLayout swipeContainer;
    private SearchView searchView;
    private @NonNull ActivityUserAttendanceRecordsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserAttendanceRecordsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize UI components
        readWriteUserTimeDetailsList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Attendance");

        listAdapter = new ListAdapter(UserAttendanceRecordsActivity.this, readWriteUserTimeDetailsList);

        binding.userAttendanceRecordsActivityListView.setAdapter(listAdapter);
        binding.userAttendanceRecordsActivityListView.setClickable(true);
        binding.userAttendanceRecordsActivityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReadWriteUserTimeDetails selectedRecord = readWriteUserTimeDetailsList.get(position);

                // Create an Intent to start the new activity
                Intent intent = new Intent(UserAttendanceRecordsActivity.this, UserAttendanceRecordDetailsActivity.class);
                intent.putExtra("selectedRecord", selectedRecord); // Pass the selected record as an extra
                startActivity(intent);
            }
        });

        // Get the currently authenticated user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            // Fetch attendance records from Firebase and populate the attendanceList
            databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    readWriteUserTimeDetailsList.clear(); // Clear existing data before populating

                    // Add readWriteUserTimeDetails objects to the List<readWriteUserTimeDetails>
                    for (DataSnapshot recordSnapshot : dataSnapshot.getChildren()) {
                        ReadWriteUserTimeDetails readWriteUserTimeDetails = recordSnapshot.getValue(ReadWriteUserTimeDetails.class);
                        readWriteUserTimeDetailsList.add(readWriteUserTimeDetails);
                    }

                    listAdapter.notifyDataSetChanged(); // Notify adapter of data change
                    binding.userAttendanceRecordsActivityListView.setVisibility(View.VISIBLE);
                    binding.userAttendanceRecordsActivityTextViewNoRecord.setVisibility(View.GONE);

                    if (listAdapter.getCount() == 0) {
                        // Handle the case where the ListView is empty
                        // You can display a message or perform any necessary actions
                        binding.userAttendanceRecordsActivityListView.setVisibility(View.GONE);
                        binding.userAttendanceRecordsActivityTextViewNoRecord.setVisibility(View.VISIBLE);

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            // User is not logged in, handle the situation accordingly
            Toast.makeText(UserAttendanceRecordsActivity.this, "Please login to continue.", Toast.LENGTH_SHORT).show();
            //Open Login Activity after successful registration
            Intent intent = new Intent(UserAttendanceRecordsActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        swipeToRefresh();

        searchView = binding.userAttendanceRecordsActivitySearchView;

        // Set an OnQueryTextListener for filtering
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.getCustomFilter().filter(newText);
                return true;
            }
        });
    }

    private void swipeToRefresh() {
        // Look up for the the Swipe Container
        swipeContainer = binding.userAttendanceRecordsActivitySwipeRefreshLayout;

        //Setup Refresh Listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Code to refresh goes here. Call swipeContainer.setRefreshing(false) once the refresh is complete.
                startActivity(getIntent());
                finish();
                overridePendingTransition(0, 0);
                swipeContainer.setRefreshing(false);
            }
        });
    }


}