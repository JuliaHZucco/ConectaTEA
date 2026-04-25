package com.example.conectaTEA;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends BaseActivity {

    private EditText etProfileEmail, etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private Button btnSaveProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupBackButton();

        etProfileEmail = findViewById(R.id.etProfileEmail);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        String currentEmail = getIntent().getStringExtra("USER_EMAIL");
        if (currentEmail != null) {
            etProfileEmail.setText(currentEmail);
        }

        btnSaveProfile.setOnClickListener(v -> {
            String email = etProfileEmail.getText().toString().trim();
            String currentPassword = etCurrentPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmNewPassword.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Informe o e-mail.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Informe um e-mail válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentPassword.isEmpty()) {
                Toast.makeText(this, "Senha atual obrigatória para alterações.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSaveProfile.setEnabled(false);
            btnSaveProfile.setText("Processando...");

            // Reautenticar antes de qualquer mudança
            user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), currentPassword))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Se o e-mail mudou, tenta atualizar no Auth e Firestore
                            if (!email.equals(user.getEmail())) {
                                updateEmail(user, email, newPassword, confirmPassword);
                            } else if (!newPassword.isEmpty()) {
                                // Se só a senha mudou
                                updatePassword(user, newPassword, confirmPassword);
                            } else {
                                btnSaveProfile.setEnabled(true);
                                btnSaveProfile.setText("Salvar");
                                Toast.makeText(this, "Nenhuma alteração detectada.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            btnSaveProfile.setEnabled(true);
                            btnSaveProfile.setText("Salvar");
                            Toast.makeText(this, "Senha atual incorreta.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void updateEmail(FirebaseUser user, String newEmail, String newPassword, String confirmPassword) {
        user.updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseFirestore.getInstance().collection("users")
                                .document(user.getUid())
                                .update("email", newEmail)
                                .addOnSuccessListener(aVoid -> {
                                    if (!newPassword.isEmpty()) {
                                        updatePassword(user, newPassword, confirmPassword);
                                    } else {
                                        completeUpdate("E-mail atualizado com sucesso.");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    btnSaveProfile.setEnabled(true);
                                    btnSaveProfile.setText("Salvar");
                                    Toast.makeText(this, "E-mail no Auth atualizado, mas falhou no Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        handleAuthError(task.getException());
                    }
                });
    }

    private void updatePassword(FirebaseUser user, String newPassword, String confirmPassword) {
        if (newPassword.length() < 6) {
            btnSaveProfile.setEnabled(true);
            btnSaveProfile.setText("Salvar");
            Toast.makeText(this, "A nova senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            btnSaveProfile.setEnabled(true);
            btnSaveProfile.setText("Salvar");
            Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show();
            return;
        }

        user.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        completeUpdate("Perfil e senha atualizados com sucesso.");
                    } else {
                        handleAuthError(task.getException());
                    }
                });
    }

    private void handleAuthError(Exception e) {
        btnSaveProfile.setEnabled(true);
        btnSaveProfile.setText("Salvar");
        Toast.makeText(this, translateError(e), Toast.LENGTH_LONG).show();
    }

    private void completeUpdate(String message) {
        btnSaveProfile.setEnabled(true);
        btnSaveProfile.setText("Salvar");
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }
}