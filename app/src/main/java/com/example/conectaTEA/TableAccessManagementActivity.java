package com.example.conectaTEA;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class TableAccessManagementActivity extends BaseActivity {

    private LinearLayout containerTeachers;
    private TextView tvNoAccess;
    private String tableId, tableName;
    private ListenerRegistration accessListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_management);
        setupBackButton();

        containerTeachers = findViewById(R.id.containerTeachers);
        tvNoAccess = findViewById(R.id.tvNoAccess);

        tableId = getIntent().getStringExtra("TABLE_ID");
        tableName = getIntent().getStringExtra("TABLE_NAME");

        loadAccessListRealtime();
    }

    private void loadAccessListRealtime() {
        if (tableId == null) return;

        accessListener = FirebaseFirestore.getInstance().collection("tableAccess")
                .whereEqualTo("tableId", tableId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    containerTeachers.removeAllViews();
                    if (value == null || value.isEmpty()) {
                        tvNoAccess.setVisibility(View.VISIBLE);
                    } else {
                        tvNoAccess.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot doc : value) {
                            fetchTeacherNameAndAddView(doc);
                        }
                    }
                });
    }

    private void fetchTeacherNameAndAddView(QueryDocumentSnapshot doc) {
        String teacherId = doc.getString("teacherId");
        if (teacherId == null) return;

        FirebaseFirestore.getInstance().collection("users").document(teacherId).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                String teacherName = userDoc.getString("name");
                addTeacherRow(teacherName, doc.getId());
            }
        });
    }

    private void addTeacherRow(String name, String accessId) {
        View row = getLayoutInflater().inflate(R.layout.item_teacher_access, containerTeachers, false);
        TextView tvName = row.findViewById(R.id.tvTeacherName);
        ImageView btnRemove = row.findViewById(R.id.btnRemoveAccess);

        tvName.setText(name);
        btnRemove.setOnClickListener(v -> showRemoveConfirmation(name, accessId));
        containerTeachers.addView(row);
    }

    private void showRemoveConfirmation(String teacherName, String accessId) {
        new AlertDialog.Builder(this)
                .setMessage("Deseja remover o acesso de " + teacherName + " à tabela " + tableName + "?")
                .setPositiveButton("Remover", (dialog, which) -> {
                    FirebaseFirestore.getInstance().collection("tableAccess").document(accessId).delete();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accessListener != null) accessListener.remove();
    }
}