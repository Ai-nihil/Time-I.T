package com.example.ojtproject;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ojtproject.databinding.ActivityMainBinding;
import com.example.ojtproject.databinding.ActivityUserAttendanceRecordDetailsBinding;
import com.example.ojtproject.databinding.ActivityUserAttendanceRecordsBinding;
import com.example.ojtproject.databinding.FragmentUserAttendanceRecordsBinding;
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
    private TextView userAttendanceRecordsFragmentTextViewNoRecord;
    private CustomSwipeRefreshLayout userAttendanceRecordsFragmentCustomSwipeRefreshLayout;
    private SearchView userAttendanceRecordsFragmentSearchView;
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_attendance_records, container, false);

        userAttendanceRecordsFragmentListView = rootView.findViewById(R.id.userAttendanceRecordsFragmentListView);
        userAttendanceRecordsFragmentTextViewNoRecord = rootView.findViewById(R.id.userAttendanceRecordsFragmentTextViewNoRecord);
        userAttendanceRecordsFragmentCustomSwipeRefreshLayout = rootView.findViewById(R.id.userAttendanceRecordsFragmentCustomSwipeRefreshLayout);
        userAttendanceRecordsFragmentSearchView = rootView.findViewById(R.id.userAttendanceRecordsFragmentSearchView);

        // Initialize UI components
        readWriteUserTimeDetailsList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Attendance");

        listAdapter = new ListAdapter(getActivity(), readWriteUserTimeDetailsList);

        userAttendanceRecordsFragmentListView.setAdapter(listAdapter);
        userAttendanceRecordsFragmentListView.setClickable(true);
        userAttendanceRecordsFragmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReadWriteUserTimeDetails selectedRecord = readWriteUserTimeDetailsList.get(position);

                // Create an Intent to start the new activity
                Intent intent = new Intent(getActivity(), UserAttendanceRecordDetailsActivity.class);
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

                    listAdapter.notifyDataSetChanged();// Notify adapter of data change
                    userAttendanceRecordsFragmentListView.setVisibility(View.VISIBLE);
                    userAttendanceRecordsFragmentTextViewNoRecord.setVisibility(View.GONE);

                    if (listAdapter.getCount() == 0) {
                        // Handle the case where the ListView is empty
                        // You can display a message or perform any necessary actions
                        userAttendanceRecordsFragmentListView.setVisibility(View.GONE);
                        userAttendanceRecordsFragmentTextViewNoRecord.setVisibility(View.VISIBLE);

                    }
                        System.out.println(listAdapter.getReadWriteUserTimeDetailsList().get(3).getDateDay());

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
                return false;
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