package com.vicgong.temperature;

import com.vicgong.MrAPI.MinimalMapReduce;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MaxTemptRunJob extends Configured implements Tool {

    public static class MaxTemptMapper extends Mapper<LongWritable, Text, IntPair, Text> {
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String[] contents = value.toString().trim().split("\t");
            if(contents.length == 2){
                try {
                    Date date = sdf.parse(contents[0]);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    int year = c.get(Calendar.YEAR);
                    int temperature = Integer.parseInt(contents[1].substring(0, contents[1].indexOf("C")));
                    IntPair intPair = new IntPair();
                    intPair.setTemperature(temperature);
                    intPair.setYear(year);
                    context.write(intPair, value);;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class MaxTemptReducer extends Reducer<IntPair, Text, IntPair, Text> {
        @Override
        protected void reduce(IntPair intPair, Iterable<Text> iterable, Context context)
                throws IOException, InterruptedException {
            for(Text i : iterable){
                context.write(intPair, i);
            }
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        if(args.length != 2){
            System.err.printf("Usage: %s [generic options] <inpath> <outpath>\n", getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            System.exit(2);
        }
        Configuration config = getConf();
        Job job = Job.getInstance(config);
        job.setJarByClass(MaxTemptRunJob.class);
        job.setMapperClass(MaxTemptMapper.class);
        job.setReducerClass(MaxTemptReducer.class);
        job.setPartitionerClass(FirstPartitioner.class);
        job.setSortComparatorClass(KeyComparator.class);
        job.setGroupingComparatorClass(GroupComparator.class);
        job.setMapOutputKeyClass(IntPair.class);
        job.setMapOutputValueClass(Text.class);
        job.setNumReduceTasks(3);
        FileInputFormat.addInputPath(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new MaxTemptRunJob(),args);
        System.exit(exitCode);
    }
}
