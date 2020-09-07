package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Activity.PersonalChatWindowActivity;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersFragmentRecyclerViewAdapter extends RecyclerView.Adapter<UsersFragmentRecyclerViewAdapter.UserViewHolder> {
    private Context mContext;
    private List<User> mUsers;

    public UsersFragmentRecyclerViewAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_list_item_layout, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.chatName.setText(user.getName());
        Picasso.get().load(user.getProfilePicUri()).placeholder(R.drawable.ic_person).into(holder.chatImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonalChatWindowActivity.class);
                intent.putExtra("personalChatWindowUserID", user.getDocumentId());
                intent.putExtra("personalChatWindowImage", user.getProfilePicUri());
                intent.putExtra("personalChatWindowName", user.getName());
                intent.putExtra("onlineStatus", user.getStatus());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView chatImage;
        TextView chatName;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            chatImage = itemView.findViewById(R.id.chatImage);
            chatName = itemView.findViewById(R.id.chatName);
        }
    }
}
