package com.ozrahat.healthai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ozrahat.healthai.R;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;

    private Button loginButton;
    private MaterialButton registerButton;
    private MaterialButton skipButton;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Setup UI Components
        setupComponents();

        // Handle click events for UI elements.
        setupListeners();

        // Initialize Firebase Auth.
        firebaseAuth = FirebaseAuth.getInstance();
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
        final String userName = usernameInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if(userName.isEmpty() || password.isEmpty()) {
            // User entered insufficient credentials.
            Toast.makeText(this, getString(R.string.warning_fill_the_blanks), Toast.LENGTH_SHORT).show();
        }else {
            // Handle the login process.
            loginWithCredentials(userName, password);
        }
    }

    private void loginWithCredentials(String email, String password){
        // Sign in user with Firebase Auth.
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        // Sign in was successfull, check if user verified his/her email.
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                        if(currentUser != null){
                            if(currentUser.isEmailVerified()){
                                // User has verified his/her email, send the user to the MainActivity.
                                finish();
                                startActivity(new Intent(this, MainActivity.class));
                            }else {
                                // User hasn't been verified his/her email.
                                // Show them a dialog.
                                new MaterialAlertDialogBuilder(this)
                                        .setTitle(getString(R.string.alert_dialog_warning))
                                        .setMessage(getString(R.string.alert_dialog_not_verified))
                                        .setPositiveButton(getString(R.string.button_send_again), ((dialog, which) ->
                                                sendVerificationEmail(currentUser, email)))
                                        .setNeutralButton(getString(R.string.button_positive), ((dialog, which) -> {}))
                                        .show();
                            }
                        }
                    }else {
                        // An error has occurred. Toast a message.
                        if(task.getException() != null){
                            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationEmail(FirebaseUser user, String email) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        // Re-sent verification email is successful.
                        new MaterialAlertDialogBuilder(this)
                                .setTitle(getString(R.string.alert_dialog_success))
                                .setMessage(getString(R.string.alert_dialog_verification_sent)
                                .replace("%email", email))
                                .setPositiveButton(getString(R.string.button_positive), ((dialog, which) -> {}))
                                .show();
                    }else {
                        // Re-sent verification email failed. Toast a message.
                        if(task.getException() != null){
                            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}