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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

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
        tvCode.setText(String.format("Código da tabela: %s", tableCode));

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

        btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnManageAccess.setOnClickListener(v -> {
            Intent intent = new Intent(this, TableAccessManagementActivity.class);
            intent.putExtra("TABLE_ID", tableId);
            intent.putExtra("TABLE_NAME", tableName);
            startActivity(intent);
        });
    }

    private void checkUserRole() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String role = doc.getString("profile");
                if ("professor".equals(role)) {
                    btnAddByLink.setVisibility(View.GONE);
                    btnPickImage.setVisibility(View.GONE);
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
            if (imageUri != null) {
                uploadImage(imageUri);
            }
        }
    }

    private void uploadImage(Uri uri) {
        String path = "pictograms/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(path);
        
        Toast.makeText(this, "Fazendo upload da imagem...", Toast.LENGTH_SHORT).show();
        
        ref.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                Intent intent = new Intent(this, AddPictogramActivity.class);
                intent.putExtra("TABLE_ID", tableId);
                intent.putExtra("IMAGE_URL", downloadUri.toString());
                startActivity(intent);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erro no upload: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}