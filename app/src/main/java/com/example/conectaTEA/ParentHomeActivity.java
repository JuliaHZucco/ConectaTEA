package com.example.conectaTEA;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class ParentHomeActivity extends BaseActivity {

    private Button btnRegisteredChildren;
    private Button btnAddChild;
    private Button btnAccessRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home);

        setupBackButton();

        btnRegisteredChildren = findViewById(R.id.btnRegisteredChildren);
        btnAddChild = findViewById(R.id.btnAddChild);
        btnAccessRequests = findViewById(R.id.btnAccessRequests);

        btnRegisteredChildren.setOnClickListener(v ->
                startActivity(new Intent(this, ChildrenListActivity.class)));

        btnAddChild.setOnClickListener(v ->
                startActivity(new Intent(this, AddChildActivity.class)));

        btnAccessRequests.setOnClickListener(v ->
                startActivity(new Intent(this, ManageTableAccessActivity.class)));
    }
}