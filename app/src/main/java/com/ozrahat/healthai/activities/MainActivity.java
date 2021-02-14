package com.ozrahat.healthai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ozrahat.healthai.R;

public class MainActivity extends AppCompatActivity {

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

        // Check if the user is logged in or not.
        checkUser();
    }

    private void setupComponents() {
        openChatsButton = findViewById(R.id.main_open_chats_button);
    }

    private void setupListeners() {
        openChatsButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChatLogActivity.class)));
    }

    private void checkUser() {
        // When the activity starts, we should check if the user logged in.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            // User has logged in.
        }else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}