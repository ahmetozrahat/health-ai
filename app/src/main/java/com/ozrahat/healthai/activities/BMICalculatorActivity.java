package com.ozrahat.healthai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.ozrahat.healthai.R;
import com.ozrahat.healthai.models.BMI;
import com.ozrahat.healthai.models.Units;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * BMI Calculator Activity.
 *
 * @author ahmetozrahat25
 * @version 1.0.0
 * @since 2021-03-01
 */
public class BMICalculatorActivity extends AppCompatActivity {

    private TextInputEditText heightInput;
    private TextInputEditText weightInput;
    private AutoCompleteTextView heightUnitInput;
    private AutoCompleteTextView weightUnitInput;

    private Button calculateButton;

    private static final double INCH = 2.54;
    private static final double LBS = 0.45359237;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_calculator);

        // Setup UI Components
        setupComponents();

        // Handle click events for UI elements.
        setupListeners();
    }

    private void setupComponents() {
        heightInput = findViewById(R.id.bmi_calculator_height_input);
        weightInput = findViewById(R.id.bmi_calculator_weight_input);
        heightUnitInput = findViewById(R.id.bmi_calculator_height_unit_input);
        weightUnitInput = findViewById(R.id.bmi_calculator_weight_unit_input);

        calculateButton = findViewById(R.id.bmi_calculator_calculate_button);

        // Setting-up the array adapter for dropdown menu of Height Unit.

        String[] heightUnitItems = new String[]{Units.CM.label, Units.INCH.label};
        ArrayAdapter<String> heightUnitAdapter = new ArrayAdapter<>(
                BMICalculatorActivity.this,
                android.R.layout.simple_dropdown_item_1line,
                heightUnitItems);
        heightUnitInput.setAdapter(heightUnitAdapter);

        // Setting-up the array adapter for dropdown menu of Weight Unit.

        String[] weightUnitItems = new String[]{Units.KG.label, Units.LBS.label};
        ArrayAdapter<String> weightUnitAdapter = new ArrayAdapter<>(
                BMICalculatorActivity.this,
                android.R.layout.simple_dropdown_item_1line,
                weightUnitItems);
        weightUnitInput.setAdapter(weightUnitAdapter);
    }

    private void setupListeners() {
        calculateButton.setOnClickListener(v -> {
            if(heightInput.getText().toString().isEmpty() || weightInput.getText().toString().isEmpty()
            || heightUnitInput.getText().toString().isEmpty() || weightUnitInput.getText().toString().isEmpty()){
                // User entered insufficient info.
                Toast.makeText(this, getString(R.string.warning_fill_the_blanks), Toast.LENGTH_SHORT).show();
            }else {
                // Calculate and show the BMI.
                double bmi = calculateBMI(
                        Double.valueOf(heightInput.getText().toString()),
                        Double.valueOf(weightInput.getText().toString()),
                        heightUnitInput.getText().toString().equals(Units.CM.label) ? Units.CM : Units.INCH,
                        weightUnitInput.getText().toString().equals(Units.KG.label) ? Units.KG : Units.LBS);

                BMI bmiClass = classificateBMI(bmi);

                DecimalFormat bmiFormat = new DecimalFormat("#.##");

                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("status", 1);
                    jsonObject.put("code", 10);
                    jsonObject.put("message",
                            getString(R.string.alert_dialog_bmi_result)
                            .replace("%bmi", bmiFormat.format(bmi))
                            .replace("%class", bmiClass.label));

                    ChatLogActivity activity = ChatLogActivity.getInstance();
                    activity.addMessageToChatlog(jsonObject.toString(), 0);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Calculates BMI using formula: kg/m^2
     *
     * @param height Height as double
     * @param weight Weight as double
     * @param heightUnit Height unit: cm or inch
     * @param weightUnit Weight unit: kg or lbs
     * @return BMI as double
     */
    private double calculateBMI(Double height, Double weight, Units heightUnit, Units weightUnit){
        double newHeight, newWeight;

        newHeight = heightUnit == Units.CM ? height/100 : (height*INCH)/100;
        newWeight = weightUnit == Units.KG ? weight : weight*LBS;

        return newWeight / Math.pow(newHeight, 2);
    }

    /**
     * Finds the Enum value of BMI.
     *
     * @param bmi Body Mass Index as double
     * @return BMI Enum Type.
     */
    private BMI classificateBMI(Double bmi){
        if(bmi < 18.5){
            // Underweight
            return BMI.UNDER_WEIGHT;
        }else if(bmi >= 18.5 && bmi <= 24.9){
            // Normal weight
            return BMI.NORMAL_WEIGHT;
        }else if(bmi >= 25.0 && bmi <= 29.9){
            // Overweight
            return BMI.OVER_WEIGHT;
        }else if(bmi >= 30.0 && bmi <= 34.9){
            // Class 1 obesity
            return BMI.CLASS1_OBESITY;
        }else if(bmi >= 35.0 && bmi <= 39.9){
            // Class 2 obesity
            return BMI.CLASS2_OBESITY;
        }else if(bmi >= 40){
            return BMI.CLASS3_OBESITY;
            // Class 3 obesity
        }
        return BMI.UNDER_WEIGHT;
    }
}