package com.vicgong.temperature;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class KeyComparator extends WritableComparator {

    //必须要调用父类的构造器
    public KeyComparator() {
        super(IntPair.class, true); //注册comparator
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        IntPair o1 = (IntPair) a;
        IntPair o2 = (IntPair) b;
        int result = Integer.compare(o1.getYear(),o2.getYear());
        if(result != 0){
            return result;
        }
        //设定自定义排序类来改写为年份升序和温度降序
        result = - Integer.compare(o1.getTemperature(), o2.getTemperature());
        return result;
    }
}
