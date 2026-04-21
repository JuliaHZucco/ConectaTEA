package com.example.conectaTEA;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ManageTableAccessActivity extends AppCompatActivity {

    private Button btnApprove, btnDeny, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_table_access);

        btnApprove = findViewById(R.id.btnApprove);
        btnDeny = findViewById(R.id.btnDeny);
        btnBack = findViewById(R.id.btnBack);

        btnApprove.setOnClickListener(v -> {
            // futura lógica de aprovação
        });

        btnDeny.setOnClickListener(v -> {
            // futura lógica de negação
        });

        btnBack.setOnClickListener(v -> finish());
    }
}