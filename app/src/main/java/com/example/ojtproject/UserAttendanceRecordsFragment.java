package com.example.ojtproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserAttendanceRecordsFragment extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private List<ReadWriteUserTimeDetails> readWriteUserTimeDetailsList;
    private ListAdapter listAdapter;
    private ListView userAttendanceRecordsFragmentListView;
    private TextView userAttendanceRecordsFragmentTextViewNoRecord, userAttendanceRecordsFragmentTextViewNoMatches;
    private CustomSwipeRefreshLayout userAttendanceRecordsFragmentCustomSwipeRefreshLayout;
    private SearchView userAttendanceRecordsFragmentSearchView;
    private ProgressBar userUserAttendanceRecordsFragmentProgressBar;
    private FragmentManager fragmentManager;
    private CustomFilterListener customFilterListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_attendance_records, container, false);

        userAttendanceRecordsFragmentListView = rootView.findViewById(R.id.userAttendanceRecordsFragmentListView);
        userAttendanceRecordsFragmentTextViewNoRecord = rootView.findViewById(R.id.userAttendanceRecordsFragmentTextViewNoRecord);
        userAttendanceRecordsFragmentTextViewNoMatches = rootView.findViewById(R.id.userAttendanceRecordsFragmentTextViewNoMatches);
        userAttendanceRecordsFragmentCustomSwipeRefreshLayout = rootView.findViewById(R.id.userAttendanceRecordsFragmentCustomSwipeRefreshLayout);
        userAttendanceRecordsFragmentSearchView = rootView.findViewById(R.id.userAttendanceRecordsFragmentSearchView);
        userUserAttendanceRecordsFragmentProgressBar = rootView.findViewById(R.id.userUserAttendanceRecordsFragmentProgressBar);

        // Initialize UI components
        readWriteUserTimeDetailsList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Attendance");

        customFilterListener = new CustomFilterListener() {
            @Override
            public void onFilterResults(boolean hasMatches) {
                if (hasMatches == true) {
                    // There are matches, update the UI accordingly
                    // For example, hide the "No matches found" TextView and show the ListView
                    userAttendanceRecordsFragmentListView.setVisibility(View.VISIBLE);
                    userAttendanceRecordsFragmentTextViewNoMatches.setVisibility(View.GONE);
                } else {
                    userAttendanceRecordsFragmentListView.setVisibility(View.GONE);
                    userAttendanceRecordsFragmentTextViewNoMatches.setVisibility(View.VISIBLE);
                }
            }
        };
        listAdapter = new ListAdapter(getActivity(), readWriteUserTimeDetailsList, customFilterListener);

        userAttendanceRecordsFragmentListView.setAdapter(listAdapter);
        userAttendanceRecordsFragmentListView.setClickable(true);
        userAttendanceRecordsFragmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReadWriteUserTimeDetails selectedRecord = readWriteUserTimeDetailsList.get(position);

                // Create an Intent to start the new activity
                Intent intent = new Intent(getActivity(), UserAttendanceRecordDetailsActivity.class);
                intent.putExtra("selectedRecord", selectedRecord); // Pass the selected record as an extra
                intent.putExtra("openedFrom", "UserAttendanceRecordsFragment");
                startActivity(intent);
            }
        });

        // Get the currently authenticated user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            userUserAttendanceRecordsFragmentProgressBar.setVisibility(View.VISIBLE);
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

                    listAdapter.notifyDataSetChanged();// Notify adapter of data change
                    userAttendanceRecordsFragmentListView.setVisibility(View.VISIBLE);
                    userAttendanceRecordsFragmentSearchView.setVisibility(View.VISIBLE);
                    userAttendanceRecordsFragmentTextViewNoRecord.setVisibility(View.GONE);

                    if (listAdapter.getCount() == 0) {
                        // Handle the case where the ListView is empty
                        // You can display a message or perform any necessary actions
                        userAttendanceRecordsFragmentListView.setVisibility(View.GONE);
                        userAttendanceRecordsFragmentSearchView.setVisibility(View.GONE);
                        userAttendanceRecordsFragmentTextViewNoRecord.setVisibility(View.VISIBLE);

                    }
                    userUserAttendanceRecordsFragmentProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            // User is not logged in, handle the situation accordingly
            Toast.makeText(getActivity(), "Please login to continue.", Toast.LENGTH_SHORT).show();
            //Open Login Activity after successful registration
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        swipeToRefresh();

        // Set an OnQueryTextListener for filtering
        userAttendanceRecordsFragmentSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        userAttendanceRecordsFragmentSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                refreshOriginalData();
                userAttendanceRecordsFragmentListView.setVisibility(View.VISIBLE);
                userAttendanceRecordsFragmentTextViewNoMatches.setVisibility(View.GONE);
                return true;
            }
        });
//      Inflate the layout for this fragment
        return rootView;
    }

    public void refreshOriginalData(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Attendance");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    readWriteUserTimeDetailsList.clear(); // Clear existing data before populating

                    // Add readWriteUserTimeDetails objects to the List<readWriteUserTimeDetails>
                    for (DataSnapshot recordSnapshot : dataSnapshot.getChildren()) {
                        ReadWriteUserTimeDetails readWriteUserTimeDetails = recordSnapshot.getValue(ReadWriteUserTimeDetails.class);
                        readWriteUserTimeDetailsList.add(readWriteUserTimeDetails);
                    }

                    listAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void swipeToRefresh() {

        userAttendanceRecordsFragmentListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    userAttendanceRecordsFragmentCustomSwipeRefreshLayout.setEnabled(false);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    userAttendanceRecordsFragmentCustomSwipeRefreshLayout.setEnabled(true);
                }
                return false;
            }
        });

        // Look up for the the Swipe Container
        //Setup Refresh Listener which triggers new data loading
        userAttendanceRecordsFragmentCustomSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Code to refresh goes here. Call swipeContainer.setRefreshing(false) once the refresh is complete.
//                startActivity(getActivity().getIntent());
//                getActivity().finish();
//                getActivity().overridePendingTransition(0, 0);
//                userAttendanceRecordsFragmentSwipeRefreshLayout.setRefreshing(false);

                fragmentManager = getActivity().getSupportFragmentManager();
                UserAttendanceRecordsFragment userAttendanceRecordsFragment = new UserAttendanceRecordsFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.homeFragmentContainer, userAttendanceRecordsFragment);
                transaction.commit();
            }
        });
    }
}