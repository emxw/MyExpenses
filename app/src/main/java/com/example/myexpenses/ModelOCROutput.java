package com.example.myexpenses;

public class ModelOCROutput {

    String id, ocrTitle, ocrOutput;

    public ModelOCROutput() {
    }

    public ModelOCROutput(String id, String ocrTitle, String ocrOutput) {
        this.id = id;
        this.ocrTitle = ocrTitle;
        this.ocrOutput = ocrOutput;
        //this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOcrTitle() {
        return ocrTitle;
    }

    public void setOcrTitle(String ocrTitle) {
        this.ocrTitle = ocrTitle;
    }

    public String getOcrOutput() {
        return ocrOutput;
    }

    public void setOcrOutput(String ocrOutput) {
        this.ocrOutput = ocrOutput;
    }

//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
}
