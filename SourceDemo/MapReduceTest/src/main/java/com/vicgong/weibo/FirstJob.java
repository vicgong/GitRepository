package com.vicgong.weibo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;

public class FirstJob extends Configured implements Tool {

    /**
     * 第一个MR，计算TF（总词频）和计算N(微博总数)
     *
     * @author vicgong
     */
    public static class FirstMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            //通过\t进行String类型的分解
            String[] v = value.toString().trim().split("\t");
            if (v.length >= 2) {
                //前面为id
                String id = v[0].trim();
                //后面为微博的具体内容 磁条
                String content = v[1].trim();
                StringReader sr = new StringReader(content);
                //IKSegmenter为分词器
                IKSegmenter ikSegmenter = new IKSegmenter(sr, true);
                Lexeme word = null;
                while ((word = ikSegmenter.next()) != null) {
                    String w = word.getLexemeText();
                    context.write(new Text(w + "_" + id), new IntWritable(1));
                }
                context.write(new Text("count"), new IntWritable(1));
            } else {
                System.out.println(value.toString() + "-------------");
            }
        }
    }

    /**
     * c1_001,2
     * c2_001,1
     * count,10000
     *
     * @author vicgong
     */
    public static class FirstReduce extends Reducer<Text, IntWritable, Text, IntWritable> {
        protected void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable i : values) {
                sum = sum + i.get();
            }
            if (key.equals(new Text("count"))) {
                System.out.println(key.toString() + "___________" + sum);
            }
            context.write(key, new IntWritable(sum));
        }
    }

    /**
     * 第一个MR自定义分区
     *
     * @author vicgong
     */
    public static class FirstPartition extends HashPartitioner<Text, IntWritable> {
        public int getPartition(Text key, IntWritable value, int reduceCount) {
            //这里reduce至少是2个 一个是微博总数 一个是pi（实质上这里是有4个 0 1 2 3）
            if (key.equals(new Text("count")))
                return 3; //这里的3 是根据reduce设计来决定的
            else
                return super.getPartition(key, value, reduceCount - 1);
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.printf("Usage: %s [generic options] <inpath> \n", getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            System.exit(-1);
        }
        Configuration config = getConf();
        config.set("FirstJobOutput","file:///C:/Users/龚明达/IdeaProjects/MapReduceTest/output1");
        FileSystem fs = FileSystem.get(config);
        Job job = Job.getInstance(config);
        job.setJarByClass(FirstJob.class);
        job.setJobName("Weibo-FirstJob");
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        //设置4个reduce 最后一个reduce记录微博总数
        job.setNumReduceTasks(4);
        job.setPartitionerClass(FirstPartition.class);
        job.setMapperClass(FirstMapper.class);
        job.setCombinerClass(FirstReduce.class);
        job.setReducerClass(FirstReduce.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        Path path = new Path(config.get("FirstJobOutput"));
        if (fs.exists(path)) {
            fs.delete(path, true);
        }
        FileOutputFormat.setOutputPath(job, path);
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) {
        int exitCode = 2;
        try {
            exitCode = ToolRunner.run(new FirstJob(), args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(exitCode);
        }
    }
}
