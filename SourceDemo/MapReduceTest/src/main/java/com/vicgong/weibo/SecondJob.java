package com.vicgong.weibo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

public class SecondJob extends Configured implements Tool {

    /**
     * 统计df：词在多少个微博中出现过。
     * @author vicgong
     *
     */
    public static class SecondMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            //获取当前 mapper task的数据片段（split）
            FileSplit fs = (FileSplit) context.getInputSplit();
            if (!fs.getPath().getName().contains("part-r-00003")) {
                //第二个mapreduce的输入是 第一个mapreduce的输出
                //这里只取前三个有效输出（第四个为微博总数，这里不用）
                String[] v = value.toString().trim().split("\t");
                //字符串大于2个字节，表示只对其中的词进行统计
                if (v.length >= 2) {
                    String[] ss = v[0].split("_");
                    if (ss.length >= 2) {
                        String w = ss[0];
                        context.write(new Text(w), new IntWritable(1));
                    }
                } else {
                    System.out.println(value.toString() + "-------------");
                }
            }
        }
    }

    public static class SecondReduce extends Reducer<Text, IntWritable, Text, IntWritable> {
        protected void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for(IntWritable i : values){
                sum = sum + i.get();	//累加次数
            }
            context.write(key, new IntWritable(sum));
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 0) {
            System.err.printf("Usage: %s [generic options] \n", getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            System.exit(-1);
        }
        Configuration config = getConf();
        config.set("FirstJobOutput", "file:///C:/Users/龚明达/IdeaProjects/MapReduceTest/output1");
        config.set("SecondJobOutput", "file:///C:/Users/龚明达/IdeaProjects/MapReduceTest/output2");
        Job job = Job.getInstance(config);
        job.setJarByClass(SecondJob.class);
        job.setJobName("Weibo-SecondJob");
        //设置map任务的输出key类型、value类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(SecondMapper.class);
        job.setCombinerClass(SecondReduce.class);
        job.setReducerClass(SecondReduce.class);
        //mr运行时的输入数据从hdfs的哪个目录中获取
        FileInputFormat.addInputPath(job, new Path(config.get("FirstJobOutput")));
        FileOutputFormat.setOutputPath(job, new Path(config.get("SecondJobOutput")));
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) {
        int exitCode = 2;
        try {
            exitCode = ToolRunner.run(new SecondJob(), args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(exitCode);
        }
    }
}
