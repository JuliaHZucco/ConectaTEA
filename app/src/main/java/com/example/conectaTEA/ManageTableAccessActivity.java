package com.example.conectaTEA;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ManageTableAccessActivity extends BaseActivity {

    private LinearLayout containerRequests;
    private TextView tvNoRequests;
    private String tableIdFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_table_access);

        setupBackButton();

        containerRequests = findViewById(R.id.containerRequests);
        tvNoRequests = findViewById(R.id.tvNoRequests);
        tableIdFilter = getIntent().getStringExtra("TABLE_ID");

        loadRequests();
    }

    private void loadRequests() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("accessRequests")
                .whereEqualTo("status", "pending")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Erro ao carregar solicitações: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    containerRequests.removeAllViews();
                    if (value == null || value.isEmpty()) {
                        tvNoRequests.setVisibility(View.VISIBLE);
                    } else {
                        boolean found = false;
                        for (QueryDocumentSnapshot doc : value) {
                            String tableId = doc.getString("tableId");
                            if (tableIdFilter != null && !tableIdFilter.equals(tableId)) continue;
                            found = true;
                            addRequestView(doc);
                        }
                        tvNoRequests.setVisibility(found ? View.GONE : View.VISIBLE);
                    }
                });
    }

    private void addRequestView(QueryDocumentSnapshot doc) {
        View view = getLayoutInflater().inflate(R.layout.item_access_request, containerRequests, false);
        
        TextView tvInfo = view.findViewById(R.id.tvRequestInfo);
        Button btnApprove = view.findViewById(R.id.btnApprove);
        Button btnDeny = view.findViewById(R.id.btnDeny);

        String childName = doc.getString("childName");
        String tableCode = doc.getString("tableCode");
        String teacherName = doc.getString("teacherName");

        tvInfo.setText(String.format("Professor(a) %s solicitou acesso.\nTabela de %s\nCódigo: %s", 
                teacherName != null ? teacherName : "Desconhecido", 
                childName, tableCode));

        btnApprove.setOnClickListener(v -> approveRequest(doc));
        btnDeny.setOnClickListener(v -> denyRequest(doc));

        containerRequests.addView(view);
    }

    private void approveRequest(QueryDocumentSnapshot doc) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String requestId = doc.getId();
        String teacherId = doc.getString("teacherId");
        String tableId = doc.getString("tableId");
        String childName = doc.getString("childName");

        db.collection("accessRequests").document(requestId)
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> {
                    Map<String, Object> access = new HashMap<>();
                    access.put("teacherId", teacherId);
                    access.put("tableId", tableId);

                    db.collection("tableAccess").add(access)
                            .addOnSuccessListener(ref -> {
                                createNotification(teacherId, "approved", "Seu acesso à tabela de " + childName + " foi aprovado!");
                                Toast.makeText(this, "Acesso aprovado!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, translateError(e), Toast.LENGTH_SHORT).show());
                });
    }

    private void denyRequest(QueryDocumentSnapshot doc) {
        FirebaseFirestore.getInstance().collection("accessRequests")
                .document(doc.getId())
                .update("status", "denied")
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Acesso negado.", Toast.LENGTH_SHORT).show());
    }

    private void createNotification(String userId, String type, String message) {
        if (userId == null) return;
        Map<String, Object> notif = new HashMap<>();
        notif.put("userId", userId);
        notif.put("type", type);
        notif.put("message", message);
        notif.put("read", false);
        notif.put("timestamp", com.google.firebase.Timestamp.now());
        FirebaseFirestore.getInstance().collection("notifications").add(notif);
    }
}