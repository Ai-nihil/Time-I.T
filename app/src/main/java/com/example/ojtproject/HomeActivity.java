package com.example.ojtproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private int fragmentToLoad;
    FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            loadHomePageFragment(); // Load the initial fragment
        }

        // Retrieve updated profile data from intent
        Intent intent = getIntent();
        if (intent != null) {
            fragmentToLoad = intent.getIntExtra("loadFragment", 0);
            if (fragmentToLoad == 1) { // Replace with fragment identifier
                fragmentToLoad = 0;
                // Programmatically select the bottom navigation menu item
                BottomNavigationView homeFragmentBottomNavigationView = findViewById(R.id.homeFragmentBottomNavigationView);
                homeFragmentBottomNavigationView.setSelectedItemId(R.id.bottomNavigationMenuItemProfile);
                loadUserProfileFragment(); // Custom method to load the fragment
            }
        }

        BottomNavigationView homeFragmentBottomNavigationView = findViewById(R.id.homeFragmentBottomNavigationView);
        homeFragmentBottomNavigationView.setOnItemSelectedListener(item ->{

            switch (item.getItemId()){

                case R.id.bottomNavigationMenuItemHome:
                    loadHomePageFragment();
                    break;
                case R.id.bottomNavigationMenuItemAttendanceRecords:
                    loadUserAttendanceRecordsFragment();
                    break;
                case R.id.bottomNavigationMenuItemProfile:
                    loadUserProfileFragment();
                    break;
                default:
                    loadHomePageFragment();
                    break;
            }

            return true;
        });
    }

    private void loadHomePageFragment() {
        HomePageFragment homePageFragment = new HomePageFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.homeFragmentContainer, homePageFragment);
        transaction.commit();
    }

    private void loadUserAttendanceRecordsFragment() {
        UserAttendanceRecordsFragment userAttendanceRecordsFragment = new UserAttendanceRecordsFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.homeFragmentContainer, userAttendanceRecordsFragment);
        transaction.commit();
    }

    private void loadUserProfileFragment() {
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.homeFragmentContainer, userProfileFragment);
        transaction.commit();
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

            case R.id.commonMenuItemContactAdmin:
                openEmailAppChooser();
                break;
            case R.id.commonMenuItemLogout:
                authProfile = FirebaseAuth.getInstance();
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);//So when phone is opened, user will be redirected to the MainFragment instead of HomePageActivity
                editor.apply();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);

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

}