package com.example.ojtproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class UserProfileFragment extends Fragment {

    private TextView userProfileFragmentTextViewWelcomeMessage,
                     userProfileFragmentTextViewName,
                     userProfileFragmentTextViewEmail,
                     userProfileFragmentTextViewBirthday,
                     userProfileFragmentTextViewGender,
                     userProfileFragmentTextViewMobileNumber;
    private ProgressBar userProfileFragmentProgressBar;
    private String fullName, email, birthday, gender, mobileNumber;
    private ImageView userProfileFragmentImageView;
    private FirebaseAuth authProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        userProfileFragmentTextViewWelcomeMessage = rootView.findViewById(R.id.userProfileFragmentTextViewWelcomeMessage);
        userProfileFragmentTextViewName = rootView.findViewById(R.id.userProfileFragmentTextViewName);
        userProfileFragmentTextViewEmail = rootView.findViewById(R.id.userProfileFragmentTextViewEmail);
        userProfileFragmentTextViewBirthday = rootView.findViewById(R.id.userProfileFragmentTextViewBirthday);
        userProfileFragmentTextViewGender = rootView.findViewById(R.id.userProfileFragmentTextViewGender);
        userProfileFragmentTextViewMobileNumber = rootView.findViewById(R.id.userProfileFragmentTextViewMobileNumber);
        userProfileFragmentProgressBar = rootView.findViewById(R.id.userProfileFragmentProgressBar);

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
}