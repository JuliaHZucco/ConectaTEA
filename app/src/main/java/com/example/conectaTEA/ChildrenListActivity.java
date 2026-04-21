package com.example.conectaTEA;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ChildrenListActivity extends AppCompatActivity {

    private Button btnChildOne, btnChildTwo, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_list);

        btnChildOne = findViewById(R.id.btnChildOne);
        btnChildTwo = findViewById(R.id.btnChildTwo);
        btnBack = findViewById(R.id.btnBack);

        btnChildOne.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChildTablesActivity.class);
            intent.putExtra("CHILD_NAME", "Lucas");
            startActivity(intent);
        });

        btnChildTwo.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChildTablesActivity.class);
            intent.putExtra("CHILD_NAME", "Marina");
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }
}