package com.example.chatapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapter.PersonalChatRecyclerViewAdapter;
import com.example.chatapp.Model.Chats;
import com.example.chatapp.Model.User;
import com.example.chatapp.Notifications.APIService;
import com.example.chatapp.Notifications.Client;
import com.example.chatapp.Notifications.Data;
import com.example.chatapp.Notifications.MyResponse;
import com.example.chatapp.Notifications.Sender;
import com.example.chatapp.Notifications.Token;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalChatWindowActivity extends AppCompatActivity {

    PersonalChatRecyclerViewAdapter adapter;
    APIService apiService;
    boolean notify = false;
    private ImageButton backButton;
    private Toolbar toolbar;
    private ImageView personalChatImage;
    private ImageView onlineStatusIcon;
    private TextView onlineStatusText;
    private TextView personalChatName;
    private EditText editText_personalMessage;
    private ImageButton sendMessage;
    private Calendar calendar;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference chatReference = firebaseDatabase.getReference("Chats");
    private DatabaseReference userReference = firebaseDatabase.getReference("Users");
    private List<Chats> mChat;
    private RecyclerView recyclerView;
    private ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat_window);

        // TOOLBAR
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonalChatWindowActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                PersonalChatWindowActivity.this.finish();
            }
        });
        personalChatImage = findViewById(R.id.personalChatImage);
        personalChatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonalChatWindowActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                PersonalChatWindowActivity.this.finish();
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        personalChatName = findViewById(R.id.personalChatName);

        // Initializing Views
        editText_personalMessage = findViewById(R.id.personalMessage);
        sendMessage = findViewById(R.id.sendMessage);
        recyclerView = findViewById(R.id.personalRecyclerView);
        onlineStatusIcon = findViewById(R.id.status_online_icon);
        onlineStatusText = findViewById(R.id.online_status_text);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Message Sender and Receiver ID
        Intent intent = getIntent();
        final String receiverID = intent.getStringExtra("personalChatWindowUserID");
        final String senderId = firebaseUser.getUid();

        // Setting Profile Pic, Name
