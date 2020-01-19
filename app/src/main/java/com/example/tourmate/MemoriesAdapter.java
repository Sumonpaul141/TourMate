package com.example.tourmate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MemoriesAdapter extends RecyclerView.Adapter<MemoriesAdapter.ViewHolder> {

    private List<Memories> memories;
    protected Context context;

    public MemoriesAdapter(List<Memories> memories, Context context) {
        this.memories = memories;
        this.context = context;
    }

    @NonNull
    @Override
    public MemoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_memories_item_design, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoriesAdapter.ViewHolder holder, int position) {

        final Memories memory= memories.get(position);
        holder.memoriesTripNameTv.setText(memory.getTripName());
        Picasso.get().load(memory.getImageLink()).into(holder.memoriesImageIv);



    }

    @Override
    public int getItemCount() {
        return memories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView memoriesImageIv;
        private TextView memoriesTripNameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            memoriesImageIv = itemView.findViewById(R.id.memoriesImageIv);
            memoriesTripNameTv = itemView.findViewById(R.id.memoriesTripNameTv);

        }
    }
}
