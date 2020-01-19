package com.example.tourmate;



import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class MemoriesFragment extends Fragment {
//    private TextView memoriesTripIdTv;
    private FloatingActionButton fabCamera;
    private String tripId, userId, tripName;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri uri;
    private ProgressBar progressBar;
    private LinearLayout cameraLL, galleryLL;
    private List<Memories> memoriesList;
    private RecyclerView memoriesRecyclerView;
    private MemoriesAdapter adapter;
    private ImageView noDataIv;
    private Dialog cameraGallary;


    public MemoriesFragment() {
        // Required empty public constructor
    }


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_memories, container, false);
        getActivity().setTitle("Trip Memories");


        init(view);
        memoriesRecyclerView = view.findViewById(R.id.memoriesRecyclerView);
        memoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        memoriesList = new ArrayList<>();
        adapter = new MemoriesAdapter(memoriesList, getContext());

        Bundle args = getArguments();

        if (args != null) {
            tripId = getArguments().getString("tripId");
            userId = getArguments().getString("userId");
            tripName = getArguments().getString("tripName");
            getActivity().setTitle(tripName +" Memories");
            fabCamera.setVisibility(View.VISIBLE);
            fabCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cameraGallary  = new Dialog(getContext());
                    cameraGallary.setContentView(R.layout.alart_camera_gallery);
                    cameraGallary.show();
                    cameraLL = cameraGallary.findViewById(R.id.cameraLL);
                    galleryLL =cameraGallary.findViewById(R.id.galleryLL);
                    galleryLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pickFromGallery();
                            cameraGallary.dismiss();
                        }
                    });
                    cameraLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pickFromCamera();
                        }
                    });


                }
            });

            DatabaseReference memoriesReference = databaseReference.child("Users").child(userId).child("Trips").child(tripId).child("Memories");
            memoriesReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        memoriesList.clear();
                        progressBar.setVisibility(View.GONE);
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Memories memories= data.getValue(Memories.class);
                            memoriesList.add(memories);
                            memoriesRecyclerView.setAdapter(adapter);

                        }adapter.notifyDataSetChanged();
                    }else{
                        progressBar.setVisibility(View.GONE);
                        noDataIv.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{

            userId = firebaseAuth.getCurrentUser().getUid();
            DatabaseReference memoriesReference = databaseReference.child("Users").child(userId).child("Memories");
            memoriesReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        memoriesList.clear();
                        progressBar.setVisibility(View.GONE);
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Memories memories= data.getValue(Memories.class);
                            memoriesList.add(memories);
                            memoriesRecyclerView.setAdapter(adapter);

                        }adapter.notifyDataSetChanged();
                    }else {
                        progressBar.setVisibility(View.GONE);
                        noDataIv.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        return view;
    }

    private void pickFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }


    private void init(View view) {
//        memoriesTripIdTv = view.findViewById(R.id.memoriesTripIdTv);
        fabCamera = view.findViewById(R.id.fabCamera);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressBar = view.findViewById(R.id.progressBar);
        noDataIv = view.findViewById(R.id.noDataIv);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            if (resultCode == RESULT_OK){
                storeToStorage(data);
                progressBar.setVisibility(View.VISIBLE);
            }
        }else if (requestCode == 1){
            if (resultCode == RESULT_OK){
                storeToStorageCamera(data);
                progressBar.setVisibility(View.VISIBLE);
            }

        }
    }

    private void storeToStorageCamera(Intent data) {
         cameraGallary.dismiss();
        Bundle bundle = data.getExtras();
         Bitmap bitmap = (Bitmap) bundle.get("data");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] datas = baos.toByteArray();
        String random = UUID.randomUUID().toString();
        final StorageReference imageReference = storageReference.child("TripImages").child(random);
        imageReference.putBytes(datas).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    imageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                String imageLink = task.getResult().toString();
                                storeToDatabase(imageLink);
                                storeToUserDatabase(imageLink);
                            }else {
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Uploaded failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeToStorage(Intent data) {
        uri = data.getData();
        String random = UUID.randomUUID().toString();
        final StorageReference imageReference = storageReference.child("TripImages").child(random);
        imageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    imageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                String imageLink = task.getResult().toString();
                                storeToDatabase(imageLink);
                                storeToUserDatabase(imageLink);
                            }else {
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeToUserDatabase(String imageLink) {
        DatabaseReference imageDataRef = databaseReference.child("Users").child(userId).child("Memories");
        String imageId = imageDataRef.push().getKey();
        HashMap<String, Object> tripInfoImage = new HashMap<>();
        tripInfoImage.put("TripName", tripName);
        tripInfoImage.put("imageLink", imageLink);
        tripInfoImage.put("imageId", imageId);
        imageDataRef.child(imageId).setValue(tripInfoImage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void storeToDatabase(String imageLink) {

        DatabaseReference imageDataRef = databaseReference.child("Users").child(userId).child("Trips").child(tripId).child("Memories");
        String imageId = imageDataRef.push().getKey();
        HashMap<String, Object> tripInfoImage = new HashMap<>();
        tripInfoImage.put("TripName", tripName);
        tripInfoImage.put("imageLink", imageLink);
        tripInfoImage.put("imageId", imageId);
        imageDataRef.child(imageId).setValue(tripInfoImage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    noDataIv.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Added to Trip", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
