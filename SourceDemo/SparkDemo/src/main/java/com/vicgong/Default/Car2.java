package com.vicgong.Default;

public class Car2 implements Vehicle,FourWheeler {
    @Override
    public void print() {
        Vehicle.super.print();
    }
}
