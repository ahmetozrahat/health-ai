package com.ozrahat.healthai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ozrahat.healthai.R;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private Button openChatsButton;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup UI Components
        setupComponents();

        // Handle click events for UI elements.
        setupListeners();

        // Initialize Firebase Auth.
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_main_profile){
            checkUser();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupComponents() {
        toolbar = findViewById(R.id.main_toolbar);
        openChatsButton = findViewById(R.id.main_open_chats_button);

        setSupportActionBar(toolbar);
    }

    private void setupListeners() {
        openChatsButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ChatLogActivity.class)));
    }

    private void checkUser() {
        // When the activity starts, we should check if the user logged in.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            // User has logged in.
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        }else {
            // User hasn't logged in.
            Toast.makeText(MainActivity.this, getString(R.string.warning_not_logged_in), Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}