package com.example.ojtproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class UserAttendanceAdminAdapter extends RecyclerView.Adapter<UserAttendanceAdminAdapter.UserTimeViewHolder> {

    Context context;
    private List<ReadWriteUserTimeDetails> userTimeDetailsList;
    String acronym;

    public UserAttendanceAdminAdapter(List<ReadWriteUserTimeDetails> userTimeDetailsList, Context context) {
        this.userTimeDetailsList = userTimeDetailsList;
        this.context = context;
    }

    public void setFilteredList(List<ReadWriteUserTimeDetails> filteredList) {
        // below line is to add our filtered
        // list in our course array list.
        this.userTimeDetailsList = filteredList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_user_attendance_admin, parent, false);

        return new UserTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserTimeViewHolder holder, int position) {
        ReadWriteUserTimeDetails userTimeDetails = userTimeDetailsList.get(position);
        holder.statusClockInTextView.setText(userTimeDetails.getClockInStatus());
        holder.statusClockOutTextView.setText(userTimeDetails.getClockOutStatus());
        holder.clockInTextView.setText(userTimeDetails.getDateClockInTime());
        holder.clockOutTextView.setText(userTimeDetails.getDateClockOutTime());
        holder.dateTextView.setText(userTimeDetails.getDateDay());
        holder.statusCodeTextView.setText(acronym);

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context, UserAttendanceAdminDetailView.class);
            intent.putExtra("statusClockIn", String.valueOf(holder.statusClockInTextView.getText()));
            intent.putExtra("statusClockOut", String.valueOf(holder.statusClockOutTextView.getText()));
            intent.putExtra("clockIn", String.valueOf(holder.clockInTextView.getText()));
            intent.putExtra("clockOut", String.valueOf(holder.clockOutTextView.getText()));
            intent.putExtra("date", String.valueOf(holder.dateTextView.getText()));
            intent.putExtra("statusCode", String.valueOf(holder.statusCodeTextView.getText()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {return userTimeDetailsList.size();}

    public interface OnItemClickListener {
        void onItemClick(ReadWriteUserTimeDetails userTimeDetails);
    }

    public class UserTimeViewHolder extends RecyclerView.ViewHolder {
        private TextView statusCodeTextView;
        private TextView dateTextView;
        private TextView clockInTextView;
        private TextView clockOutTextView;
        private TextView statusClockInTextView;
        private TextView statusClockOutTextView;

        public UserTimeViewHolder(@NonNull View itemView) {
            super(itemView);
            statusCodeTextView = itemView.findViewById(R.id.listItemTextViewStatusCode);
            dateTextView = itemView.findViewById(R.id.listItemTextViewDateDay);
            clockInTextView = itemView.findViewById(R.id.listItemTextViewDateTimeClockIn);
            clockOutTextView = itemView.findViewById(R.id.listItemTextViewDateTimeClockOut);
            statusClockInTextView = itemView.findViewById(R.id.listItemTextViewStatusClockIn);
            statusClockOutTextView = itemView.findViewById(R.id.listItemTextViewStatusClockOut);

            if(clockInTextView.equals("On-time") && clockOutTextView.equals("On-time")){
                acronym = "O";
            } else if(clockInTextView.equals("On-time") && clockOutTextView.equals("Undertime")){
                acronym = "OU";
            } else if(clockInTextView.equals("Late") && clockOutTextView.equals("On-time")){
                acronym = "LO";
            } else if(clockInTextView.equals("Late") && clockOutTextView.equals("Undertime")){
                acronym = "LU";
            } else {
                acronym = "A";
            }
        }
    }
}
