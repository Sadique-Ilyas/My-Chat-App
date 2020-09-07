package com.example.chatapp.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.chatapp.Fragment.AccountFragment;
import com.example.chatapp.Fragment.ChatFragment;
import com.example.chatapp.Fragment.UsersFragment;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_users:
                            selectedFragment = new UsersFragment();
                            break;
                        case R.id.nav_home:
                            selectedFragment = new ChatFragment();
                            break;
                        case R.id.nav_account:
                            selectedFragment = new AccountFragment();
                            break;
                    }
                    assert selectedFragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UsersFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    // Checking User is Online or Offline
    public void userStatus(String status) {

        User user = new User();
        user.setStatus(status);

        databaseReference.child(firebaseUser.getUid()).child("status").setValue(user.getStatus());
    }


    @Override
    protected void onResume() {
        super.onResume();
        userStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        userStatus("Offline");
    }
}