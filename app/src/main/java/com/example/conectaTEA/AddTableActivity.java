package com.example.conectaTEA;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddTableActivity extends AppCompatActivity {

    private EditText etTableName;
    private Button btnSaveTable, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_table);

        etTableName = findViewById(R.id.etTableName);
        btnSaveTable = findViewById(R.id.btnSaveTable);
        btnBack = findViewById(R.id.btnBack);

        btnSaveTable.setOnClickListener(v -> {
            String tableName = etTableName.getText().toString().trim();

            if (tableName.isEmpty()) {
                Toast.makeText(this, "Informe o nome da tabela de pictogramas", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Tabela criada: " + tableName, Toast.LENGTH_SHORT).show();
            finish();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}