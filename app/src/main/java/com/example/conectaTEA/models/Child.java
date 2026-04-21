package com.example.conectaTEA.models;

public class Child {
    private String id;
    private String nome;
    private String info;
    private String responsavelId;

    public Child(String id, String nome, String info, String responsavelId) {
        this.id = id;
        this.nome = nome;
        this.info = info;
        this.responsavelId = responsavelId;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getInfo() {
        return info;
    }

    public String getResponsavelId() {
        return responsavelId;
    }
}