//        String personalChatWindowImage = intent.getStringExtra("personalChatWindowImage");
//        String personalChatWindowName = intent.getStringExtra("personalChatWindowName");
//        String onlineStatus = intent.getStringExtra("onlineStatus");
//        Picasso.get().load(personalChatWindowImage).placeholder(R.drawable.ic_account).into(personalChatImage);
//        personalChatName.setText(personalChatWindowName);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setDocumentId(dataSnapshot.getKey());
                    if (user.getDocumentId().equals(receiverID)) {
                        personalChatName.setText(user.getName());
                        Picasso.get().load(user.getProfilePicUri()).placeholder(R.drawable.ic_account).into(personalChatImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PersonalChatWindowActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Send Message On Click Listener
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                final String message = editText_personalMessage.getText().toString().trim();
                // Message Sent time
                calendar = Calendar.getInstance();
                String time = DateFormat.getTimeInstance(DateFormat.DATE_FIELD).format(calendar.getTime());
                // Document Name according to current date and time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault());
                final String docName = sdf.format(new Date());
                // Empty the edit text after sending a message
                editText_personalMessage.setText("");
                // Checking if user is sending an empty message
                if (message.isEmpty()) {
                    Toast.makeText(PersonalChatWindowActivity.this, "Write Some Message First", Toast.LENGTH_SHORT).show();
                } else {
                    Chats chats = new Chats(senderId, receiverID, message, time, "Delivered");
                    chatReference.child(docName).setValue(chats)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    chatRoom(senderId, receiverID, message);

                                }
                            })
                            .addOnFailureListener(PersonalChatWindowActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PersonalChatWindowActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }


        });

        // Display Message in Users Personal Chat Box
        showMessage(senderId, receiverID);

        seenMessage(senderId, receiverID);
    }

    private void chatRoom(final String senderId, final String receiverID, String message) {
        final DatabaseReference chatListReference = firebaseDatabase.getReference("ChatList").child(firebaseUser.getUid());

        chatListReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatListReference.child(senderId).child("senderId").setValue(senderId);
                    chatListReference.child(receiverID).child("receiverId").setValue(receiverID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PersonalChatWindowActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        final String msg = message;
        userReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                user.setDocumentId(snapshot.getKey());
                if (user.getDocumentId().equals(firebaseUser.getUid())) {
                    if (notify) {
                        System.out.println("------------------------------------");
                        System.out.println("------------------------------------");
                        System.out.println("------------------------------------");
                        System.out.println(user.getName());
                        System.out.println("------------------------------------");
                        System.out.println("------------------------------------");
                        sendNotification(receiverID, user.getName(), msg);
                    }
                    notify = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PersonalChatWindowActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotification(final String receiverID, final String name, final String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiverID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), msg, name, receiverID, R.mipmap.chat_icon);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(PersonalChatWindowActivity.this, "Failed !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PersonalChatWindowActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void seenMessage(final String senderId, final String receiverId) {
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chats chats = dataSnapshot.getValue(Chats.class);
                    if (chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(receiverId)) {
                        dataSnapshot.getRef().child("isSeen").setValue("Seen");
                        userReference.child(senderId).child("isSeen").setValue("Seen");
                    } else if (!chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(receiverId)) {
                        dataSnapshot.getRef().child("isSeen").setValue("Delivered");
                        userReference.child(senderId).child("isSeen").setValue("Delivered");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PersonalChatWindowActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMessage(final String senderId, final String receiverId) {
        mChat = new ArrayList<>();
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chats chats = dataSnapshot.getValue(Chats.class);
                    chats.setDocumentId(dataSnapshot.getKey());
                    String ChatRoomID = chats.getDocumentId();     //CHAT ROOM ID MAYBE REQUIRED LATER
                    if (chats.getReceiver().equals(receiverId) && chats.getSender().equals(senderId)
                            || chats.getReceiver().equals(senderId) && chats.getSender().equals(receiverId)) {
                        mChat.add(chats);
                    }
                }
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setStackFromEnd(true);
                linearLayoutManager.setSmoothScrollbarEnabled(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter = new PersonalChatRecyclerViewAdapter(mChat);
                if (adapter.getItemCount() != 0) {
                    recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                }
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // If user is in chat box don't show notification
    private void currentUser(String userid) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentUser", userid);
        editor.apply();
    }

    // Checking User is Online or Offline
    public void userStatus(String status) {

        User user = new User();
        user.setStatus(status);

        userReference.child(firebaseUser.getUid()).child("status").setValue(user.getStatus());

        Intent intent = getIntent();
        final String receiverID = intent.getStringExtra("personalChatWindowUserID");

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user1 = dataSnapshot.getValue(User.class);
                    user1.setDocumentId(dataSnapshot.getKey());
                    if (user1.getDocumentId().equals(receiverID)) {
                        System.out.println("-------------------------------------------");
                        System.out.println("-------------------------------------------");
                        System.out.println("-------------------------------------------");
                        System.out.println(receiverID);
                        System.out.println("-------------------------------------------");
                        System.out.println("-------------------------------------------");
                        System.out.println("-------------------------------------------");
                        if (user1.getStatus().equals("Online")) {
                            System.out.println("-------------------------------------------");
                            System.out.println("-------------------------------------------");
                            System.out.println("-------------------------------------------");
                            System.out.println("-----------------ONLINE--------------------");
                            System.out.println("-------------------------------------------");
                            System.out.println("-------------------------------------------");
                            System.out.println("-------------------------------------------");
                            onlineStatusIcon.setVisibility(View.VISIBLE);
                            onlineStatusText.setText("Online");
                        } else {
                            onlineStatusIcon.setVisibility(View.GONE);
                            onlineStatusText.setText("Offline");
//                            calendar = Calendar.getInstance();
//                            String time = DateFormat.getTimeInstance(DateFormat.DATE_FIELD).format(calendar.getTime());
//                            onlineStatusText.setText("Last seen at " +time);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PersonalChatWindowActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userStatus("Online");
        Intent intent = getIntent();
        String receiverID = intent.getStringExtra("personalChatWindowUserID");
        currentUser(receiverID);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        chatReference.removeEventListener(seenListener);
        userStatus("Offline");
        currentUser("none");
//        Intent intent = getIntent();
//        String receiverID = intent.getStringExtra("personalChatWindowUserID");
//        userReference.child(receiverID).child("isSeen").setValue("Delivered");
    }
}