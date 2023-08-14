package com.example.ojtproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter<ReadWriteUserTimeDetails> {

    //Constructors for passing either ArrayList or List
    public ListAdapter(Context context, ArrayList<ReadWriteUserTimeDetails> userAttendanceArrayList){
        super(context, R.layout.list_item, userAttendanceArrayList);
    }
    public ListAdapter(Context context, List<ReadWriteUserTimeDetails> userAttendanceArrayList){
        super(context, R.layout.list_item, userAttendanceArrayList);
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

}
