package com.example.ojtproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private int fragmentToLoad;
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
}