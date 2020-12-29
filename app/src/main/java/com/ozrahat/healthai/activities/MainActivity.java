package com.ozrahat.healthai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.ozrahat.healthai.R;

public class MainActivity extends AppCompatActivity {

    private Button openChatsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup UI Components
        setupComponents();

        // Handle click events for UI elements.
        setupListeners();

        startActivity(new Intent(this, LoginActivity.class));
    }

    private void setupComponents() {
        openChatsButton = findViewById(R.id.main_open_chats_button);
    }

    private void setupListeners() {
        openChatsButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChatLogActivity.class)));
    }

}