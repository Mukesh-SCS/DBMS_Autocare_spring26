package com.germantown.autocare.model;

/**
 * Data model for an inventory part.
 */
public class Part {

    private int partId;
    private String partName;
    private String description;
    private double unitPrice;
    private int quantityInStock;
    private int reorderLevel;

    public Part() {}

    public Part(String partName, String description, double unitPrice, int quantityInStock, int reorderLevel) {
        this.partName = partName;
        this.description = description;
        this.unitPrice = unitPrice;
        this.quantityInStock = quantityInStock;
        this.reorderLevel = reorderLevel;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
}

