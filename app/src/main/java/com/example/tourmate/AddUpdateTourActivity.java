package com.example.tourmate;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AddUpdateTourActivity extends AppCompatActivity {
    private EditText nameEt, descriptionEt, startDateEt, endDateEt, budgetEt;
    private TextView tripIntro;
    private ImageView tripEditIv;
    private Button addTripButton, updateTripButton;
    private String name, description, startDate, endDate, tripId;
    private String budget;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private String iTripId = "", iTripName, iTripDesc, iTripStartDate, iTripEndDate, iTripBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_tour);
        init();
        setTitle("Add new Trip");
        startDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatePicker(startDateEt);
            }
        });
        endDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatePicker(endDateEt);
            }
        });

        if(getIntent().getStringExtra("tripId") == null) {


            addTripButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getEditTextData();

                    if (name.isEmpty() || description.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || budget.isEmpty()) {
                        if (name.isEmpty()) {
                            Toast.makeText(AddUpdateTourActivity.this, "Trip Name Required", Toast.LENGTH_SHORT).show();
                        } else if (description.isEmpty()) {
                            Toast.makeText(AddUpdateTourActivity.this, "Insert Trip Description", Toast.LENGTH_SHORT).show();
                        } else if (startDate.isEmpty()) {
                            Toast.makeText(AddUpdateTourActivity.this, "When you want to start the Trip?", Toast.LENGTH_SHORT).show();
                        } else if (endDate.isEmpty()) {
                            Toast.makeText(AddUpdateTourActivity.this, "End at??", Toast.LENGTH_SHORT).show();
                        } else if (budget.isEmpty()) {
                            Toast.makeText(AddUpdateTourActivity.this, "No Budget???", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        insertTourData(name, description, startDate, endDate, budget);
                    }
                }
            });

        }else{
            tripIntro.setText("Update your Trip???");
            setTitle("Update Trip Details");
            addTripButton.setVisibility(View.GONE);
            updateTripButton.setVisibility(View.VISIBLE);
            nameEt.setText(getIntent().getStringExtra("tripName"));
            descriptionEt.setText(getIntent().getStringExtra("tripDesc"));
            startDateEt.setText(getIntent().getStringExtra("tripSdate"));
            endDateEt.setText(getIntent().getStringExtra("tripEdate"));
            budgetEt.setText(getIntent().getStringExtra("tripBudget"));
            updateTripButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getEditTextData();

                    if (name.isEmpty() || description.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || budget.isEmpty()) {
                        if (name.isEmpty()) {
                            Toast.makeText(AddUpdateTourActivity.this, "Trip Name Required", Toast.LENGTH_SHORT).show();
                        } else if (description.isEmpty()) {
                            Toast.makeText(AddUpdateTourActivity.this, "Insert Trip Description", Toast.LENGTH_SHORT).show();
                        } else if (startDate.isEmpty()) {
                            Toast.makeText(AddUpdateTourActivity.this, "When you want to start the Trip?", Toast.LENGTH_SHORT).show();
                        } else if (endDate.isEmpty()) {
                            Toast.makeText(AddUpdateTourActivity.this, "End at??", Toast.LENGTH_SHORT).show();
                        } else if (budget.isEmpty()) {
                            Toast.makeText(AddUpdateTourActivity.this, "No Budget???", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        tripId = getIntent().getStringExtra("tripId");
                        updateTourData(name, description, startDate, endDate, budget, tripId);
                    }

                }
            });
////
        }

    }


    private void updateTourData(String name, String description, String startDate, String endDate, String budget, String tripId) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = currentUser.getUid();
        DatabaseReference tripReference = databaseReference.child("Users").child(userId).child("Trips");
        String id = tripId;
        HashMap<String, Object> tripInfo = new HashMap<>();
        tripInfo.put("name", name);
        tripInfo.put("description", description);
        tripInfo.put("startDate", startDate);
        tripInfo.put("endDate", endDate);
        tripInfo.put("budget", budget);
        tripInfo.put("tripId", tripId);
        tripReference.child(tripId).updateChildren(tripInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AddUpdateTourActivity.this, "Tour Updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddUpdateTourActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddUpdateTourActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getEditTextData() {
        name = nameEt.getText().toString();
        description = descriptionEt.getText().toString();
        startDate = startDateEt.getText().toString();
        endDate = endDateEt.getText().toString();
        budget = budgetEt.getText().toString();
    }


    private void getDatePicker(final EditText editText) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String currentDate = year + "/" + month + "/" + dayOfMonth;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = null;

                try {
                    date = dateFormat.parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                editText.setText(dateFormat.format(date));
            }
        };
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void insertTourData(String name, String description, String startDate, String endDate, String budget) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = currentUser.getUid();
        DatabaseReference tripReference = databaseReference.child("Users").child(userId).child("Trips");
        String tripId = tripReference.push().getKey();
        HashMap<String, Object> tripInfo = new HashMap<>();
        tripInfo.put("name", name);
        tripInfo.put("description", description);
        tripInfo.put("startDate", startDate);
        tripInfo.put("endDate", endDate);
        tripInfo.put("budget", budget);
        tripInfo.put("tripId", tripId);
        tripReference.child(tripId).setValue(tripInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddUpdateTourActivity.this, "Tour Added", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddUpdateTourActivity.this, MainActivity.class));
                    finish();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(AddUpdateTourActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        nameEt = findViewById(R.id.tripNameEt);
        descriptionEt = findViewById(R.id.tripDescriptionEt);
        startDateEt = findViewById(R.id.tripStartDateEt);
        endDateEt = findViewById(R.id.tripEndDateEt);
        budgetEt = findViewById(R.id.tripBudgetEt);
        addTripButton = findViewById(R.id.addTripButton);
        updateTripButton = findViewById(R.id.updateTripButton);
        progressBar = findViewById(R.id.progressBar);
        tripIntro = findViewById(R.id.tripIntroTv);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
}
