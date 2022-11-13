package com.example.myexpenses;

public class ModelAnalytics {

    String id, category, totalAmount;

    public ModelAnalytics() {
    }

    public ModelAnalytics(String id, String category, String totalAmount) {
        this.id = id;
        this.category = category;
        this.totalAmount = totalAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
