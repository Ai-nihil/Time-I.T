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

        //Initialize TextView values of each list item in the list view of UserAttendanceRecordsActivity with data from FireBase
        listItemTextViewDateDay.setText(readWriteUserTimeDetails.getDateDay());
        listItemTextViewDateTimeClockIn.setText(readWriteUserTimeDetails.getDateClockInTime());
        listItemTextViewStatusClockIn.setText(readWriteUserTimeDetails.getClockInStatus());

        return convertView;
    }

}
