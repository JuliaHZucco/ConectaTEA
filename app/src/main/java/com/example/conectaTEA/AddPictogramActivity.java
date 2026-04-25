package com.example.conectaTEA;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPictogramActivity extends BaseActivity {

    private EditText etPictogramName, etPictogramLink;
    private Button btnSavePictogram;
    private String tableId, passedImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pictogram);

        setupBackButton();

        etPictogramName = findViewById(R.id.etPictogramName);
        etPictogramLink = findViewById(R.id.etPictogramLink);
        btnSavePictogram = findViewById(R.id.btnSavePictogram);
        
        tableId = getIntent().getStringExtra("TABLE_ID");
        passedImageUrl = getIntent().getStringExtra("IMAGE_URL");

        if (passedImageUrl != null) {
            etPictogramLink.setText(passedImageUrl);
            etPictogramLink.setEnabled(false); // Link vindo do Storage não deve ser editado manualmente
        }

        btnSavePictogram.setOnClickListener(v -> {
            String name = etPictogramName.getText().toString().trim();
            String link = etPictogramLink.getText().toString().trim();

            if (name.isEmpty() || link.isEmpty()) {
                Toast.makeText(this, "Preencha nome e link do pictograma", Toast.LENGTH_SHORT).show();
                return;
            }

            if (tableId == null) {
                Toast.makeText(this, "Erro: Tabela não identificada.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSavePictogram.setEnabled(false);
            btnSavePictogram.setText("Adicionando...");

            Map<String, Object> pictogram = new HashMap<>();
            pictogram.put("name", name);
            pictogram.put("imageUrl", link);
            pictogram.put("tableId", tableId);

            FirebaseFirestore.getInstance().collection("pictograms")
                    .add(pictogram)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "Pictograma adicionado!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnSavePictogram.setEnabled(true);
                        btnSavePictogram.setText("Salvar pictograma");
                        Toast.makeText(this, translateError(e), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}