// UserAdapter.java
package com.example.ojtproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    Context context;
    private List<ReadWriteUserDetails> userList;

    public UserAdapter(List<ReadWriteUserDetails> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    public void setFilteredList(List<ReadWriteUserDetails> filteredList) {
        // below line is to add our filtered
        // list in our course array list.
        this.userList = filteredList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_admin, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        ReadWriteUserDetails user = userList.get(position);
        holder.nameTextView.setText(user.getFullName());
        holder.genderTextView.setText(user.getGender());
        holder.birthdayTextView.setText(user.getBirthDate());
        holder.userImageView.setImageResource(R.drawable.ic_baseline_person_24);
        if(user.getImageUrl() != null) {
            String[] imageRefParts = user.getImageUrl().split("/");
            String imageRef = new String(imageRefParts[3] + "/" + imageRefParts[4]);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(imageRef);
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                Glide.with(context)
                        .load(storageRef)
                        .centerCrop()
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                // Handle the error here
                                System.out.println("Ito ung error: " + e.getMessage());
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                // Image loaded successfully
                                return false;
                            }
                        })
                        .into(holder.userImageView);
            });
        }
        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context, UserAttendanceAdminView.class);
            intent.putExtra("userID", user.getUserId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(ReadWriteUserDetails user);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView userImageView;
        private TextView nameTextView;
        private TextView genderTextView;
        private TextView birthdayTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.user_picture);
            nameTextView = itemView.findViewById(R.id.username);
            genderTextView = itemView.findViewById(R.id.gender);
            birthdayTextView = itemView.findViewById(R.id.birthday);
        }
    }
}