package com.example.huellas.models;

public class Huella {

    private String Huella;
    private String Descripcion;
    private int Thumbnail;

    public Huella(){

    }

    public Huella(String huella, String descripcion, int thumbnail){

        Huella = huella;
        Descripcion = descripcion;
        Thumbnail = thumbnail;

    }

    public String getHuella() {
        return Huella;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public int getThumbnail() {
        return Thumbnail;
    }

    public void setHuella(String huella) {
        Huella = huella;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public void setThumbnail(int thumbnail) {
        Thumbnail = thumbnail;
    }
}
