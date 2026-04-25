package com.example.conectaTEA;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class TeacherHomeActivity extends BaseActivity {

    private Button btnRequestAccess, btnAuthorizedTables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        setupBackButton();

        btnRequestAccess = findViewById(R.id.btnRequestAccess);
        btnAuthorizedTables = findViewById(R.id.btnAuthorizedTables);

        btnRequestAccess.setOnClickListener(v ->
                startActivity(new Intent(this, RequestAccessActivity.class)));

        btnAuthorizedTables.setOnClickListener(v ->
                startActivity(new Intent(this, AuthorizedTablesActivity.class)));
    }
}