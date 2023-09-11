package com.example.ojtproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class UserAttendanceAdminDetailView extends AppCompatActivity {

    private String statusClockIn,
            statusClockOut,
            clockIn,
            clockOut,
            date,
            statusCode;
    private TextView statusClockInTextView,
            statusClockOutTextView,
            clockInTextView,
            clockOutTextView,
            dateTextView,
            statusCodeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_attendance_admin_detail_view);

        statusClockInTextView = findViewById(R.id.userAttendanceRecordDetailsActivityTextViewClockInStatusValue);
        statusClockOutTextView = findViewById(R.id.userAttendanceRecordDetailsActivityTextViewClockOutStatusValue);
        clockInTextView = findViewById(R.id.userAttendanceRecordDetailsActivityTextViewClockInTimeValue);
        clockOutTextView = findViewById(R.id.userAttendanceRecordDetailsActivityTextViewClockOutTimeValue);
        dateTextView = findViewById(R.id.userAttendanceRecordDetailsActivityTextViewDayDateValue);
        statusCodeTextView = findViewById(R.id.userAttendanceRecordDetailsActivityTextViewStatusCode);

        statusClockIn = getIntent().getStringExtra("statusClockIn");
        statusClockOut = getIntent().getStringExtra("statusClockOut");
        clockIn = getIntent().getStringExtra("clockIn");
        clockOut = getIntent().getStringExtra("clockOut");
        date = getIntent().getStringExtra("date");
        statusCode = getIntent().getStringExtra("statusCode");

        statusClockInTextView.setText(statusClockIn);
        statusClockOutTextView.setText(statusClockOut);
        clockInTextView.setText(clockIn);
        clockOutTextView.setText(clockOut);
        dateTextView.setText(date);
        statusCodeTextView.setText(statusCode);

    }
}