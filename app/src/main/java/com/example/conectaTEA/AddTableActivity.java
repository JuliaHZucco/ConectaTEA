package com.example.conectaTEA;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddTableActivity extends BaseActivity  {

    private EditText etTableName;
    private Button btnSaveTable;
    private String childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_table);

        setupBackButton();

        etTableName = findViewById(R.id.etTableName);
        btnSaveTable = findViewById(R.id.btnSaveTable);
        childId = getIntent().getStringExtra("CHILD_ID");

        btnSaveTable.setOnClickListener(v -> {
            String tableName = etTableName.getText().toString().trim();

            if (tableName.isEmpty()) {
                Toast.makeText(this, "Informe o nome da tabela de pictogramas", Toast.LENGTH_SHORT).show();
                return;
            }

            if (childId == null) {
                Toast.makeText(this, "Erro: Criança não identificada.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSaveTable.setEnabled(false);
            btnSaveTable.setText("Salvando...");

            String tableCode = generateTableCode();
            String parentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Map<String, Object> table = new HashMap<>();
            table.put("name", tableName);
            table.put("code", tableCode);
            table.put("childId", childId);
            table.put("parentId", parentId);

            FirebaseFirestore.getInstance().collection("pictogramTables")
                    .add(table)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "Tabela criada com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnSaveTable.setEnabled(true);
                        btnSaveTable.setText("Salvar tabela");
                        Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private String generateTableCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder("TBL-");
        Random rnd = new Random();
        while (code.length() < 9) {
            int index = (int) (rnd.nextFloat() * chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }
}