package com.ozrahat.healthai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.ozrahat.healthai.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText password2Input;

    private Button registerButton;
    private MaterialButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Setup UI Components
        setupComponents();

        // Handle click events for UI elements.
        setupListeners();
    }

    private void setupComponents() {
        usernameInput = findViewById(R.id.register_page_username_input);
        emailInput = findViewById(R.id.register_page_email_input);
        passwordInput = findViewById(R.id.register_page_password_input);
        password2Input = findViewById(R.id.register_page_password2_input);

        registerButton = findViewById(R.id.register_page_register_button);
        loginButton = findViewById(R.id.register_page_login_button);
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> handleRegister());

        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void handleRegister() {
        if(usernameInput.getText().toString().isEmpty() || emailInput.getText().toString().isEmpty() ||
                passwordInput.getText().toString().isEmpty() || password2Input.getText().toString().isEmpty()) {
            // User entered insufficient credentials.
            Toast.makeText(this, getString(R.string.warning_fill_the_blanks), Toast.LENGTH_SHORT).show();
        }else {
            if(passwordInput.getText().toString().equals(password2Input.getText().toString())) {
                // Passwords do not match, warn the user.
                Toast.makeText(this, getString(R.string.warning_passwords_not_match), Toast.LENGTH_SHORT).show();
            }else {
                // Handle the registration process somehow.
            }
        }
    }
}