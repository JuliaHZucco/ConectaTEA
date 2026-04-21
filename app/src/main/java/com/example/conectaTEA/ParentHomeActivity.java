package com.example.conectaTEA;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ParentHomeActivity extends AppCompatActivity {

    private Button btnRegisteredChildren;
    private Button btnAddChild;
    private Button btnAccessRequests;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home);

        btnRegisteredChildren = findViewById(R.id.btnRegisteredChildren);
        btnAddChild = findViewById(R.id.btnAddChild);
        btnAccessRequests = findViewById(R.id.btnAccessRequests);
        btnBack = findViewById(R.id.btnBack);

        btnRegisteredChildren.setOnClickListener(v ->
                startActivity(new Intent(this, ChildrenListActivity.class)));

        btnAddChild.setOnClickListener(v ->
                startActivity(new Intent(this, AddChildActivity.class)));

        btnAccessRequests.setOnClickListener(v ->
                startActivity(new Intent(this, ManageTableAccessActivity.class)));

        btnBack.setOnClickListener(v -> finish());
    }
}