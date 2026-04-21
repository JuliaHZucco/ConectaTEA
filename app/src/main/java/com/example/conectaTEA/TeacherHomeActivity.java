package com.example.conectaTEA;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherHomeActivity extends AppCompatActivity {

    private Button btnRequestAccess, btnAuthorizedTables, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        btnRequestAccess = findViewById(R.id.btnRequestAccess);
        btnAuthorizedTables = findViewById(R.id.btnAuthorizedTables);
        btnBack = findViewById(R.id.btnBack);

        btnRequestAccess.setOnClickListener(v ->
                startActivity(new Intent(this, RequestAccessActivity.class)));

        btnAuthorizedTables.setOnClickListener(v ->
                startActivity(new Intent(this, AuthorizedTablesActivity.class)));

        btnBack.setOnClickListener(v -> finish());
    }
}