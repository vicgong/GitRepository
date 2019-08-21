package com.vicgong.Default;

public class Car implements Vehicle,FourWheeler{
    @Override
    public void print(){
        System.out.println("我是一辆四轮汽车!");
    }
}
