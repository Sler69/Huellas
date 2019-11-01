package com.example.huellas.models;

public class Minutia {

    private Integer x,y, minutiaType;
    private Double angle;
    private Boolean flag;

    public Minutia(int x, int y, double angle, int minutiaType, boolean flag) {
        this.x = x;
        this.y = y;
        this.minutiaType = minutiaType;
        this.angle = angle;
        this.flag = flag;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getMinutiaType() {
        return minutiaType;
    }

    public double getAngle() {
        return angle;
    }

    public boolean getFlag() {
        return flag;
    }
}
