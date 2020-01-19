package com.example.tourmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    private String userId;
    private EditText fullNameEt, emailEt, passwordEt, phoneNoEt;
    private CircleImageView signUpProfileImage;
    private Button signUpButton;
    private String fullName, email, password, phone;
    private int phoneNoLength;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private Uri uri;
    private Intent imageUri;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        setTitle("Create A new Account");
        signUpProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickUpFromGallary();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullName = fullNameEt.getText().toString();
                email = emailEt.getText().toString();
                password = passwordEt.getText().toString();
                phone = phoneNoEt.getText().toString();
                phoneNoLength = phone.length();


                if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || phoneNoLength !=11){
                    if (fullName.isEmpty()){
                        Toast.makeText(SignUpActivity.this, "Input Full Name", Toast.LENGTH_SHORT).show();
                    } else if (email.isEmpty()){
                        Toast.makeText(SignUpActivity.this, "Input Email", Toast.LENGTH_SHORT).show();
                    } else if (password.isEmpty()){
                        Toast.makeText(SignUpActivity.this, "Input Password", Toast.LENGTH_SHORT).show();
                    } else if (phone.isEmpty()){
                        Toast.makeText(SignUpActivity.this, "Input Phone Number", Toast.LENGTH_SHORT).show();
                    } else if (phoneNoLength != 11){
                        Toast.makeText(SignUpActivity.this, "Input valid Phone Number", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    signUp(fullName, email, password, phone);
                    if (imageUri != null){
                        storeToStorage(imageUri);
                    }

                }
            }
        });
    }
    private void pickUpFromGallary() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 7);
    }

    private void signUp(final String fullName, final String email, String password, final String phone) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {


                    userId = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference reference = databaseReference.child("Users").child(userId);
                    HashMap<String, Object> userInfo = new HashMap<>();
                    userInfo.put("name", fullName);
                    userInfo.put("email", email);
                    userInfo.put("phone", phone);
                    userInfo.put("userId", userId);
                    reference.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        fullNameEt = findViewById(R.id.signUpFullNameEt);
        emailEt = findViewById(R.id.signUpEmailEt);
        passwordEt = findViewById(R.id.signUpPasswordEt);
        phoneNoEt = findViewById(R.id.signUpPhoneNoEt);
        signUpButton = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.progressBar);
        signUpProfileImage = findViewById(R.id.signUpProfileImage);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == 7){
                signUpProfileImage.setImageURI(data.getData());
                imageUri = data;

            }
        }
    }

    private void storeToStorage(Intent data) {
        uri = data.getData();
        String random = UUID.randomUUID().toString();
        final StorageReference imageReference = storageReference.child("UserProfileImages").child(random);
        imageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    imageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                String imageLink = task.getResult().toString();
                                storeToUserDatabase(imageLink, uri);

                            }else {
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(SignUpActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeToUserDatabase(final String imageLink, final Uri uri) {
        DatabaseReference imageDataRef = databaseReference.child("Users").child(userId).child("ProfileImage");
        HashMap<String, Object> tripInfoImage = new HashMap<>();
        tripInfoImage.put("imageLink", imageLink);
        imageDataRef.setValue(tripInfoImage).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void SignUpTvClicked(View view) {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
    }
}
