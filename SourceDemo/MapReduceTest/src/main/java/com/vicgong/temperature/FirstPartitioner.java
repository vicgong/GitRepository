package com.vicgong.temperature;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class FirstPartitioner extends Partitioner<IntPair, Text> {

    @Override
    public int getPartition(IntPair key, Text value, int numPartitions) {
        return Math.abs(key.getYear() * 127) % numPartitions;
    }
}
