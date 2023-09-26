package com.example.ojtproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListAdapter extends ArrayAdapter<ReadWriteUserTimeDetails> {
    private List<ReadWriteUserTimeDetails> readWriteUserTimeDetailsList;
    private List<ReadWriteUserTimeDetails> readWriteUserTimeDetailsListFull; // Add this field
    private CustomFilter customFilter;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private CustomFilterListener customFilterListener;

    //Constructors for passing either ArrayList or List
    public ListAdapter(Context context, ArrayList<ReadWriteUserTimeDetails> userAttendanceArrayList){
        super(context, R.layout.list_item, userAttendanceArrayList);
    }
    public ListAdapter(Context context, List<ReadWriteUserTimeDetails> userAttendanceArrayList, CustomFilterListener customFilterListener){
        super(context, R.layout.list_item, userAttendanceArrayList);
        this.readWriteUserTimeDetailsList = userAttendanceArrayList;
        this.readWriteUserTimeDetailsListFull = new ArrayList<>(userAttendanceArrayList); // Initialize the full list
        this.customFilterListener = customFilterListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ReadWriteUserTimeDetails readWriteUserTimeDetails = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView listItemTextViewDateDay = convertView.findViewById(R.id.listItemTextViewDateDay);
        TextView listItemTextViewDateTimeClockIn = convertView.findViewById(R.id.listItemTextViewDateTimeClockIn);
        TextView listItemTextViewStatusClockIn = convertView.findViewById(R.id.listItemTextViewStatusClockIn);
        TextView listItemTextViewDateTimeClockOut = convertView.findViewById(R.id.listItemTextViewDateTimeClockOut);
        TextView listItemTextViewStatusClockOut = convertView.findViewById(R.id.listItemTextViewStatusClockOut);
        TextView listItemTextViewStatusCode = convertView.findViewById(R.id.listItemTextViewStatusCode);

        String dateDay = readWriteUserTimeDetails.getDateDay();
        String dateClockInTime = readWriteUserTimeDetails.getDateClockInTime();
        String clockInStatus = readWriteUserTimeDetails.getClockInStatus();
        String dateClockOutTime = readWriteUserTimeDetails.getDateClockOutTime();
        String clockOutStatus = readWriteUserTimeDetails.getClockOutStatus();

        //Initialize TextView values of each list item in the list view of UserAttendanceRecordsActivity with data from FireBase
        listItemTextViewDateDay.setText(dateDay);
        listItemTextViewDateTimeClockIn.setText(dateClockInTime);
        listItemTextViewStatusClockIn.setText(clockInStatus);
        listItemTextViewDateTimeClockOut.setText(dateClockOutTime);
        listItemTextViewStatusClockOut.setText(clockOutStatus);

        if(clockInStatus.equals("On-time") && clockOutStatus.equals("On-time")){
            String acronym = "O";
            listItemTextViewStatusCode.setText(acronym);
        } else if(clockInStatus.equals("On-time") && clockOutStatus.equals("Undertime")){
            String acronym = "OU";
            listItemTextViewStatusCode.setText(acronym);
        } else if(clockInStatus.equals("Late") && clockOutStatus.equals("On-time")){
            String acronym = "LO";
            listItemTextViewStatusCode.setText(acronym);
        } else if(clockInStatus.equals("Late") && clockOutStatus.equals("Undertime")){
            String acronym = "LU";
            listItemTextViewStatusCode.setText(acronym);
        } else {
            String acronym = "A";
            listItemTextViewStatusCode.setText(acronym);
        }


        return convertView;
    }

    @NonNull
    public CustomFilter getCustomFilter() {
        customFilter = new CustomFilter(ListAdapter.this, readWriteUserTimeDetailsList, customFilterListener);
        return customFilter;
    }

    public void updateData(List<ReadWriteUserTimeDetails> newData) {
        readWriteUserTimeDetailsList.clear();
        readWriteUserTimeDetailsList.addAll(newData);
        // Sort the list by timestamp in descending order (latest to earliest)
        Collections.sort(readWriteUserTimeDetailsList, new Comparator<ReadWriteUserTimeDetails>() {
            @Override
            public int compare(ReadWriteUserTimeDetails item1, ReadWriteUserTimeDetails item2) {
                // Compare timestamps here, assuming they are Long values
                Long timestamp1 = item1.getTimestamp();
                Long timestamp2 = item2.getTimestamp();
                return timestamp2.compareTo(timestamp1); // Sort in descending order
            }
        });
        notifyDataSetChanged();
    }

    public void notUpdatedData(){
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

                    // Sort the list by timestamp in descending order (latest to earliest)
                    Collections.sort(readWriteUserTimeDetailsList, new Comparator<ReadWriteUserTimeDetails>() {
                        @Override
                        public int compare(ReadWriteUserTimeDetails item1, ReadWriteUserTimeDetails item2) {
                            // Compare timestamps here, assuming they are Long values
                            Long timestamp1 = item1.getTimestamp();
                            Long timestamp2 = item2.getTimestamp();
                            return timestamp2.compareTo(timestamp1); // Sort in descending order
                        }
                    });

                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public List<ReadWriteUserTimeDetails> getReadWriteUserTimeDetailsList() {
        return readWriteUserTimeDetailsList;
    }
}
