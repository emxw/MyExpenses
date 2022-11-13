package com.example.myexpenses;

public class ModelCategory {
    String id, category, uid;

    public ModelCategory() {
    }

    public ModelCategory(String id, String category, String uid) {
        this.id = id;
        this.category = category;
        this.uid = uid;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
