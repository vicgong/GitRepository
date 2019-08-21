package com.vicgong.Default;

public class Car3 implements Vehicle {
    public static void main(String[] args) {
        //直接通过接口名调用静态方法，输出：按喇叭!!!
        Vehicle.blowHorn();
    }
}
