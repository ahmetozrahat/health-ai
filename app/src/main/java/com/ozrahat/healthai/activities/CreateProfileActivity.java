package com.ozrahat.healthai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.functions.FirebaseFunctions;
import com.ozrahat.healthai.R;
import com.ozrahat.healthai.models.Gender;
import com.ozrahat.healthai.models.Units;

import java.util.HashMap;
import java.util.Map;

public class CreateProfileActivity extends AppCompatActivity {

    private TextInputEditText nameInput;
    private AutoCompleteTextView genderInput;
    private TextInputEditText heightInput;
    private TextInputEditText weightInput;
    private AutoCompleteTextView heightUnitInput;
    private AutoCompleteTextView weightUnitInput;

    private Button saveButton;

    private FirebaseFunctions firebaseFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        // Setup UI Components
        setupComponents();

        // Handle click events for UI elements.
        setupListeners();

        // Initialize Firebase Functions.
        firebaseFunctions = FirebaseFunctions.getInstance();
    }

    private void setupComponents() {
        nameInput = findViewById(R.id.create_profile_name_input);
        genderInput = findViewById(R.id.create_profile_gender_input);
        heightInput = findViewById(R.id.create_profile_height_input);
        weightInput = findViewById(R.id.create_profile_weight_input);
        heightUnitInput = findViewById(R.id.create_profile_height_unit_input);
        weightUnitInput = findViewById(R.id.create_profile_weight_unit_input);

        saveButton = findViewById(R.id.create_profile_page_save_button);

        // Setting-up the array adapter for dropdown menu of Gender.

        String[] genderItems = new String[]{Gender.MALE.label, Gender.FEMALE.label};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                CreateProfileActivity.this,
                android.R.layout.simple_dropdown_item_1line,
                genderItems);
        genderInput.setAdapter(genderAdapter);

        // Setting-up the array adapter for dropdown menu of Height Unit.

        String[] heightUnitItems = new String[]{Units.CM.label, Units.INCH.label};
        ArrayAdapter<String> heightUnitAdapter = new ArrayAdapter<>(
                CreateProfileActivity.this,
                android.R.layout.simple_dropdown_item_1line,
                heightUnitItems);
        heightUnitInput.setAdapter(heightUnitAdapter);

        // Setting-up the array adapter for dropdown menu of Weight Unit.

        String[] weightUnitItems = new String[]{Units.KG.label, Units.LBS.label};
        ArrayAdapter<String> weightUnitAdapter = new ArrayAdapter<>(
                CreateProfileActivity.this,
                android.R.layout.simple_dropdown_item_1line,
                weightUnitItems);
        weightUnitInput.setAdapter(weightUnitAdapter);
    }

    private void setupListeners() {
        if(nameInput.getText() != null || genderInput.getText() != null
        || heightInput.getText() != null || heightUnitInput.getText() != null
        || weightInput.getText() != null || weightUnitInput.getText() != null){
            saveButton.setOnClickListener(v -> {
                if(nameInput.getText().toString().isEmpty() || genderInput.getText().toString().isEmpty()
                        || heightInput.getText().toString().isEmpty() || heightUnitInput.getText().toString().isEmpty()
                        || weightInput.getText().toString().isEmpty() || weightUnitInput.getText().toString().isEmpty()) {
                    // User entered insufficient credentials.
                    Toast.makeText(this, getString(R.string.warning_fill_the_blanks), Toast.LENGTH_SHORT).show();
                }else {
                    // Save the profile information somehow.
                    try {
                        final int gender = genderInput.getText().toString().equals(Gender.MALE.label) ? Gender.MALE.id : Gender.FEMALE.id;
                        final int height = Integer.parseInt(heightInput.getText().toString());
                        final int heightUnit = heightUnitInput.getText().toString().equals(Units.CM.label) ? Units.CM.id : Units.INCH.id;
                        final int weight = Integer.parseInt(weightInput.getText().toString());
                        final int weightUnit = weightUnitInput.getText().toString().equals(Units.KG.label) ? Units.KG.id : Units.LBS.id;

                        Map<String, Object> data = new HashMap<>();
                        data.put("name", nameInput.getText().toString());
                        data.put("gender", gender);
                        data.put("height", height);
                        data.put("heightUnit", heightUnit);
                        data.put("weight", weight);
                        data.put("weightUnit", weightUnit);

                        updateProfile(data);

                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    private void updateProfile(Map<String, Object> info) {
        firebaseFunctions.getHttpsCallable("updateUser")
                .call(info)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        // Task is successful, check if there is an error.
                        if(task.getResult() != null) {
                            Map<String, Object> result = (Map<String, Object>) task.getResult().getData();

                            boolean error = (Boolean) result.get("error");

                            if(!error){
                                // No errors, continue.
                                finish();
                                startActivity(new Intent(CreateProfileActivity.this, MainActivity.class));
                            }else {
                                // An error occurred. Toast a message.
                                Toast.makeText(CreateProfileActivity.this, (String) result.get("message"), Toast.LENGTH_LONG).show();
                            }
                        }
                    }else {
                        // Task failed. Toast a message.
                        if(task.getException() != null){
                            Toast.makeText(CreateProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}