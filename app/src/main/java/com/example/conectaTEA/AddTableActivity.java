package com.example.conectaTEA;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddTableActivity extends BaseActivity  {

    private EditText etTableName;
    private Spinner spTableBorderColor;
    private Button btnSaveTable;
    private String childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_table);

        setupBackButton();

        etTableName = findViewById(R.id.etTableName);
        spTableBorderColor = findViewById(R.id.spTableBorderColor);
        btnSaveTable = findViewById(R.id.btnSaveTable);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.border_colors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTableBorderColor.setAdapter(adapter);

        childId = getIntent().getStringExtra("CHILD_ID");

        btnSaveTable.setOnClickListener(v -> {
            String tableName = etTableName.getText().toString().trim();

            String rawColor = spTableBorderColor.getSelectedItem().toString();
            if (spTableBorderColor.getSelectedItemPosition() == 0) {
                rawColor = "";
            }
            String borderColor = rawColor.isEmpty() ? null : normalizeColor(rawColor);

            if (tableName.isEmpty()) {
                Toast.makeText(this, "Informe o nome da tabela de pictogramas", Toast.LENGTH_SHORT).show();
                return;
            }

            if (borderColor == null) {
                Toast.makeText(this, "Selecione uma cor para a moldura da tabela", Toast.LENGTH_SHORT).show();
                return;
            }

            if (childId == null) {
                Toast.makeText(this, "Erro: Criança não identificada.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSaveTable.setEnabled(false);
            btnSaveTable.setText("Salvando...");

            fetchChildCodeAndSave(tableName, borderColor);
        });
    }

    private void fetchChildCodeAndSave(String tableName, String borderColor) {
        FirebaseFirestore.getInstance().collection("children").document(childId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String studentCode = documentSnapshot.getString("code");
                    
                    if (studentCode == null || studentCode.isEmpty() || studentCode.equals("SEM-CODIGO")) {
                        studentCode = generateChildCode();
                        String finalStudentCode = studentCode;
                        FirebaseFirestore.getInstance().collection("children").document(childId)
                                .update("code", studentCode)
                                .addOnSuccessListener(aVoid -> saveTable(tableName, finalStudentCode, borderColor))
                                .addOnFailureListener(e -> {
                                    btnSaveTable.setEnabled(true);
                                    btnSaveTable.setText("Salvar tabela");
                                    Toast.makeText(this, "Erro ao gerar código para o aluno: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        saveTable(tableName, studentCode, borderColor);
                    }
                })
                .addOnFailureListener(e -> {
                    btnSaveTable.setEnabled(true);
                    btnSaveTable.setText("Salvar tabela");
                    Toast.makeText(this, "Erro ao buscar código do aluno: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveTable(String tableName, String studentCode, String borderColor) {
        String parentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> table = new HashMap<>();
        table.put("name", tableName);
        table.put("code", studentCode);
        table.put("childId", childId);
        table.put("parentId", parentId);
        table.put("borderColor", borderColor);

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
    }

    private String generateChildCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder("ALU-");
        Random rnd = new Random();
        while (code.length() < 9) {
            int index = (int) (rnd.nextFloat() * chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }
}