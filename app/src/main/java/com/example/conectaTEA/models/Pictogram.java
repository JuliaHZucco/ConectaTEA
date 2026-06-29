package com.example.conectaTEA.models;

public class Pictogram {
    private String id;
    private String name;
    private String imageUrl;
    private String imageBase64;
    private String imageMimeType;
    private String tableId;
    private String category;
    private String borderColor;

    public Pictogram() {}

    public Pictogram(String id, String name, String imageUrl, String imageBase64, String imageMimeType,
                     String tableId, String category, String borderColor) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.imageBase64 = imageBase64;
        this.imageMimeType = imageMimeType;
        this.tableId = tableId;
        this.category = category;
        this.borderColor = borderColor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getImageMimeType() {
        return imageMimeType;
    }

    public void setImageMimeType(String imageMimeType) {
        this.imageMimeType = imageMimeType;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }
}