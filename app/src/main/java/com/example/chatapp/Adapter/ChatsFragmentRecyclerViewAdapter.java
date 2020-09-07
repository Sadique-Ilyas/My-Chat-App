package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Activity.PersonalChatWindowActivity;
import com.example.chatapp.Model.Chats;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatsFragmentRecyclerViewAdapter extends RecyclerView.Adapter<ChatsFragmentRecyclerViewAdapter.UserViewHolder> {
    private Context mContext;
    private List<User> mUsers;
    private String theLastMessage, theLastMessageTime;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

    public ChatsFragmentRecyclerViewAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_list_item_layout, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, final int position) {
        final User user = mUsers.get(position);
        holder.chatName.setText(user.getName());
        Picasso.get().load(user.getProfilePicUri()).placeholder(R.drawable.ic_person).into(holder.chatImage);


//         last message
        if (user.getIsSeen().equals("Delivered")) {
            holder.lastTextSeen.setVisibility(View.GONE);
            holder.lastTextUnSeen.setVisibility(View.VISIBLE);
            lastMessage(user.getDocumentId(), holder.lastTextUnSeen, holder.lastTextTime);
        } else {
            holder.lastTextUnSeen.setVisibility(View.GONE);
            holder.lastTextSeen.setVisibility(View.VISIBLE);
            lastMessage(user.getDocumentId(), holder.lastTextSeen, holder.lastTextTime);
        }

        if (user.getStatus().equals("Online")) {
            holder.statusOnline.setVisibility(View.VISIBLE);
        } else {
            holder.statusOnline.setVisibility(View.GONE);
        }

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

    // check for last message
    private void lastMessage(final String receiverID, final TextView last_message, final TextView last_message_time) {
        theLastMessage = "default";
        theLastMessageTime = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chats chats = dataSnapshot.getValue(Chats.class);
                    if (chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(receiverID)
                            || chats.getReceiver().equals(receiverID) && chats.getSender().equals(firebaseUser.getUid())) {
                        theLastMessage = chats.getMessage();
                        theLastMessageTime = chats.getTime();
                    }
                }

                if ("default".equals(theLastMessage)) {
                    last_message.setText("");
                } else {
                    last_message.setText(theLastMessage);
                }

                if ("default".equals(theLastMessageTime)) {
                    last_message_time.setText("");
                } else {
                    last_message_time.setText(theLastMessageTime);
                }
                theLastMessage = "default";
                theLastMessageTime = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView chatImage;
        TextView chatName, lastTextSeen, lastTextUnSeen, lastTextTime;
        ImageView statusOnline;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            chatImage = itemView.findViewById(R.id.chatImage);
            chatName = itemView.findViewById(R.id.chatName);
            statusOnline = itemView.findViewById(R.id.status_online);
            lastTextSeen = itemView.findViewById(R.id.lastTextSeen);
            lastTextUnSeen = itemView.findViewById(R.id.lastTextUnseen);
            lastTextTime = itemView.findViewById(R.id.lastTextTime);
        }
    }
}
