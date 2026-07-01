package com.example.conectaTEA;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Locale;

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

    protected String normalizeColor(String rawColor) {
        if (rawColor == null) return null;
        String colorText = rawColor.trim();
        if (colorText.isEmpty()) return null;
        String key = colorText.toLowerCase(Locale.ROOT);
        switch (key) {
            case "vermelho": return "#F44336";
            case "azul": return "#2196F3";
            case "verde": return "#4CAF50";
            case "amarelo": return "#FFEB3B";
            case "laranja": return "#FF9800";
            case "roxo": return "#9C27B0";
            case "lilás":
            case "lilas": return "#B39DDB";
            case "rosa": return "#E91E63";
            case "marrom": return "#795548";
            case "cinza": return "#9E9E9E";
            case "preto": return "#000000";
            case "branco": return "#FFFFFF";
        }
        if (colorText.matches("^[0-9a-fA-F]{6}$")) {
            colorText = "#" + colorText;
        }
        try {
            int parsedColor = Color.parseColor(colorText);
            return String.format(Locale.ROOT, "#%06X", (0xFFFFFF & parsedColor));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}