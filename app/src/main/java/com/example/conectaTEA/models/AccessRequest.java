package com.example.conectaTEA.models;

public class AccessRequest {
    private String id;
    private String professorId;
    private String tabelaId;
    private String status;

    public AccessRequest(String id, String professorId, String tabelaId, String status) {
        this.id = id;
        this.professorId = professorId;
        this.tabelaId = tabelaId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getProfessorId() {
        return professorId;
    }

    public String getTabelaId() {
        return tabelaId;
    }

    public String getStatus() {
        return status;
    }
}