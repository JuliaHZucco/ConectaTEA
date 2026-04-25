package com.example.conectaTEA;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.conectaTEA.models.Pictogram;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PictogramGridActivity extends BaseActivity {

    private RecyclerView rvPictograms;
    private TextView tvGridTitle;
    private String tableId, tableName;
    private PictogramAdapter adapter;
    private List<Pictogram> pictogramList;
    private ListenerRegistration pictogramsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictogram_grid);
        setupBackButton();

        rvPictograms = findViewById(R.id.rvPictograms);
        tvGridTitle = findViewById(R.id.tvGridTitle);

        tableId = getIntent().getStringExtra("TABLE_ID");
        tableName = getIntent().getStringExtra("TABLE_NAME");

        if (tableName != null) {
            tvGridTitle.setText(tableName);
        }

        pictogramList = new ArrayList<>();
        adapter = new PictogramAdapter(pictogramList);
        rvPictograms.setLayoutManager(new GridLayoutManager(this, 2));
        rvPictograms.setAdapter(adapter);

        loadPictogramsRealtime();
    }

    private void loadPictogramsRealtime() {
        if (tableId == null) return;

        pictogramsListener = FirebaseFirestore.getInstance().collection("pictograms")
                .whereEqualTo("tableId", tableId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Erro ao carregar pictogramas.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    pictogramList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Pictogram p = doc.toObject(Pictogram.class);
                            p.setId(doc.getId());
                            pictogramList.add(p);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pictogramsListener != null) pictogramsListener.remove();
    }

    private class PictogramAdapter extends RecyclerView.Adapter<PictogramAdapter.ViewHolder> {
        private List<Pictogram> list;

        public PictogramAdapter(List<Pictogram> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pictogram, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Pictogram p = list.get(position);
            holder.tvName.setText(p.getName());
            Glide.with(PictogramGridActivity.this)
                    .load(p.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(holder.ivPictogram);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(PictogramGridActivity.this, PictogramDetailActivity.class);
                intent.putExtra("IMAGE_URL", p.getImageUrl());
                intent.putExtra("NAME", p.getName());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivPictogram;
            TextView tvName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivPictogram = itemView.findViewById(R.id.ivPictogram);
                tvName = itemView.findViewById(R.id.tvPictogramName);
            }
        }
    }
}