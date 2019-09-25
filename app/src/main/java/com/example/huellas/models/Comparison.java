package com.example.huellas.models;

public class Comparison {
    private String id;
    private String firstFingerPrint;
    private String secondFingerPrint;
    private Integer coincidencePercentage;
    private String title;

    public Comparison(String id, String firstFingerPrint, String secondFingerPrint, Integer coincidencePercentage, String title) {
        this.id = id;
        this.firstFingerPrint = firstFingerPrint;
        this.secondFingerPrint = secondFingerPrint;
        this.coincidencePercentage = coincidencePercentage;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstFingerPrint() {
        return firstFingerPrint;
    }

    public void setFirstFingerPrint(String firstFingerPrint) {
        this.firstFingerPrint = firstFingerPrint;
    }

    public String getSecondFingerPrint() {
        return secondFingerPrint;
    }

    public void setSecondFingerPrint(String secondFingerPrint) {
        this.secondFingerPrint = secondFingerPrint;
    }

    public Integer getCoincidencePercentage() {
        return coincidencePercentage;
    }

    public void setCoincidencePercentage(Integer coincidencePercentage) {
        this.coincidencePercentage = coincidencePercentage;
    }
}
