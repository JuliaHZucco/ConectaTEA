package com.example.conectaTEA.models;

public class PictogramTable {
    private String id;
    private String titulo;
    private String codigo;
    private String childId;
    private String responsavelId;

    public PictogramTable(String id, String titulo, String codigo, String childId, String responsavelId) {
        this.id = id;
        this.titulo = titulo;
        this.codigo = codigo;
        this.childId = childId;
        this.responsavelId = responsavelId;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getChildId() {
        return childId;
    }

    public String getResponsavelId() {
        return responsavelId;
    }
}