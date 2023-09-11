package com.example.ojtproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
}