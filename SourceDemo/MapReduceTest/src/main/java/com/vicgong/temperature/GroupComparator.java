package com.vicgong.temperature;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class GroupComparator extends WritableComparator {

    //必须要调用父类的构造器
    public GroupComparator() {
        super(IntPair.class, true); //注册comparator
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        IntPair o1 = (IntPair) a;
        IntPair o2 = (IntPair) b;
        return Integer.compare(o1.getYear(),o2.getYear());
    }
}
