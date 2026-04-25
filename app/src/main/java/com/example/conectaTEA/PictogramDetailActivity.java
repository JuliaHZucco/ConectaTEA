package com.example.conectaTEA;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class PictogramDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictogram_detail);

        ImageView ivFullscreen = findViewById(R.id.ivFullscreen);
        TextView tvDetailName = findViewById(R.id.tvDetailName);
        ImageView btnClose = findViewById(R.id.btnCloseDetail);

        String imageUrl = getIntent().getStringExtra("IMAGE_URL");
        String name = getIntent().getStringExtra("NAME");

        tvDetailName.setText(name);
        Glide.with(this).load(imageUrl).into(ivFullscreen);

        btnClose.setOnClickListener(v -> finish());
    }
}