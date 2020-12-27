package com.ozrahat.healthai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.ozrahat.healthai.R;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;

    private Button loginButton;
    private MaterialButton registerButton;
    private MaterialButton skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Setup UI Components
        setupComponents();

        // Handle click events for UI elements.
        setupListeners();
    }

    private void setupComponents() {
        usernameInput = findViewById(R.id.login_page_username_input);
        passwordInput = findViewById(R.id.login_page_password_input);

        loginButton = findViewById(R.id.login_page_login_button);
        registerButton = findViewById(R.id.login_page_register_button);
        skipButton = findViewById(R.id.login_page_skip_button);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> handleLogin());

        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        skipButton.setOnClickListener(v -> finish());
    }

    private void handleLogin() {
        if(usernameInput.getText().toString().isEmpty() || passwordInput.getText().toString().isEmpty()) {
            // User entered insufficient credentials.
            Toast.makeText(this, getString(R.string.warning_fill_the_blanks), Toast.LENGTH_SHORT).show();
        }else {
            // Handle the login process somehow.

        }
    }
}