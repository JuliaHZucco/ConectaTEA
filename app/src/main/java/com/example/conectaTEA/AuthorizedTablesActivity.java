package com.example.conectaTEA;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashSet;
import java.util.Set;

public class AuthorizedTablesActivity extends BaseActivity  {

    private LinearLayout containerAuthorizedTables;
    private TextView tvNoAuthorizedTables;
    private ListenerRegistration accessListener;
    private Set<String> processedChildIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorized_tables);

        setupBackButton();

        containerAuthorizedTables = findViewById(R.id.containerAuthorizedTables);
        tvNoAuthorizedTables = findViewById(R.id.tvNoAuthorizedTables);

        loadAuthorizedChildrenRealtime();
    }

    private void loadAuthorizedChildrenRealtime() {
        String teacherId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        accessListener = db.collection("tableAccess")
                .whereEqualTo("teacherId", teacherId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Erro ao carregar acessos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    containerAuthorizedTables.removeAllViews();
                    processedChildIds.clear();
                    
                    if (value == null || value.isEmpty()) {
                        tvNoAuthorizedTables.setVisibility(View.VISIBLE);
                        tvNoAuthorizedTables.setText("Nenhuma tabela autorizada.");
                    } else {
                        tvNoAuthorizedTables.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot doc : value) {
                            String tableId = doc.getString("tableId");
                            fetchChildIdFromTable(tableId);
                        }
                    }
                });
    }

    private void fetchChildIdFromTable(String tableId) {
        if (tableId == null) return;
        FirebaseFirestore.getInstance().collection("pictogramTables").document(tableId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String childId = doc.getString("childId");
                        if (childId != null && !processedChildIds.contains(childId)) {
                            processedChildIds.add(childId);
                            fetchChildDetailsAndAddButton(childId);
                        }
                    }
                });
    }

    private void fetchChildDetailsAndAddButton(String childId) {
        FirebaseFirestore.getInstance().collection("children").document(childId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        addChildButton(name, childId);
                    }
                });
    }

    private void addChildButton(String name, String childId) {
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
            Intent intent = new Intent(this, ChildTablesActivity.class);
            intent.putExtra("CHILD_NAME", name);
            intent.putExtra("CHILD_ID", childId);
            startActivity(intent);
        });

        containerAuthorizedTables.addView(btn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accessListener != null) accessListener.remove();
    }
}