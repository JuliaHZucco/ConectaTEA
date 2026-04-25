package com.example.conectaTEA;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ChildTablesActivity extends BaseActivity {

    private TextView tvChildName;
    private LinearLayout containerTables;
    private FloatingActionButton fabAddTable;
    private String childId, childName;
    private ListenerRegistration tablesListener;
    private boolean isTeacher = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_tables);

        setupBackButton();

        tvChildName = findViewById(R.id.tvChildName);
        containerTables = findViewById(R.id.containerTables);
        fabAddTable = findViewById(R.id.fabAddTable);

        childName = getIntent().getStringExtra("CHILD_NAME");
        childId = getIntent().getStringExtra("CHILD_ID");
        
        tvChildName.setText("Tabelas de pictogramas de " + childName);

        checkUserRole();

        fabAddTable.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTableActivity.class);
            intent.putExtra("CHILD_ID", childId);
            startActivity(intent);
        });

        loadTablesRealtime();
    }

    private void checkUserRole() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String role = doc.getString("profile");
                isTeacher = "professor".equals(role);
                if (isTeacher) {
                    fabAddTable.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadTablesRealtime() {
        if (childId == null) return;

        tablesListener = FirebaseFirestore.getInstance().collection("pictogramTables")
                .whereEqualTo("childId", childId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Erro ao carregar tabelas: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    containerTables.removeAllViews();
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            String name = document.getString("name");
                            String tableId = document.getId();
                            String code = document.getString("code");
                            addTableButton(name, tableId, code);
                        }
                    }
                });
    }

    private void addTableButton(String name, String tableId, String code) {
        Button btn = new Button(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (56 * getResources().getDisplayMetrics().density));
        params.setMargins(0, (int) (12 * getResources().getDisplayMetrics().density), 0, 0);
        btn.setLayoutParams(params);

        btn.setBackgroundResource(R.drawable.bg_button_primary);
        btn.setText(name);
        btn.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.text_white));
        btn.setAllCaps(false);
        btn.setTextSize(16);

        btn.setOnClickListener(v -> {
            Intent intent = new Intent(this, TableDetailsActivity.class);
            intent.putExtra("TABLE_NAME", name);
            intent.putExtra("TABLE_ID", tableId);
            intent.putExtra("TABLE_CODE", code);
            startActivity(intent);
        });

        containerTables.addView(btn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tablesListener != null) tablesListener.remove();
    }
}