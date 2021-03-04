package com.ozrahat.healthai.activities;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ozrahat.healthai.R;
import com.ozrahat.healthai.adapters.ProfileItemAdapter;
import com.ozrahat.healthai.models.Gender;
import com.ozrahat.healthai.models.ProfileItem;
import com.ozrahat.healthai.models.Units;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private CollapsingToolbarLayout toolbarLayout;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;

    private ProfileItemAdapter profileItemAdapter;
    private ArrayList<ProfileItem> profileItems;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth.
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Firestore.
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Setup UI Components
        setupComponents();

        // Handle click events for UI elements.
        setupListeners();
    }

    private void setupComponents(){
        toolbarLayout = findViewById(R.id.profile_toolbar_layout);
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        fab = findViewById(R.id.profile_fab);
        recyclerView = findViewById(R.id.profile_recyclerview);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Check if user has logged in or not.
        checkUser();
    }

    private void setupListeners(){
        fab.setOnClickListener(view -> Snackbar.make(view, "It will be added soon.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    private void checkUser() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            // User has logged in.

            // We are going to fetch users information from Firestore.
            firebaseFirestore.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        // We should check if the task was successful.
                        if(task.isSuccessful()){
                            // Task was successful, keep going!

                            DocumentSnapshot document = task.getResult();

                            final String name = document.getString("name");
                            final String username = document.getString("userName");
                            final String email = document.getString("email");
                            final Timestamp dateJoined = document.getTimestamp("dateJoined");
                            final String gender = document.getString("gender");
                            final Long height = document.getLong("height");
                            final Long heightUnit = document.getLong("heightUnit");
                            final Long weight = document.getLong("weight");
                            final Long weightUnit = document.getLong("weightUnit");

                            // Setup UI.

                            toolbarLayout.setTitle(name);

                            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

                            profileItems = new ArrayList<>();

                            profileItems.add(new ProfileItem(
                                    R.drawable.ic_baseline_person_24,
                                    getString(R.string.profile_page_username),
                                    username));

                            profileItems.add(new ProfileItem(
                                    R.drawable.ic_baseline_email_24,
                                    getString(R.string.profile_page_email),
                                    email));

                            profileItems.add(new ProfileItem
                                    (R.drawable.ic_baseline_event_24,
                                            getString(R.string.profile_page_datejoined),
                                            getDateFromTimestamp(dateJoined)));

                            if(gender != null){
                                profileItems.add(new ProfileItem(
                                        getGenderIcon(gender),
                                        getString(R.string.profile_page_gender),
                                        gender.equals(Gender.MALE.id) ? getString(R.string.male) : getString(R.string.female)));
                            }

                            profileItems.add(new ProfileItem(
                                    R.drawable.ic_baseline_fitness_center_24,
                                    getString(R.string.profile_page_height),
                                    getHeightString(convertLongtoInt(height), convertLongtoInt(heightUnit))));

                            profileItems.add(new ProfileItem(
                                    R.drawable.ic_baseline_fitness_center_24,
                                    getString(R.string.profile_page_weight),
                                    getWeightString(convertLongtoInt(weight), convertLongtoInt(weightUnit))));

                            profileItemAdapter = new ProfileItemAdapter(this, profileItems);
                            recyclerView.setAdapter(profileItemAdapter);

                        }else {
                            // An error occurred. Toast a message to user.
                            if(task.getException() != null){
                                Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private int convertLongtoInt(Long value){
        try {
            return value.intValue();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return 0;
    }

    private String getDateFromTimestamp(Timestamp timestamp){
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

            return sdf.format(timestamp.toDate());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return "";
    }

    private int getGenderIcon(String gender){
        return gender.equals(Gender.MALE.id) ? R.drawable.ic_baseline_male_24 : R.drawable.ic_baseline_female_24;
    }

    private String getHeightString(int height, int heightUnit){
        if(heightUnit == Units.CM.id || heightUnit == Units.INCH.id){
            return height + " " + (heightUnit == Units.CM.id ? Units.CM.label : Units.INCH.label);
        }else {
            return Units.CM.label;
        }
    }

    private String getWeightString(int weight, int weightUnit){
        if(weightUnit == Units.KG.id || weightUnit == Units.LBS.id){
            return weight + " " + (weightUnit == Units.KG.id ? Units.KG.label : Units.LBS.label);
        }else {
            return Units.KG.label;
        }
    }
}