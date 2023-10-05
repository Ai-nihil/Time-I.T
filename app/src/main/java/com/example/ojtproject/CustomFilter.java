package com.example.ojtproject;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.widget.Filter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CustomFilter extends Filter {

    //Global variables
    private final List<ReadWriteUserTimeDetails> toBeModifiedList;
    private final List<ReadWriteUserTimeDetails> originalList;
    private final ListAdapter adapter;
    private CustomFilterListener customFilterListener;
    private Boolean hasMatches;

    //Constructor for custom filter
    public CustomFilter(ListAdapter adapter, List<ReadWriteUserTimeDetails> originalList, CustomFilterListener customFilterListener) {
        this.adapter = adapter;
        this.toBeModifiedList = new ArrayList<>(originalList); // Create a copy of the original list
        this.originalList = new ArrayList<>(originalList);
        this.customFilterListener = customFilterListener;
        this.hasMatches = false;
    }

    //Perform filtering method with the matches filter method put new data entries in the filtered list
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        ArrayList<ReadWriteUserTimeDetails> filteredList = new ArrayList<>();

        if (constraint == null || constraint.length() == 0) {
            // No filter applied, return the original list
            adapter.notUpdatedData();
        } else {
            String filterPattern = constraint.toString().toLowerCase().trim();

            // Loop through the original list and add items that match the filter
            for (ReadWriteUserTimeDetails item : toBeModifiedList) {
                if (matchesFilter(item, filterPattern)) {
                    filteredList.add(item);
                }
            }
        }

        results.values = filteredList;
        results.count = filteredList.size();
        return results;
    }

    //Publish results method to change the list view of the attendance record
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        List<ReadWriteUserTimeDetails> filteredList = (List<ReadWriteUserTimeDetails>) results.values;
        int filteredListCount = (Integer) results.count;

        // Notify the adapter about the new filtered list
        if ((constraint == null && constraint.length() == 0) && filteredListCount == 0) {
            hasMatches = true;
            adapter.notUpdatedData();
        } else if ((constraint != null && constraint.length() != 0) && filteredListCount == 0) {
            hasMatches = false;
        } else {
            hasMatches = true;
            adapter.updateData(filteredList);
        }
        // Update the filter listener based on whether there are matches
        customFilterListener.onFilterResults(hasMatches);
    }

    //Matches filter method to check if search view contents match any data in the attendance record
    private boolean matchesFilter(ReadWriteUserTimeDetails item, String filterPattern) {
        return item.getDateDay().toLowerCase().contains(filterPattern)
                || item.getDateClockInTime().toLowerCase().contains(filterPattern)
                || item.getDateClockOutTime().toLowerCase().contains(filterPattern)
                || item.getClockInStatus().toLowerCase().contains(filterPattern)
                || item.getClockOutStatus().toLowerCase().contains(filterPattern);
    }
}
