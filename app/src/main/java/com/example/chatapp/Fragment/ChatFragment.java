package com.example.chatapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapter.ChatsFragmentRecyclerViewAdapter;
import com.example.chatapp.Model.Chats;
import com.example.chatapp.Model.User;
import com.example.chatapp.Notifications.Token;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ChatFragment extends Fragment {

    View view;
    boolean check = false;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private RecyclerView recyclerView;
    private ChatsFragmentRecyclerViewAdapter adapter;
    private List<User> mUsers;
    private List<User> users;
    private Set<User> set;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference chatlistReference;
    private DatabaseReference chatReference;
    private DatabaseReference userReference;
    private ProgressBar chatProgressBar;
    private List<String> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        chatProgressBar = view.findViewById(R.id.progress_bar_chat);

        recyclerView = view.findViewById(R.id.chat_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        firebaseDatabase = FirebaseDatabase.getInstance();

        userList = new ArrayList<>();
        mUsers = new ArrayList<>();

        chatReference = FirebaseDatabase.getInstance().getReference("Chats");
        userReference = FirebaseDatabase.getInstance().getReference("Users");
//        chatlistReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
//        chatlistReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                userList.clear();
//                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
//                    userList.add(chatList);
//                }
//                chatList();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

//         delete this
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chats chats = dataSnapshot.getValue(Chats.class);

                    if (chats.getSender().equals(firebaseUser.getUid())) {
                        userList.add(chats.getReceiver());  // Checking if message sender is me, then add the receiver id to userList
                    }
                    if (chats.getReceiver().equals(firebaseUser.getUid())) {
                        userList.add(chats.getSender());    // Checking if message receiver is me, then add the Sender id to userList
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------");
        System.out.println(userList);
        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------");

        readChats();

        showUnreadMessagesCount();

        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    private void showUnreadMessagesCount() {
        firebaseDatabase.getReference("Chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int unread = 0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Chats chats = dataSnapshot.getValue(Chats.class);
                            if (chats.getReceiver().equals(firebaseUser.getUid()) && !chats.getIsSeen().equals("Seen")) {
                                unread++;
                            }
                        }
                        System.out.println("-------------------------------------------------");
                        System.out.println("-------------------------------------------------");
                        System.out.println("-------------------------------------------------");
                        System.out.println("NUMBER OF UNREAD MESSAGES: " + unread);
                        System.out.println("-------------------------------------------------");
                        System.out.println("-------------------------------------------------");
                        System.out.println("-------------------------------------------------");
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateToken(String token) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        databaseReference.child(firebaseUser.getUid()).setValue(token1);
    }

//    private void chatList() {
//        userReference = FirebaseDatabase.getInstance().getReference("Users");
//        userReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mUsers.clear();
//                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                    User user = dataSnapshot.getValue(User.class);
//                    user.setDocumentId(dataSnapshot.getKey());
//                    for (ChatList chatList: userList){
////                        if (user.getDocumentId().equals(chatList.getSenderId())){
//                            if (user.getDocumentId().equals(chatList.getReceiverId())){
//                                mUsers.add(user);
//                            }
////                        }
////                        if (user.getDocumentId().equals(chatList.getReceiverId())){
////                            if (user.getDocumentId().equals(chatList.getSenderId())){
////                                mUsers.add(user);
////                            }
////                        }
//                    }
//                }
//                adapter = new ChatsFragmentRecyclerViewAdapter(getContext(), mUsers);
//                recyclerView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    //     delete this
    private void readChats() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                chatProgressBar.setVisibility(View.GONE);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setDocumentId(dataSnapshot.getKey());
                    for (int i = 0; i < userList.size(); i++) {
                        if (user.getDocumentId().equals(userList.get(i))) {  // Checking if user in userList is equal to the all registered users
                            if (mUsers.size() != 0) {                        // just to get the object of user document
                                for (int j = 0; j < mUsers.size(); j++) {
                                    if (user.getDocumentId().equals(mUsers.get(j).getDocumentId())) {    // Checking if user in mUsers is present,
                                        check = false;                                                  // then don't add the user in mUsers
                                        break;
                                    }
                                    if (!user.getDocumentId().equals(mUsers.get(j).getDocumentId())) {   // if user in mUsers is not present,
                                        check = true;                                                   // then add
                                    }
                                }
                                if (check) {         // Adding user in mUsers
                                    mUsers.add(user);
                                }
                            } else {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                adapter = new ChatsFragmentRecyclerViewAdapter(getContext(), mUsers);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}