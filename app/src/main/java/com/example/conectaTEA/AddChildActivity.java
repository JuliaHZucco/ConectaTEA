package com.example.conectaTEA;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddChildActivity extends AppCompatActivity {

    private EditText etChildName, etChildInfo;
    private Button btnSaveChild, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        etChildName = findViewById(R.id.etChildName);
        etChildInfo = findViewById(R.id.etChildInfo);
        btnSaveChild = findViewById(R.id.btnSaveChild);
        btnBack = findViewById(R.id.btnBack);

        btnSaveChild.setOnClickListener(v -> {
            String childName = etChildName.getText().toString().trim();
            String childInfo = etChildInfo.getText().toString().trim();

            if (childName.isEmpty()) {
                Toast.makeText(this, "Informe o nome da criança", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Criança cadastrada: " + childName, Toast.LENGTH_SHORT).show();
            finish();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}