package com.example.chatapp.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapter.UsersFragmentRecyclerViewAdapter;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {
    View view;
    boolean check = false;
    private ProgressBar userProgressBar;
    private RecyclerView recyclerView;
    private UsersFragmentRecyclerViewAdapter adapter;
    private FirebaseUser firebaseUser;
    private List<User> mUsers;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference userReference = firebaseDatabase.getReference("Users");
    private EditText editTextSearch;
    private FloatingActionButton fabSearch;
    private FloatingActionButton fabCancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_users, container, false);

        userProgressBar = view.findViewById(R.id.progress_bar_user);
        recyclerView = view.findViewById(R.id.users_recyclerView);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        editTextSearch = view.findViewById(R.id.edit_text_search);
        fabSearch = view.findViewById(R.id.fab_search);
        fabCancel = view.findViewById(R.id.fab_cancel);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        mUsers = new ArrayList<>();

        fabCancel.setVisibility(View.GONE);
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearch.setVisibility(View.VISIBLE);
                fabSearch.setVisibility(View.GONE);
                fabCancel.setVisibility(View.VISIBLE);
                editTextSearch.requestFocus();
            }
        });

        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearch.setVisibility(View.GONE);
                fabCancel.setVisibility(View.GONE);
                fabSearch.setVisibility(View.VISIBLE);
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        readUsers();

        return view;
    }

    private void searchUsers(String s) {
        Query query = userReference.orderByChild("search").startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!dataSnapshot.getKey().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                }
                adapter = new UsersFragmentRecyclerViewAdapter(getContext(), mUsers);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readUsers() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userProgressBar.setVisibility(View.GONE);
                if (editTextSearch.getText().toString().equals("")) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        assert user != null;
                        user.setDocumentId(dataSnapshot.getKey());
                        if (!user.getDocumentId().equals(firebaseUser.getUid())) {
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

                    adapter = new UsersFragmentRecyclerViewAdapter(getContext(), mUsers);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}