package com.vicgong;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class ExampleConsumer {

    //config
    public static Properties getConfig() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.1.111:9092,192.168.1.112:9092");
        props.put("group.id", "ExampleGroup");
        props.put("enable.auto.commit", "true");
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

    //comsumer data
    public void consumerMessage(){
        Properties props = getConfig();
        String topic = "example";
        KafkaConsumer<Integer,String> consumer = new KafkaConsumer<Integer,String>(props);
        consumer.subscribe(Arrays.asList(topic));
        ConsumerRecords<Integer,String> records = consumer.poll(10000);
        for(ConsumerRecord record : records){
            System.out.println("topic:" + record.key()
                    +"; offset:" + record.offset()
                    +"; key:" + record.key()
                    +"; value:" + record.value()
            );
        }
    }

    public static void main(String[] args) {
        ExampleConsumer ec = new ExampleConsumer();
        ec.consumerMessage();
    }
}
