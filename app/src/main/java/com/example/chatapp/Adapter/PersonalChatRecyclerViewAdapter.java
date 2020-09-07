package com.example.chatapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Model.Chats;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class PersonalChatRecyclerViewAdapter extends RecyclerView.Adapter<PersonalChatRecyclerViewAdapter.PersonalChatViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    FirebaseUser firebaseUser;
    private List<Chats> mChats;

    public PersonalChatRecyclerViewAdapter(List<Chats> mChats) {
        this.mChats = mChats;
    }

    @NonNull
    @Override
    public PersonalChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sent_message_right, parent, false);
            return new PersonalChatViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_received_message_left, parent, false);
            return new PersonalChatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalChatViewHolder holder, int position) {
        holder.myMessage.setText(mChats.get(position).getMessage());
        holder.messageTime.setText(mChats.get(position).getTime());
        if (mChats.get(position).getSender().equals(firebaseUser.getUid())) {
            if (position == mChats.size() - 1) {
                if (mChats.get(position).getIsSeen().equals("Seen")) {
                    holder.seenMessage.setVisibility(View.VISIBLE);
                    holder.seenMessage.setText("Seen");
                } else {
                    holder.seenMessage.setVisibility(View.VISIBLE);
                    holder.seenMessage.setText("Delivered");
                }
            } else {
                holder.seenMessage.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChats.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public static class PersonalChatViewHolder extends RecyclerView.ViewHolder {
        private TextView myMessage;
        private TextView messageTime;
        private TextView seenMessage;

        public PersonalChatViewHolder(@NonNull View itemView) {
            super(itemView);

            myMessage = itemView.findViewById(R.id.myMessage);
            messageTime = itemView.findViewById(R.id.messageTime);
            seenMessage = itemView.findViewById(R.id.text_seen);
        }
    }
}