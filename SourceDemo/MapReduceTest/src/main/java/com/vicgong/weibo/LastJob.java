package com.vicgong.weibo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class LastJob extends Configured implements Tool {

    /**
     * 最后计算 第三次MapReduce
     *
     * @author vicgong
     */
    public static class LastMapper extends Mapper<LongWritable, Text, Text, Text> {
        //存放微博总数
        public static Map<String, Integer> cmap = null;
        //存放df
        public static Map<String, Integer> df = null;

        /**
         * 在map方法执行之前
         * 程序在map方法实例化之前调用setup方法 只会执行一次
         * 这个时候map方法并没有开始执行
         */
        protected void setup(Context context) throws IOException,
                InterruptedException {
            System.out.println("******************");
            if (cmap == null || cmap.size() == 0 || df == null || df.size() == 0) {

                URI[] ss = context.getCacheFiles();    //从内存中获取
                if (ss != null) {
                    for (int i = 0; i < ss.length; i++) {
                        URI uri = ss[i];
                        if (uri.getPath().endsWith("part-r-00003")) {//微博总数
                            Path path = new Path(uri.getPath());
//						FileSystem fs =FileSystem.get(context.getConfiguration());
//						fs.open(path);
                            BufferedReader br = new BufferedReader(new FileReader(path.getName()));
                            String line = br.readLine();
                            if (line.startsWith("count")) {
                                String[] ls = line.split("\t");
                                cmap = new HashMap<String, Integer>();
                                cmap.put(ls[0], Integer.parseInt(ls[1].trim()));
                            }
                            br.close();
                        } else if (uri.getPath().endsWith("part-r-00000")) {//词条的DF
                            df = new HashMap<String, Integer>();
                            Path path = new Path(uri.getPath());
                            BufferedReader br = new BufferedReader(new FileReader(path.getName()));
                            String line;
                            while ((line = br.readLine()) != null) {
                                String[] ls = line.split("\t");
                                df.put(ls[0], Integer.parseInt(ls[1].trim()));
                            }
                            br.close();
                        }
                    }
                }
            }
        }

        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            FileSplit fs = (FileSplit) context.getInputSplit();
            System.out.println("--------------------");
            if (!fs.getPath().getName().contains("part-r-00003")) {
                String[] v = value.toString().trim().split("\t");
                if (v.length >= 2) {
                    int tf = Integer.parseInt(v[1].trim());//tf值
                    String[] ss = v[0].split("_");
                    if (ss.length >= 2) {
                        String w = ss[0];
                        String id = ss[1];

                        double s = tf * Math.log(cmap.get("count") / df.get(w));
                        NumberFormat nf = NumberFormat.getInstance();
                        nf.setMaximumFractionDigits(5);
                        context.write(new Text(id), new Text(w + ":" + nf.format(s)));
                    }
                } else {
                    System.out.println(value.toString() + "-------------");
                }
            }
        }
    }

    public static class LastReduce extends Reducer<Text, Text, Text, Text> {
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            StringBuffer sb = new StringBuffer();
            for (Text i : values) {
                sb.append(i.toString() + "\t");
            }
            context.write(key, new Text(sb.toString()));
        }
    }

    /**
     * 最后计算 第三次MapReduce
     *
     * @author vicgong
     * 这个小案例一共经过三次mapreduce方法 最终得到的结果是根据每个用户所发的微博 得到每个词条所造微博的权重
     * 案例的作用是根据相应词条得到最相关微博
     */

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 0) {
            System.err.printf("Usage: %s [generic options] \n", getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            System.exit(-1);
        }
        Configuration config = getConf();
        FileSystem fs = FileSystem.get(config);
        Job job = Job.getInstance(config);
        job.setJarByClass(LastJob.class);
        job.setJobName("Weibo-LastJob");
        //config.set("FirstJobOutput", "file:///C:/Users/龚明达/IdeaProjects/MapReduceTest/output1");
        //config.set("SecondJobOutput", "file:///C:/Users/龚明达/IdeaProjects/MapReduceTest/output2");
        //config.set("LastJobOutput", "file:///C:/Users/龚明达/IdeaProjects/MapReduceTest/output3");
        //把微博总数加载到内存
        job.addCacheFile(new Path(config.get("FirstJobOutput") + File.separator + "part-r-00003").toUri());
        //把df(某个词条在多少文章中出现过)加载到内存
        job.addCacheFile(new Path(config.get("SecondJobOutput") + File.separator + "part-r-00000").toUri());
        //设置map任务的输出key类型、value类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setMapperClass(LastMapper.class);
        job.setReducerClass(LastReduce.class);
        //mr运行时的输入数据从hdfs的哪个目录中获取
        FileInputFormat.addInputPath(job, new Path(config.get("FirstJobOutput")));
        Path outpath = new Path(config.get("LastJobOutput"));
        if (fs.exists(outpath)) {
            fs.delete(outpath, true);
        }
        FileOutputFormat.setOutputPath(job, outpath);
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) {
        int exitCode = 2;
        try {
            exitCode = ToolRunner.run(new LastJob(), args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(exitCode);
        }
    }
}
