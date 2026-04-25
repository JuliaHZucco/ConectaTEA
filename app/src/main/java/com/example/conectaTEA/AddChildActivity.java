package com.example.conectaTEA;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddChildActivity extends BaseActivity {

    private EditText etChildName, etChildInfo;
    private Button btnSaveChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        setupBackButton();

        etChildName = findViewById(R.id.etChildName);
        etChildInfo = findViewById(R.id.etChildInfo);
        btnSaveChild = findViewById(R.id.btnSaveChild);

        btnSaveChild.setOnClickListener(v -> {
            String childName = etChildName.getText().toString().trim();
            String childInfo = etChildInfo.getText().toString().trim();

            if (childName.isEmpty()) {
                Toast.makeText(this, "Informe o nome da criança", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSaveChild.setEnabled(false);
            btnSaveChild.setText("Salvando...");

            String parentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Map<String, Object> child = new HashMap<>();
            child.put("name", childName);
            child.put("info", childInfo);
            child.put("parentId", parentId);

            FirebaseFirestore.getInstance().collection("children")
                    .add(child)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Criança cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                        startActivity(new android.content.Intent(this, ChildrenListActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnSaveChild.setEnabled(true);
                        btnSaveChild.setText("Salvar");
                        Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}