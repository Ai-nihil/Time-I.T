package com.example.ojtproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfileFragment extends Fragment {

    private Button userProfileFragmentButtonEditInfo;
    private TextView userProfileFragmentTextViewWelcomeMessage,
                     userProfileFragmentTextViewName,
                     userProfileFragmentTextViewEmail,
                     userProfileFragmentTextViewBirthday,
                     userProfileFragmentTextViewGender,
                     userProfileFragmentTextViewMobileNumber;
    private ProgressBar userProfileFragmentProgressBar;
    private String fullName, email, birthday, gender, mobileNumber;
    private ImageView userProfileFragmentImageView;
    private LinearLayout userProfileFragmentLinearLayoutCustomSwipeRefreshLayout;
    private CustomSwipeRefreshLayout userProfileFragmentCustomSwipeRefreshLayout;
    private FirebaseAuth authProfile;
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        userProfileFragmentButtonEditInfo = rootView.findViewById(R.id.userProfileFragmentButtonEditInfo);
        userProfileFragmentTextViewWelcomeMessage = rootView.findViewById(R.id.userProfileFragmentTextViewWelcomeMessage);
        userProfileFragmentTextViewName = rootView.findViewById(R.id.userProfileFragmentTextViewName);
        userProfileFragmentTextViewEmail = rootView.findViewById(R.id.userProfileFragmentTextViewEmail);
        userProfileFragmentTextViewBirthday = rootView.findViewById(R.id.userProfileFragmentTextViewBirthday);
        userProfileFragmentTextViewGender = rootView.findViewById(R.id.userProfileFragmentTextViewGender);
        userProfileFragmentTextViewMobileNumber = rootView.findViewById(R.id.userProfileFragmentTextViewMobileNumber);
        userProfileFragmentProgressBar = rootView.findViewById(R.id.userProfileFragmentProgressBar);
        userProfileFragmentLinearLayoutCustomSwipeRefreshLayout = rootView.findViewById(R.id.userProfileFragmentLinearLayoutCustomSwipeRefreshLayout);
        userProfileFragmentCustomSwipeRefreshLayout = rootView.findViewById(R.id.userProfileFragmentCustomSwipeRefreshLayout);

        userProfileFragmentLinearLayoutCustomSwipeRefreshLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    userProfileFragmentCustomSwipeRefreshLayout.setEnabled(false);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    userProfileFragmentCustomSwipeRefreshLayout.setEnabled(true);
                }
                return false;
            }
        });

        // Look up for the the Swipe Container
        //Setup Refresh Listener which triggers new data loading
        userProfileFragmentCustomSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Code to refresh goes here. Call swipeContainer.setRefreshing(false) once the refresh is complete.
//                startActivity(getActivity().getIntent());
//                getActivity().finish();
//                getActivity().overridePendingTransition(0, 0);
//                userAttendanceRecordsFragmentSwipeRefreshLayout.setRefreshing(false);

                fragmentManager = getActivity().getSupportFragmentManager();
                UserProfileFragment userProfileFragment = new UserProfileFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.homeFragmentContainer, userProfileFragment);
                transaction.commit();
            }
        });

        userProfileFragmentButtonEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserUpdateProfileActivity.class);
                intent.putExtra("openedFrom", "UserProfileFragment");
                startActivity(intent);
            }
        });

        //Set OnClicklistener on ImageView to Open UploadProfilePictureActivity
        userProfileFragmentImageView = rootView.findViewById(R.id.userProfileFragmentImageView);
        userProfileFragmentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UploadProfilePictureActivity.class);
                intent.putExtra("openedFrom", "UserProfileFragment");
                startActivity(intent);
            }
        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(getActivity(), "Something went wrong! User's details are not available at this time.", Toast.LENGTH_LONG).show();
        } else {
            userProfileFragmentProgressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
        // Inflate the layout for this fragment
        return rootView;
    }

    private void showUserProfile(FirebaseUser firebaseUser){
        String userID = firebaseUser.getUid();

        //Extracting User Reference from Database for "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readWriteUserDetails != null){
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    birthday = readWriteUserDetails.getBirthDate();
                    gender = readWriteUserDetails.getGender();
                    mobileNumber = readWriteUserDetails.getMobileNumber();

                    userProfileFragmentTextViewWelcomeMessage.setText("Welcome, " + fullName + "!" );
                    userProfileFragmentTextViewName.setText(fullName);
                    userProfileFragmentTextViewEmail.setText(email);
                    userProfileFragmentTextViewBirthday.setText(birthday);
                    userProfileFragmentTextViewGender.setText(gender);
                    userProfileFragmentTextViewMobileNumber.setText(mobileNumber);

                    //Set User DP (After user has uploaded)
                    Uri uri = firebaseUser.getPhotoUrl();

                    //ImageView setImageURI() should not be used with regular URIs. So use Picasso.
                    Picasso.get().load(uri).into(userProfileFragmentImageView);

                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
                userProfileFragmentProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                userProfileFragmentProgressBar.setVisibility(View.GONE);
            }
        });
    }

    public void updateProfileData(String fullName, String birthday, String gender, String mobileNumber) {
        userProfileFragmentTextViewName.setText(fullName);
        userProfileFragmentTextViewBirthday.setText(birthday);
        userProfileFragmentTextViewGender.setText(gender);
        userProfileFragmentTextViewMobileNumber.setText(mobileNumber);
        // You can similarly update other UI elements as needed
    }
}