package com.example.conectaTEA;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RequestAccessActivity extends BaseActivity {

    private EditText etTableCode;
    private Button btnSendRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_access);

        setupBackButton();

        etTableCode = findViewById(R.id.etTableCode);
        btnSendRequest = findViewById(R.id.btnSendRequest);

        btnSendRequest.setOnClickListener(v -> {
            String code = etTableCode.getText().toString().trim();

            if (code.isEmpty()) {
                Toast.makeText(this, "Informe o código da tabela", Toast.LENGTH_SHORT).show();
                return;
            }

            findTableAndSendRequest(code);
        });
    }

    private void findTableAndSendRequest(String code) {
        btnSendRequest.setEnabled(false);
        btnSendRequest.setText("Enviando...");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pictogramTables")
                .whereEqualTo("code", code)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot tableDoc = task.getResult().getDocuments().get(0);
                        sendRequest(tableDoc);
                    } else {
                        btnSendRequest.setEnabled(true);
                        btnSendRequest.setText("Enviar solicitação");
                        Toast.makeText(this, "Tabela não encontrada. Verifique o código.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendRequest(DocumentSnapshot tableDoc) {
        com.google.firebase.auth.FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        
        String teacherId = currentUser.getUid();
        String tableId = tableDoc.getId();
        String tableCode = tableDoc.getString("code");
        String childId = tableDoc.getString("childId");
        String parentId = tableDoc.getString("parentId");

        if (childId == null) {
            Toast.makeText(this, "Erro: Criança não identificada na tabela.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(teacherId).get().addOnSuccessListener(userDoc -> {
            String teacherName = userDoc.getString("name");
            
            db.collection("children").document(childId).get().addOnSuccessListener(childDoc -> {
                String childName = childDoc.getString("name");

                Map<String, Object> request = new HashMap<>();
                request.put("teacherId", teacherId);
                request.put("teacherName", teacherName);
                request.put("tableId", tableId);
                request.put("tableCode", tableCode);
                request.put("childName", childName);
                request.put("status", "pending");

                db.collection("accessRequests").add(request)
                        .addOnSuccessListener(ref -> {
                            createNotification(parentId, "request", "O professor " + teacherName + " solicitou acesso à tabela de " + childName);
                            Toast.makeText(this, "Solicitação enviada!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            btnSendRequest.setEnabled(true);
                            btnSendRequest.setText("Enviar solicitação");
                            Toast.makeText(this, translateError(e), Toast.LENGTH_LONG).show();
                        });
            });
        });
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