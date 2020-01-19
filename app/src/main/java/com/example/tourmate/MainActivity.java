package com.example.tourmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser = null;
    private BottomNavigationView bottomNavigationView;
    private Dialog mDialog;
    private TextView dialogEmailTv, dialogNameTv, dialogPhoneTv;
    private DatabaseReference databaseReference;
    private CircleImageView dialogProfileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        checkAuthAndLogout();
        replaceFragment(new TripsFragment());


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navTrips :
                        replaceFragment(new TripsFragment());
                        return true;
                    case R.id.navMemories:
                        replaceFragment(new MemoriesFragment());
                        return true;
                    case R.id.navWallet:
//                        replaceFragment(new WalletFragment());
                        Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });



    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, fragment);
        ft.commit();
    }

    private void checkAuthAndLogout() {
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null){
            mainToSignIn();
        }
    }

    private void mainToSignIn() {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuMapButton:
                startActivity(new Intent(MainActivity.this, MapActivity.class));
                return true;
            case R.id.menuLogoutButton:
                firebaseAuth.signOut();
                mainToSignIn();
                return true;
            case R.id.menuProfileButton:
                profileDialogeSHow();
                return true;
            case R.id.menuWeatherButton:
                sendToWeatherActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendToWeatherActivity() {
        Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
        startActivity(intent);

    }

    private void profileDialogeSHow() {
        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.dialoge_profile_user);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogNameTv = mDialog.findViewById(R.id.dialogNameTv);
        dialogEmailTv = mDialog.findViewById(R.id.dialogEmailTv);
        dialogPhoneTv = mDialog.findViewById(R.id.dialogPhoneTv);
        dialogProfileImage = mDialog.findViewById(R.id.dialogUserImage);

        final DatabaseReference profileImageRef = databaseReference.child("Users").child(currentUser.getUid());
        profileImageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    dialogNameTv.setText(dataSnapshot.child("name").getValue().toString());
                    dialogPhoneTv.setText(dataSnapshot.child("phone").getValue().toString());
                    dialogEmailTv.setText(dataSnapshot.child("email").getValue().toString());
                    if (dataSnapshot.child("ProfileImage").child("imageLink").getValue() != null){
                        String imageLink = dataSnapshot.child("ProfileImage").child("imageLink").getValue().toString();
                        Picasso.get().load(imageLink).into(dialogProfileImage);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDialog.show();
    }

}
