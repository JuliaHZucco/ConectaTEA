package com.example.conectaTEA;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ChildrenListActivity extends BaseActivity  {

    private LinearLayout containerChildren;
    private TextView tvEmptyState;
    private ListenerRegistration childrenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_list);

        setupBackButton();

        containerChildren = findViewById(R.id.containerChildren);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        loadChildrenRealtime();
    }

    private void loadChildrenRealtime() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String parentId = user.getUid();

        childrenListener = FirebaseFirestore.getInstance().collection("children")
                .whereEqualTo("parentId", parentId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Erro ao carregar crianças: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    containerChildren.removeAllViews();
                    if (value == null || value.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot document : value) {
                            String name = document.getString("name");
                            String childId = document.getId();
                            addChildRow(name, childId);
                        }
                    }
                });
    }

    private void addChildRow(String name, String childId) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, (int) (12 * getResources().getDisplayMetrics().density), 0, 0);
        row.setLayoutParams(rowParams);

        Button btn = new Button(this);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, (int) (56 * getResources().getDisplayMetrics().density), 1);
        btn.setLayoutParams(btnParams);
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

        ImageView deleteIcon = new ImageView(this);
        int iconSize = (int) (44 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        iconParams.setMargins((int) (12 * getResources().getDisplayMetrics().density), 0, 0, 0);
        deleteIcon.setLayoutParams(iconParams);
        deleteIcon.setImageResource(android.R.drawable.ic_menu_delete);
        deleteIcon.setPadding((int) (8 * getResources().getDisplayMetrics().density), (int) (8 * getResources().getDisplayMetrics().density), (int) (8 * getResources().getDisplayMetrics().density), (int) (8 * getResources().getDisplayMetrics().density));
        deleteIcon.setBackgroundResource(R.drawable.bg_back_circle);
        deleteIcon.setColorFilter(androidx.core.content.ContextCompat.getColor(this, R.color.title_purple));
        deleteIcon.setOnClickListener(v -> showDeleteConfirmation(childId, name));

        row.addView(btn);
        row.addView(deleteIcon);
        containerChildren.addView(row);
    }

    private void showDeleteConfirmation(String childId, String name) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir criança")
                .setMessage("Tem certeza que deseja excluir " + name + "? Isso apagará todas as tabelas e pictogramas relacionados.")
                .setPositiveButton("Confirmar", (dialog, which) -> deleteChild(childId))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteChild(String childId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        db.collection("pictogramTables").whereEqualTo("childId", childId).get().addOnSuccessListener(tables -> {
            for (DocumentSnapshot table : tables) {
                String tableId = table.getId();
                
                db.collection("pictograms").whereEqualTo("tableId", tableId).get().addOnSuccessListener(pictograms -> {
                    for (DocumentSnapshot pic : pictograms) pic.getReference().delete();
                });
                
                db.collection("accessRequests").whereEqualTo("tableId", tableId).get().addOnSuccessListener(requests -> {
                    for (DocumentSnapshot req : requests) req.getReference().delete();
                });

                db.collection("tableAccess").whereEqualTo("tableId", tableId).get().addOnSuccessListener(accesses -> {
                    for (DocumentSnapshot acc : accesses) acc.getReference().delete();
                });
                
                table.getReference().delete();
            }
            
            db.collection("children").document(childId).delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Criança excluída.", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (childrenListener != null) childrenListener.remove();
    }
}