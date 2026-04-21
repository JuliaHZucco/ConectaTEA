package com.example.conectaTEA;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddPictogramActivity extends AppCompatActivity {

    private EditText etPictogramName, etPictogramLink;
    private Button btnSavePictogram, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pictogram);

        etPictogramName = findViewById(R.id.etPictogramName);
        etPictogramLink = findViewById(R.id.etPictogramLink);
        btnSavePictogram = findViewById(R.id.btnSavePictogram);
        btnBack = findViewById(R.id.btnBack);

        btnSavePictogram.setOnClickListener(v -> {
            String name = etPictogramName.getText().toString().trim();
            String link = etPictogramLink.getText().toString().trim();

            if (name.isEmpty() || link.isEmpty()) {
                Toast.makeText(this, "Preencha nome e link do pictograma", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Pictograma adicionado: " + name, Toast.LENGTH_SHORT).show();
            finish();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}