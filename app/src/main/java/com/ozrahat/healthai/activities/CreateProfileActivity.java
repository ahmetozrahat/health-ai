package com.ozrahat.healthai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ozrahat.healthai.R;

public class CreateProfileActivity extends AppCompatActivity {

    private TextInputLayout nameLayout;
    private TextInputLayout genderLayout;
    private TextInputLayout heightLayout;
    private TextInputLayout weightLayout;
    private TextInputLayout heightUnitLayout;
    private TextInputLayout weightUnitLayout;

    private TextInputEditText nameInput;
    private AutoCompleteTextView genderInput;
    private TextInputEditText heightInput;
    private TextInputEditText weightInput;
    private AutoCompleteTextView heightUnitInput;
    private AutoCompleteTextView weightUnitInput;

    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        // Setup UI Components
        setupComponents();

        // Handle click events for UI elements.
        setupListeners();
    }

    private void setupComponents() {
        nameLayout = findViewById(R.id.create_profile_name_layout);
        genderLayout = findViewById(R.id.create_profile_gender_layout);
        heightLayout = findViewById(R.id.create_profile_height_layout);
        weightLayout = findViewById(R.id.create_profile_weight_layout);
        heightUnitLayout = findViewById(R.id.create_profile_height_unit_layout);
        weightUnitLayout = findViewById(R.id.create_profile_weight_unit_layout);

        nameInput = findViewById(R.id.create_profile_name_input);
        genderInput = findViewById(R.id.create_profile_gender_input);
        heightInput = findViewById(R.id.create_profile_height_input);
        weightInput = findViewById(R.id.create_profile_weight_input);
        heightUnitInput = findViewById(R.id.create_profile_height_unit_input);
        weightUnitInput = findViewById(R.id.create_profile_weight_unit_input);

        saveButton = findViewById(R.id.create_profile_page_save_button);

        // Setting-up the array adapter for dropdown menu of Gender.

        String[] genderItems = new String[]{getString(R.string.male), getString(R.string.female)};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                CreateProfileActivity.this,
                android.R.layout.simple_dropdown_item_1line,
                genderItems);
        genderInput.setAdapter(genderAdapter);

        // Setting-up the array adapter for dropdown menu of Height Unit.

        String[] heightUnitItems = new String[]{getString(R.string.cms), getString(R.string.inches)};
        ArrayAdapter<String> heightUnitAdapter = new ArrayAdapter<>(
                CreateProfileActivity.this,
                android.R.layout.simple_dropdown_item_1line,
                heightUnitItems);
        heightUnitInput.setAdapter(heightUnitAdapter);

        // Setting-up the array adapter for dropdown menu of Weight Unit.

        String[] weightUnitItems = new String[]{getString(R.string.kgs), getString(R.string.lbs)};
        ArrayAdapter<String> weightUnitAdapter = new ArrayAdapter<>(
                CreateProfileActivity.this,
                android.R.layout.simple_dropdown_item_1line,
                weightUnitItems);
        weightUnitInput.setAdapter(weightUnitAdapter);
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> {
            if(nameInput.getText().toString().isEmpty() || genderInput.getText().toString().isEmpty()
            || heightInput.getText().toString().isEmpty() || heightUnitInput.getText().toString().isEmpty()
            || weightInput.getText().toString().isEmpty() || weightUnitInput.getText().toString().isEmpty()) {
                // User entered insufficient credentials.
                Toast.makeText(this, getString(R.string.warning_fill_the_blanks), Toast.LENGTH_SHORT).show();
            }else {
                // Save the profile information somehow.
            }
        });
    }
}