package com.example.conectaTEA;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChildTablesActivity extends AppCompatActivity {

    private TextView tvChildName;
    private Button btnMorningTable, btnSchoolTable, btnNewTable, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_tables);

        tvChildName = findViewById(R.id.tvChildName);
        btnMorningTable = findViewById(R.id.btnMorningTable);
        btnSchoolTable = findViewById(R.id.btnSchoolTable);
        btnNewTable = findViewById(R.id.btnNewTable);
        btnBack = findViewById(R.id.btnBack);

        String childName = getIntent().getStringExtra("CHILD_NAME");
        tvChildName.setText("Tabelas de pictogramas de " + childName);

        btnMorningTable.setOnClickListener(v -> {
            Intent intent = new Intent(this, TableDetailsActivity.class);
            intent.putExtra("TABLE_NAME", "Rotina da manhã");
            startActivity(intent);
        });

        btnSchoolTable.setOnClickListener(v -> {
            Intent intent = new Intent(this, TableDetailsActivity.class);
            intent.putExtra("TABLE_NAME", "Escola");
            startActivity(intent);
        });

        btnNewTable.setOnClickListener(v ->
                startActivity(new Intent(this, AddTableActivity.class)));

        btnBack.setOnClickListener(v -> finish());
    }
}