package com.example.conectaTEA;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RequestAccessActivity extends AppCompatActivity {

    private EditText etTableCode;
    private Button btnSendRequest, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_access);

        etTableCode = findViewById(R.id.etTableCode);
        btnSendRequest = findViewById(R.id.btnSendRequest);
        btnBack = findViewById(R.id.btnBack);

        btnSendRequest.setOnClickListener(v -> {
            String code = etTableCode.getText().toString().trim();

            if (code.isEmpty()) {
                Toast.makeText(this, "Informe o código da tabela", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Solicitação enviada: " + code, Toast.LENGTH_SHORT).show();
            finish();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}