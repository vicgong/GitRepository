package com.vicgong;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ExampleProducer {

    public Properties getConfig(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.1.111:9092,192.168.1.112:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer","org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }

    //同步发送
    public void syncSendMessage(){
        Properties props = getConfig();
        String[] messages = {"Hello world","Hello java","Hello food","Hello tomorrow"};
        String topic = "example";
        ProducerRecord<Integer, String> pr = null;
        RecordMetadata metadata = null;
        KafkaProducer<Integer,String> producer = new KafkaProducer<Integer,String>(props);
        for(int i=0;i<messages.length;i++){
            pr = new ProducerRecord<Integer, String>(topic,i,messages[i]);
            System.out.println("Ready Send Message : " + i + "->" + messages[i]);
            try {
                metadata = producer.send(pr).get();
                System.out.println("The offset of the record we just sent is: " + metadata.offset());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    //异步发送
    public void asyncSendMessage(){
        Properties props = getConfig();
        String[] messages = {"Hello World","Hello Java","Hello Food","Hello Tomorrow"};
        String topic = "example";
        ProducerRecord<Integer, String> pr = null;
        RecordMetadata rmdata = null;
        KafkaProducer<Integer,String> producer = new KafkaProducer<Integer,String>(props);
        for(int i=0;i<messages.length;i++){
            pr = new ProducerRecord<Integer, String>(topic,i+4,messages[i]);
            System.out.println("Ready Send Message : " + (i+4) + "->" + messages[i]);
            producer.send(pr, new Callback() {
                        @Override
                        public void onCompletion(RecordMetadata metadata, Exception exception) {
                            if(exception != null){
                                exception.printStackTrace();
                                System.out.println("The offset of the record we just sent is: "
                                        + metadata.offset());
                            }
                        }
                    });
        }
    }

    public static void main(String[] args) {
        ExampleProducer ep = new ExampleProducer();
        ep.syncSendMessage();
        ep.asyncSendMessage();
    }
}
