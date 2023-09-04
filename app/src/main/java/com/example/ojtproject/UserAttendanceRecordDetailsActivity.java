package com.example.ojtproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ojtproject.databinding.ActivityUserAttendanceRecordDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;

public class UserAttendanceRecordDetailsActivity extends AppCompatActivity {
    private ActivityUserAttendanceRecordDetailsBinding binding;
    private TextView userAttendanceRecordDetailsActivityTextViewDayDateValue;
    private TextView userAttendanceRecordDetailsActivityTextViewClockInTimeValue;
    private TextView userAttendanceRecordDetailsActivityTextViewClockInStatusValue;
    private TextView userAttendanceRecordDetailsActivityTextViewClockOutTimeValue;
    private TextView userAttendanceRecordDetailsActivityTextViewClockOutStatusValue;
    private TextView userAttendanceRecordDetailsActivityTextViewStatusCode;
    private ProgressBar userAttendanceRecordDetailsActivityProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserAttendanceRecordDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        userAttendanceRecordDetailsActivityProgressBar = findViewById(R.id.userAttendanceRecordDetailsActivityProgressBar);

        //Initialize TextView values with data from UserAttendanceRecordsActivity
        if (intent != null) {
            userAttendanceRecordDetailsActivityProgressBar.setVisibility(View.VISIBLE);
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
            userAttendanceRecordDetailsActivityProgressBar.setVisibility(View.GONE);
        }
    }

    //Creating ActionBar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //When any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.commonMenuItemContactAdmin:
                openEmailAppChooser();
                break;
            case R.id.commonMenuItemLogout:
                FirebaseAuth authProfile = FirebaseAuth.getInstance();
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);//So when phone is opened, user will be redirected to the MainFragment instead of HomePageActivity
                editor.apply();
                Intent intent = new Intent(UserAttendanceRecordDetailsActivity.this, MainActivity.class);

                //Don't let user go back to HomePageActivity or any activities related opened after logout button was tapped if back button of phone is tapped
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                authProfile.signOut();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openEmailAppChooser() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"borjy46@gmail.com"}); // Replace with admin's email address
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact Admin");

        // Check if there's an app that can handle the email intent
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, "Choose an email client:"));
        } else {
            // If no email app is available, open browser to Gmail website
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com/"));
            startActivity(browserIntent);
        }
    }

    public void onBackPressed() {
        String openedFrom = getIntent().getStringExtra("openedFrom");
        if ("UserAttendanceRecordsFragment".equals(openedFrom)) {
            Intent intent = new Intent(UserAttendanceRecordDetailsActivity.this, HomeActivity.class);
            intent.putExtra("openedFrom", "UserAttendanceRecordsFragment");
            startActivity(intent);
        }
    }
}