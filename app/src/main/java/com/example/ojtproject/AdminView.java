package com.example.ojtproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminView extends AppCompatActivity implements UserAdapter.OnItemClickListener {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<ReadWriteUserDetails> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        getSupportActionBar().setTitle("Admin");

        searchView = findViewById(R.id.search_view);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this); // Pass 'this' as the itemClickListener
        recyclerView.setAdapter(userAdapter);

        // Retrieve the user data from Firebase and add it to the userList
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Registered Users");
        usersRef.orderByChild("userType").equalTo("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    ReadWriteUserDetails user = userSnapshot.getValue(ReadWriteUserDetails.class);
                    if (user != null) {
                        user.setUserId(userSnapshot.getKey()); // Set the userId from the snapshot key
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
            }
        });
    }

    private void filterList(String text) {
        List<ReadWriteUserDetails> filteredList = new ArrayList<>();
        for (ReadWriteUserDetails readWriteUserDetails : userList) {
            if (readWriteUserDetails.getFullName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(readWriteUserDetails);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show();
        } else {
            userAdapter.setFilteredList(filteredList);
        }

    }

    @Override
    public void onItemClick(ReadWriteUserDetails user) {

    }

    //Creating ActionBar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.admin_logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //When any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        FirebaseAuth authProfile;

        switch (item.getItemId()){


            case R.id.adminLogoutMenuItemLogout:
                authProfile = FirebaseAuth.getInstance();
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);//So when phone is opened, user will be redirected to the MainFragment instead of HomePageActivity
                editor.apply();
                Intent intent = new Intent(AdminView.this, MainActivity.class);

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
}