package com.example.tourmate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class ExpenceAdapter extends RecyclerView.Adapter<ExpenceAdapter.ViewHolder> {
    private List<Expence> expences;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String userId, tripId, expenceId;





    public ExpenceAdapter(List<Expence> expences, Context context) {
        this.expences = expences;
        this.context = context;
    }

    @NonNull
    @Override
    public ExpenceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_tour_expence_design, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenceAdapter.ViewHolder holder, int position) {

        final Expence expence = expences.get(position);
        holder.expenceName.setText(expence.getExpenceName());
        holder.expenceAmount.setText(expence.getExpenceAmount());

        expenceId = expence.getExpenceId();
        userId = firebaseAuth.getCurrentUser().getUid();
        tripId = expence.getTripId();

        holder.expenceDeleteIV.setOnClickListener(new View.OnClickListener() {
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
                        expenceId = expence.getExpenceId();
                        DatabaseReference expenceDeleteRef = databaseReference.child("Users").child(userId).child("Trips").child(tripId).child("Expences").child(expenceId);
                        expenceDeleteRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
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

        holder.expenceEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.alart_expence, null);
                builder.setView(view);
                final Dialog dialog = builder.create();
                dialog.show();
                final EditText expenceName = view.findViewById(R.id.expenceNameEt);
                final EditText expenceAmount = view.findViewById(R.id.expenceAmountEt);
                Button addExpenceButton = view.findViewById(R.id.addExpenceButton);
                Button updateExpenceButton = view.findViewById(R.id.updateExpenceButton);
                addExpenceButton.setVisibility(View.GONE);
                expenceName.setText(expence.getExpenceName());
                expenceAmount.setText(expence.getExpenceAmount());
                updateExpenceButton.setVisibility(View.VISIBLE);

                updateExpenceButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = expenceName.getText().toString();
                        String amount = expenceAmount.getText().toString();

                        if (name.isEmpty() || amount.isEmpty()){
                            if (name.isEmpty()){
                                Toast.makeText(context, "Expence for what?", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(context, "You forgot to fill the amount", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            expenceId = expence.getExpenceId();
                            DatabaseReference expenceEditRef = databaseReference.child("Users").child(userId).child("Trips").child(tripId).child("Expences").child(expenceId);
                            Expence expencees = new Expence(expenceId, name, amount, tripId);
                            expenceEditRef.setValue(expencees).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return expences.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView expenceAmount, expenceName;
        private ImageView expenceDeleteIV, expenceEditIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            expenceAmount = itemView.findViewById(R.id.expenceAmountTv);
            expenceName = itemView.findViewById(R.id.expenceNameTv);
            expenceDeleteIV = itemView.findViewById(R.id.expenceDeleteIV);
            expenceEditIV = itemView.findViewById(R.id.expenceEditIV);
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
    }
}
