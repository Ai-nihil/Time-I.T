package com.example.ojtproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ojtproject.databinding.ActivityUserAttendanceRecordDetailsBinding;

public class UserAttendanceRecordDetailsActivity extends AppCompatActivity {
    private ActivityUserAttendanceRecordDetailsBinding binding;
    private TextView userAttendanceRecordDetailsActivityTextViewDayDateValue;
    private TextView userAttendanceRecordDetailsActivityTextViewClockInTimeValue;
    private TextView userAttendanceRecordDetailsActivityTextViewClockInStatusValue;
    private TextView userAttendanceRecordDetailsActivityTextViewClockOutTimeValue;
    private TextView userAttendanceRecordDetailsActivityTextViewClockOutStatusValue;
    private TextView userAttendanceRecordDetailsActivityTextViewStatusCode;

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

            userAttendanceRecordDetailsActivityTextViewClockOutTimeValue = findViewById(R.id.userAttendanceRecordDetailsActivityTextViewClockOutTimeValue);
            String dateClockOutTime = selectedRecord.getDateClockOutTime();
            userAttendanceRecordDetailsActivityTextViewClockOutTimeValue.setText(dateClockOutTime);

            userAttendanceRecordDetailsActivityTextViewClockOutStatusValue = findViewById(R.id.userAttendanceRecordDetailsActivityTextViewClockOutStatusValue);
            String clockOutStatus = selectedRecord.getClockOutStatus();
            userAttendanceRecordDetailsActivityTextViewClockOutStatusValue.setText(clockOutStatus);

            userAttendanceRecordDetailsActivityTextViewStatusCode = findViewById(R.id.userAttendanceRecordDetailsActivityTextViewStatusCode);
            if(clockInStatus.equals("On-time") && clockOutStatus.equals("On-time")){
                String acronym = "O";
                userAttendanceRecordDetailsActivityTextViewStatusCode.setText(acronym);
            } else if(clockInStatus.equals("On-time") && clockOutStatus.equals("Undertime")){
                String acronym = "OU";
                userAttendanceRecordDetailsActivityTextViewStatusCode.setText(acronym);
            } else if(clockInStatus.equals("Late") && clockOutStatus.equals("On-time")){
                String acronym = "LO";
                userAttendanceRecordDetailsActivityTextViewStatusCode.setText(acronym);
            } else if(clockInStatus.equals("Late") && clockOutStatus.equals("Undertime")){
                String acronym = "LU";
                userAttendanceRecordDetailsActivityTextViewStatusCode.setText(acronym);
            } else {
                String acronym = "A";
                userAttendanceRecordDetailsActivityTextViewStatusCode.setText(acronym);
            }
        }
    }
}