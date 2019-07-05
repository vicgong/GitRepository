package com.vicgong;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerTest {

    //config
    public Properties getConfig(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.1.111:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return props;
    }

    //同步发送 Send Message
    public void produceMessage(){
        Properties props = getConfig();
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        ProducerRecord<String, String> pr = null;
        //模拟循环1000次发送消息
        for(int i=0; i<1000; i++){
            System.out.println("i:"+i);
            pr = new ProducerRecord<String, String>("test1",Integer.toString(i),Integer.toString(i));
            producer.send(pr);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //同步发送 Send Message With return
    public RecordMetadata produceMessage2(){
        RecordMetadata rm = null;
        Properties props = getConfig();
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        ProducerRecord<String, String> pr =
                new ProducerRecord<String, String>("test1", "key", "value");
        try {
            rm = producer.send(pr).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return rm;
    }

    //异步发送 Send Message With return
    public void produceMessage3(){
        Properties props = getConfig();
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        ProducerRecord<String, String> pr =
                new ProducerRecord<String, String>("test1", "key", "value");
        producer.send(pr, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if(e != null){
                    e.printStackTrace();
                    System.out.println("The offset of the record we just sent is: "
                            + recordMetadata.offset());
                }
            }
        });
    }


    public static void main(String[] args) {
        ProducerTest pt = new ProducerTest();
        pt.produceMessage();
        //System.out.println(pt.produceMessage2().toString());
        //pt.produceMessage3();
    }
}
