package com.example.conectaTEA;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class BaseActivity extends AppCompatActivity {

    protected void setupBackButton() {
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    protected String translateError(Exception e) {
        if (e == null) return "Erro desconhecido";
        if (e instanceof FirebaseAuthInvalidUserException) {
            return "Usuário não encontrado.";
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "E-mail ou senha incorretos.";
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            return "Este e-mail já está cadastrado.";
        } else if (e instanceof FirebaseAuthWeakPasswordException) {
            return "Senha muito fraca.";
        } else if (e instanceof FirebaseNetworkException) {
            return "Sem conexão com a internet.";
        }
        return "Erro: " + e.getMessage();
    }
}