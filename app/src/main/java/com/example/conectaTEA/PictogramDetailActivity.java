package com.example.conectaTEA;

import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PictogramDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictogram_detail);

        ImageView ivFullscreen = findViewById(R.id.ivFullscreen);
        TextView tvDetailName = findViewById(R.id.tvDetailName);
        ImageView btnClose = findViewById(R.id.btnCloseDetail);

        String imageUrl = normalizeImageUrl(getIntent().getStringExtra("IMAGE_URL"));
        String imageBase64 = getIntent().getStringExtra("IMAGE_BASE64");
        String name = getIntent().getStringExtra("NAME");

        tvDetailName.setText(name);

        loadImage(ivFullscreen, imageBase64, imageUrl);

        btnClose.setOnClickListener(v -> finish());
    }

    private void loadImage(ImageView imageView, String imageBase64, String imageUrl) {
        if (imageBase64 != null && !imageBase64.trim().isEmpty()) {
            try {
                byte[] imageBytes = Base64.decode(imageBase64, Base64.DEFAULT);

                Glide.with(this)
                        .load(imageBytes)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .into(imageView);

                return;
            } catch (Exception ignored) {
                // Se Base64 falhar, tenta carregar por link.
            }
        }

        Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
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
}