package com.example.tourmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    private EditText emailEt, passwordEt;
    private Button signInButton;
    private String email, password;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEt.getText().toString();
                password = passwordEt.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()){
                    if (email.isEmpty()){
                        Toast.makeText(SignInActivity.this, "Email Can't be blank", Toast.LENGTH_SHORT).show();
                    } else if (password.isEmpty()){
                        Toast.makeText(SignInActivity.this, "Input Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    signIn(email, password);

                }
            }
        });
    }

    private void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        signInButton = findViewById(R.id.signInButton);
        emailEt = findViewById(R.id.signInEmailEt);
        passwordEt = findViewById(R.id.signInPasswordEt);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void SignInTvClicked(View view) {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));

    }
}
