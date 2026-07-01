package com.example.conectaTEA;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class TableDetailsActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private TextView tvTableTitle, tvCode;
    private Button btnViewPictograms, btnAddByLink, btnPickImage, btnManageAccess;
    private String tableId, tableName, tableCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_details);

        setupBackButton();

        tvTableTitle = findViewById(R.id.tvTableTitle);
        tvCode = findViewById(R.id.tvCode);
        btnViewPictograms = findViewById(R.id.btnViewPictograms);
        btnAddByLink = findViewById(R.id.btnAddByLink);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnManageAccess = findViewById(R.id.btnManageAccess);

        tableName = getIntent().getStringExtra("TABLE_NAME");
        tableId = getIntent().getStringExtra("TABLE_ID");
        tableCode = getIntent().getStringExtra("TABLE_CODE");

        tvTableTitle.setText(tableName);
        
        if (tableCode == null || tableCode.equals("SEM-CODIGO")) {
            FirebaseFirestore.getInstance().collection("pictogramTables").document(tableId)
                    .get().addOnSuccessListener(doc -> {
                        String freshCode = doc.getString("code");
                        tvCode.setText(String.format("Código do Aluno: %s", freshCode));
                    });
        } else {
            tvCode.setText(String.format("Código do Aluno: %s", tableCode));
        }

        checkUserRole();

        btnViewPictograms.setOnClickListener(v -> {
            Intent intent = new Intent(this, PictogramGridActivity.class);
            intent.putExtra("TABLE_ID", tableId);
            intent.putExtra("TABLE_NAME", tableName);
            startActivity(intent);
        });

        btnAddByLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPictogramActivity.class);
            intent.putExtra("TABLE_ID", tableId);
            startActivity(intent);
        });

        btnPickImage.setOnClickListener(v -> openImagePicker());

        btnManageAccess.setOnClickListener(v -> {
            Intent intent = new Intent(this, TableAccessManagementActivity.class);
            intent.putExtra("TABLE_ID", tableId);
            intent.putExtra("TABLE_NAME", tableName);
            startActivity(intent);
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void checkUserRole() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String role = doc.getString("profile");

                        if ("professor".equals(role)) {
                            btnAddByLink.setVisibility(View.VISIBLE);
                            btnPickImage.setVisibility(View.VISIBLE);
                            btnManageAccess.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            if (imageUri == null) {
                Toast.makeText(this, "Imagem não encontrada.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
            } catch (Exception ignored) {
            }

            Intent intent = new Intent(this, AddPictogramActivity.class);
            intent.putExtra("TABLE_ID", tableId);
            intent.putExtra("LOCAL_IMAGE_URI", imageUri.toString());
            startActivity(intent);
        }
    }
}