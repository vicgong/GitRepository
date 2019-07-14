package com.vicgong.MrAPI;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SecondRel extends Configured implements Tool {

    public static class SecondRelMapper extends Mapper<LongWritable, Text, Text, Text>{
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] name = value.toString().split("-");
            context.write(new Text(name[0]),new Text(name[1]));
            context.write(new Text(name[1]),new Text(name[0]));
        }
    }

    public static class SecondRelReducer extends Reducer<Text, Text, Text, Text>{
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            Set<String> set = new HashSet<String>();
            for(Text i : values){
                set.add(i.toString());
            }
            if(set.size() > 1){
                for(Iterator<String> i = set.iterator(); i.hasNext();){
                    String name = i.next();
                    for(Iterator<String> j = set.iterator(); j.hasNext();){
                        String othername = j.next();
                        if(!name.equals(othername)){
                            context.write(new Text(name),new Text(othername));
                        }
                    }
                }
            }
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        if(args.length != 2){
            System.err.println("Usage: SecondRel <inpath> <outpath>");
            ToolRunner.printGenericCommandUsage(System.out);
            System.exit(2);
        }
        Configuration conf = getConf();
        Job job = Job.getInstance(conf);
        job.setJobName("Second Relative Search");
        job.setJarByClass(SecondRel.class);
        job.setNumReduceTasks(2); //默认为1
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setMapperClass(SecondRelMapper.class);
        job.setReducerClass(SecondRelReducer.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new SecondRel(),args);
        System.exit(exitCode);
    }
}
