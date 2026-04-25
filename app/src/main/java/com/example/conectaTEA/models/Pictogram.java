package com.example.conectaTEA.models;

public class Pictogram {
    private String id;
    private String name;
    private String imageUrl;
    private String tableId;

    public Pictogram() {}

    public Pictogram(String id, String name, String imageUrl, String tableId) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.tableId = tableId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }
}