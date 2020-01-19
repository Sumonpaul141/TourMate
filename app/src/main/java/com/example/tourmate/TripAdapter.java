package com.example.tourmate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    private List<Trip> trips;
    private Context context;
    private View getView;
    private String userId;


    public TripAdapter(List<Trip> trips, Context context) {
        this.trips = trips;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_tour_item_design, parent, false);
        getView = view;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Trip trip = trips.get(position);
        holder.nameTv.setText(trip.getName());
        holder.descTv.setText(trip.getDescription());

        holder.memoriesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();

                bundle.putString("tripId", trip.getTripId());
                bundle.putString("userId", userId);
                bundle.putString("tripName", trip.getName());

                MemoriesFragment memoriesFragment = new MemoriesFragment();
                memoriesFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity) getView.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, memoriesFragment).commit();


            }
        });

        holder.expenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();

                bundle.putString("tripId", trip.getTripId());
                bundle.putString("userId", userId);
                bundle.putString("tripName", trip.getName());
                bundle.putString("budget", trip.getBudget());

                WalletFragment walletFragment = new WalletFragment();
                walletFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity) getView.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, walletFragment).commit();


            }
        });

        holder.detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.details_trip, null);
                builder.setView(view);
                final Dialog dialog = builder.create();
                dialog.show();
                final TextView name = view.findViewById(R.id.tripNameDetails);
                TextView description = view.findViewById(R.id.tripDescriptionDetails);
                TextView startDate = view.findViewById(R.id.startDateDetails);
                TextView endDate = view.findViewById(R.id.endDateDetails);
                TextView budget = view.findViewById(R.id.budgetDetails);
                Button updateDetailsBtn = view.findViewById(R.id.updateButtonDetails);

                name.setText(trip.getName());
                description.setText(trip.getDescription());
                startDate.setText(trip.getStartDate());
                endDate.setText(trip.getEndDate());
                budget.setText(trip.getBudget());

                updateDetailsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(context, AddUpdateTourActivity.class);
                        intent.putExtra("tripId", trip.getTripId());
                        intent.putExtra("tripName", trip.getName());
                        intent.putExtra("tripDesc", trip.getDescription());
                        intent.putExtra("tripSdate", trip.getStartDate());
                        intent.putExtra("tripEdate", trip.getEndDate());
                        intent.putExtra("tripBudget", trip.getBudget());
                        context.startActivity(intent);
                        notifyDataSetChanged();
                        dialog.dismiss();

                    }
                });


            }
        });


        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.alart_warning, null);
                builder.setView(view);
                final Dialog dialog = builder.create();
                dialog.show();
                Button yesBtn = view.findViewById(R.id.yesButton);
                Button noBtn = view.findViewById(R.id.noButton);

                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String tripId = trip.getTripId();
                        String userId = holder.firebaseAuth.getCurrentUser().getUid();
                        DatabaseReference deleteRef = holder.databaseReference.child("Users").child(userId).child("Trips").child(tripId);
                        deleteRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTv, descTv;
        private Button detailsBtn, memoriesBtn, expenceButton, deleteBtn;
        private FirebaseAuth firebaseAuth;
        private DatabaseReference databaseReference;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.tripNameTv);
            descTv = itemView.findViewById(R.id.tripDescriptionTv);
            deleteBtn = itemView.findViewById(R.id.tripDeleteButton);
            detailsBtn = itemView.findViewById(R.id.tripDetailsButton);
            expenceButton = itemView.findViewById(R.id.tripExpenceButton);
            firebaseAuth = FirebaseAuth.getInstance();
            memoriesBtn = itemView.findViewById(R.id.tripMemoriesButton);
            databaseReference = FirebaseDatabase.getInstance().getReference();
            userId = firebaseAuth.getCurrentUser().getUid();
        }
    }
}
