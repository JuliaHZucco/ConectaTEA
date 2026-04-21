package com.example.conectaTEA;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TableDetailsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private TextView tvTableTitle, tvCode;
    private Button btnAddByLink, btnPickImage, btnManageAccess, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_details);

        tvTableTitle = findViewById(R.id.tvTableTitle);
        tvCode = findViewById(R.id.tvCode);
        btnAddByLink = findViewById(R.id.btnAddByLink);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnManageAccess = findViewById(R.id.btnManageAccess);
        btnBack = findViewById(R.id.btnBack);

        String tableName = getIntent().getStringExtra("TABLE_NAME");
        tvTableTitle.setText(tableName);
        tvCode.setText("Código da tabela: TBL-8KQ2L");

        btnAddByLink.setOnClickListener(v ->
                startActivity(new Intent(this, AddPictogramActivity.class)));

        btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnManageAccess.setOnClickListener(v ->
                startActivity(new Intent(this, ManageTableAccessActivity.class)));

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            Toast.makeText(this, "Imagem selecionada: " + imageUri, Toast.LENGTH_SHORT).show();
        }
    }
}