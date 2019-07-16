package com.vicgong.temperature;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntPair implements WritableComparable<IntPair> {

    private int year;
    private int temperature;

    //空构造方法防止反序列化抛出异常
    public IntPair(){}

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        //序列化：对象转化为二进制
        dataOutput.writeInt(year);
        dataOutput.writeInt(temperature);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        //反序列化：二进制转化为对象
        this.year = dataInput.readInt();
        this.temperature = dataInput.readInt();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String toString() {
        return year + "\t" + temperature;
    }
    public int hashCode() {
        return (year + "\t" + temperature).hashCode();
    }

    @Override
    public int compareTo(IntPair o) {
        int result = Integer.compare(this.year, o.getYear());
        if(result != 0){
            return result;
        }
        //故意设定键默认为年份升序和温度升序
        //后续通过设定自定义排序类来改写为年份升序和温度降序
        result = Integer.compare(this.temperature, o.getTemperature());
        return result;
    }
}
