package com.example.conectaTEA;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Base64;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if (pictogramsListener != null) {
            pictogramsListener.remove();
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private GradientDrawable createBorderDrawable(String colorHex) {
        int borderColor = parseColorOrDefault(colorHex);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.TRANSPARENT);
        drawable.setStroke(dpToPx(5), borderColor);
        drawable.setCornerRadius(dpToPx(20));

        return drawable;
    }

    private int parseColorOrDefault(String colorHex) {
        if (colorHex == null || colorHex.trim().isEmpty()) {
            return Color.parseColor("#9E9E9E");
        }

        try {
            return Color.parseColor(colorHex);
        } catch (IllegalArgumentException e) {
            return Color.parseColor("#9E9E9E");
        }
    }

    private String getSafeCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return "Sem categoria";
        }

        return category;
    }

    private void loadPictogramImage(ImageView imageView, Pictogram pictogram) {
        String imageBase64 = pictogram.getImageBase64();

        if (imageBase64 != null && !imageBase64.trim().isEmpty()) {
            try {
                byte[] imageBytes = Base64.decode(imageBase64, Base64.DEFAULT);

                Glide.with(PictogramGridActivity.this)
                        .load(imageBytes)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .centerCrop()
                        .into(imageView);

                return;
            } catch (Exception ignored) {
                // Se Base64 falhar, tenta carregar por link.
            }
        }

        String fixedImageUrl = normalizeImageUrl(pictogram.getImageUrl());

        Glide.with(PictogramGridActivity.this)
                .load(fixedImageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .into(imageView);
    }

    private String normalizeImageUrl(String rawUrl) {
        if (rawUrl == null) return "";

        String url = rawUrl.trim();

        if (url.isEmpty()) {
            return "";
        }

        String arasaacId = extractArasaacId(url);

        if (arasaacId != null) {
            return buildArasaacDirectImageUrl(arasaacId);
        }

        return url;
    }

    private String extractArasaacId(String url) {
        if (url == null) return null;

        Pattern pagePattern = Pattern.compile("/pictograms/[^/]+/(\\d+)");
        Matcher pageMatcher = pagePattern.matcher(url);

        if (pageMatcher.find()) {
            return pageMatcher.group(1);
        }

        Pattern apiPattern = Pattern.compile("/api/pictograms/(\\d+)");
        Matcher apiMatcher = apiPattern.matcher(url);

        if (apiMatcher.find()) {
            return apiMatcher.group(1);
        }

        return null;
    }

    private String buildArasaacDirectImageUrl(String id) {
        return "https://static.arasaac.org/pictograms/" + id + "/" + id + "_300.png";
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
            holder.tvCategory.setText(getSafeCategory(p.getCategory()));
            holder.rootPictogramBorder.setBackground(createBorderDrawable(p.getBorderColor()));

            loadPictogramImage(holder.ivPictogram, p);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(PictogramGridActivity.this, PictogramDetailActivity.class);
                intent.putExtra("IMAGE_URL", normalizeImageUrl(p.getImageUrl()));
                intent.putExtra("IMAGE_BASE64", p.getImageBase64());
                intent.putExtra("NAME", p.getName());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View rootPictogramBorder;
            ImageView ivPictogram;
            TextView tvName;
            TextView tvCategory;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                rootPictogramBorder = itemView.findViewById(R.id.rootPictogramBorder);
                ivPictogram = itemView.findViewById(R.id.ivPictogram);
                tvName = itemView.findViewById(R.id.tvPictogramName);
                tvCategory = itemView.findViewById(R.id.tvPictogramCategory);
            }
        }
    }
}