// UserAdapter.java
package com.example.ojtproject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    Context context;
    private List<ReadWriteUserDetails> userList;
    private OnItemClickListener itemClickListener;

    public UserAdapter(List<ReadWriteUserDetails> userList, OnItemClickListener itemClickListener, Context context) {
        this.userList = userList;
        this.itemClickListener = itemClickListener;
        this.context = context;
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
        holder.genderTextView.setText(user.getGender());
        holder.birthdayTextView.setText(user.getBirthDate());
        Glide.with(context).load(user.getImageUrl()).into(holder.userImageView);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(ReadWriteUserDetails user);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

            // Set click listener on the itemView
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ReadWriteUserDetails user = userList.get(position);
                itemClickListener.onItemClick(user);
            }
        }
    }
}