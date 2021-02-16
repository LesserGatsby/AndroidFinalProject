package com.code.finalproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code.finalproject.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private User[] data;
    private AppCompatActivity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView userEmail;
        public ImageView userImage;

        public View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userListName);
            userEmail = itemView.findViewById(R.id.userListEmail);
            userImage = itemView.findViewById(R.id.userListImage);

            this.itemView = itemView;
        }
    }

    public UserAdapter(User[] data, AppCompatActivity activity) {
        this.data = data;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false);


        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User u = data[position];

        if (u.equals(MainActivity.user)) {
            holder.userName.setText("(YOU) " + u.name);
        } else {
            holder.userName.setText(u.name);
        }
        holder.userEmail.setText(u.email);

        MainActivity.setUserImageForView(activity, u, holder.userImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = u;
                openUser(v, u);
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.length;
    }



    public void openUser(View view, User user) {

        Context context = view.getContext();
        Intent intent = new Intent(context, ViewEditUser.class);

        intent.putExtra("User", user.email);
        context.startActivity(intent);

    }


}
