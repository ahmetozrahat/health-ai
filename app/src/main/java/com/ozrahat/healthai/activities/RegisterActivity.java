package com.ozrahat.healthai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.ozrahat.healthai.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText password2Input;

    private Button registerButton;
    private MaterialButton loginButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseFunctions firebaseFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Setup UI Components
        setupComponents();

        // Handle click events for UI elements.
        setupListeners();

        // Initialize Firebase Auth.
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Functions.
        firebaseFunctions = FirebaseFunctions.getInstance();
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
        final String userName = usernameInput.getText().toString();
        final String email = emailInput.getText().toString();
        final String password1 = passwordInput.getText().toString();
        final String password2 = password2Input.getText().toString();

        if(userName.isEmpty() || email.isEmpty() ||
                password1.isEmpty() || password2.isEmpty()) {
            // User entered insufficient credentials.
            Toast.makeText(this, getString(R.string.warning_fill_the_blanks), Toast.LENGTH_SHORT).show();
        }else {
            if(!password1.equals(password2)) {
                // Passwords do not match, warn the user.
                Toast.makeText(this, getString(R.string.warning_passwords_not_match), Toast.LENGTH_SHORT).show();
            }else {
                // Handle the registration process somehow.
                signUpUser(userName, email, password1);
            }
        }
    }

    private void signUpUser(String username, String email, String password) {
        // Sign up the user using email and password.
        // After the successful sign up add username field to the user record.
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        // Sign up successful.

                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                        if(currentUser != null){
                            // We create a map of info and pass it to the Cloud Functions data.
                            Map<String, Object> data = new HashMap<>();
                            data.put("userName", username);
                            data.put("email", email);
                            if(currentUser.getMetadata() != null) {
                                data.put("dateJoined", currentUser.getMetadata().getCreationTimestamp());
                            }

                            firebaseFunctions.getHttpsCallable("createUser")
                                    .call(data)
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()){
                                            // Cloud function task succeeded.
                                            // Check if there is an error.

                                            if(task1.getResult() != null){
                                                Map<String, Object> result = (Map<String, Object>) task1.getResult().getData();

                                                boolean error = (Boolean) result.get("error");

                                                if(!error){
                                                    // No errors, continue.
                                                    // Send a verification email and switch to LoginActivity.
                                                    currentUser.sendEmailVerification().addOnCompleteListener(task2 -> {
                                                        if(task2.isSuccessful()){
                                                            // Sending verification email succeeded. Show a dialog.
                                                            new MaterialAlertDialogBuilder(RegisterActivity.this)
                                                                    .setTitle(getString(R.string.alert_dialog_success))
                                                                    .setMessage(getString(R.string.alert_dialog_verification_sent)
                                                                            .replace("%email", email))
                                                                    .setPositiveButton(getString(R.string.button_positive), (dialog, which) -> {
                                                                        firebaseAuth.signOut();
                                                                        finish();
                                                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                    })
                                                                    .show();
                                                        }else {
                                                            // Sending verification email failed. Toast a message.
                                                            if(task2.getException() != null){
                                                                Toast.makeText(RegisterActivity.this, task2.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                }else {
                                                    // An error occurred. Toast a message.
                                                    Toast.makeText(RegisterActivity.this, (String) result.get("message"), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }else {
                                            // Cloud function task failed. Toast a message.
                                            if(task1.getException() != null){
                                                Toast.makeText(RegisterActivity.this, task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }else {
                        // An error occurred. Toast a message.
                        if(task.getException() != null){
                            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}