package com.example.tourmate;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WalletFragment extends Fragment {
    private FloatingActionButton addExpenceFab;
    private RecyclerView expenceRecyclerView;
    private List<Expence> expenceList;
    private ExpenceAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String tripId, userId, tripName, budget;
    private double totalExpence = 0, leftAmount;
    private TextView totalAmountTv, totalBudgetTv, totalLeftTv;

    public WalletFragment() {
        // Required empty public constructor
    }
    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        getActivity().setTitle("Tour Expenses");
        init(view);

        expenceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        expenceList = new ArrayList<>();
        adapter = new ExpenceAdapter(expenceList, getContext());
        totalAmountTv = view.findViewById(R.id.totalAmountTv);
        totalBudgetTv = view.findViewById(R.id.totalbudgetTv);
        totalLeftTv = view.findViewById(R.id.totalLeftTv);

        Bundle args = getArguments();

        if (args != null){
            tripId = getArguments().getString("tripId");
            userId = getArguments().getString("userId");
            tripName = getArguments().getString("tripName");
            budget = getArguments().getString("budget");
            getActivity().setTitle(tripName+" Expenses");
            totalBudgetTv.setText(budget);

            addExpenceFab.setVisibility(View.VISIBLE);

            addExpenceFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.alart_expence, null);
                    builder.setView(view);
                    final Dialog dialog = builder.create();
                    dialog.show();

                    final EditText expenceName = view.findViewById(R.id.expenceNameEt);
                    final EditText expenceAmount = view.findViewById(R.id.expenceAmountEt);
                    Button addExpenceButton = view.findViewById(R.id.addExpenceButton);
                    addExpenceButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String name = expenceName.getText().toString();
                            String amount = expenceAmount.getText().toString().trim();
                            if (name.isEmpty() || amount.isEmpty()){
                                if (name.isEmpty()){
                                    Toast.makeText(getContext(), "Expence for what?", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getContext(), "You forgot to fill the amount", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                DatabaseReference expenceRef = databaseReference.child("Users").child(userId).child("Trips").child(tripId).child("Expences");
                                String expenceId = expenceRef.push().getKey();
                                Expence expence = new Expence(expenceId, name, amount, tripId);
                                expenceRef.child(expenceId).setValue(expence).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getContext(), "Expence added", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }
                    });
                }
            });

            DatabaseReference expenceRef = databaseReference.child("Users").child(userId).child("Trips").child(tripId).child("Expences");
            expenceRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        expenceList.clear();
                        totalExpence =0;
                        for (DataSnapshot data : dataSnapshot.getChildren()){
                            Expence expence = data.getValue(Expence.class);
                            expenceList.add(expence);
                            expenceRecyclerView.setAdapter(adapter);
                            totalExpence = totalExpence + Double.valueOf(expence.expenceAmount);
                        }
                        totalAmountTv.setText(String.valueOf(totalExpence));
                        leftAmount = Double.valueOf(budget) - totalExpence;
                        totalLeftTv.setText(String.valueOf(leftAmount));

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return view;
    }
    private void init(View view) {
        addExpenceFab = view.findViewById(R.id.addExpenceFab);
        expenceRecyclerView = view.findViewById(R.id.expenceRecyclerView);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
}
