package com.example.ojtproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ojtproject.databinding.ActivityUserAttendanceRecordDetailsBinding;
import com.example.ojtproject.databinding.ActivityUserAttendanceRecordsBinding;

public class UserAttendanceRecordDetailsActivity extends AppCompatActivity {
    private ActivityUserAttendanceRecordDetailsBinding binding;
    private TextView userAttendanceRecordDetailsActivityTextViewDayDateValue;
    private TextView userAttendanceRecordDetailsActivityTextViewClockInTimeValue;
    private TextView userAttendanceRecordDetailsActivityTextViewClockInStatusValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserAttendanceRecordDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = this.getIntent();

        //Initialize TextView values with data from UserAttendanceRecordsActivity
        if (intent != null) {
            ReadWriteUserTimeDetails selectedRecord = (ReadWriteUserTimeDetails) intent.getParcelableExtra("selectedRecord");

            userAttendanceRecordDetailsActivityTextViewDayDateValue = findViewById(R.id.userAttendanceRecordDetailsActivityTextViewDayDateValue);
            String dateDay = selectedRecord.getDateDay();
            userAttendanceRecordDetailsActivityTextViewDayDateValue.setText(dateDay);

            userAttendanceRecordDetailsActivityTextViewClockInTimeValue = findViewById(R.id.userAttendanceRecordDetailsActivityTextViewClockInTimeValue);
            String dateClockInTime = selectedRecord.getDateClockInTime();
            userAttendanceRecordDetailsActivityTextViewClockInTimeValue.setText(dateClockInTime);

            userAttendanceRecordDetailsActivityTextViewClockInStatusValue = findViewById(R.id.userAttendanceRecordDetailsActivityTextViewClockInStatusValue);
            String clockInStatus = selectedRecord.getClockInStatus();
            userAttendanceRecordDetailsActivityTextViewClockInStatusValue.setText(clockInStatus);

            userAttendanceRecordDetailsActivityTextViewClockInTimeValue = findViewById(R.id.userAttendanceRecordDetailsActivityTextViewClockInTimeValue);
            String dateClockOutTime = selectedRecord.getDateClockInTime();
            userAttendanceRecordDetailsActivityTextViewClockInTimeValue.setText(dateClockOutTime);

            userAttendanceRecordDetailsActivityTextViewClockInStatusValue = findViewById(R.id.userAttendanceRecordDetailsActivityTextViewClockInStatusValue);
            String clockOutStatus = selectedRecord.getClockInStatus();
            userAttendanceRecordDetailsActivityTextViewClockInStatusValue.setText(clockOutStatus);
        }
    }
}