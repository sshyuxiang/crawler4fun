package com.seeker.consumer;

import com.seeker.util.PropertiesUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

public class SpiderConsumer {

    private SpiderConsumer() {

    }

    public static KafkaConsumer getInstance() {
        Properties props = PropertiesUtils.getProperties("properties/kafkaConsumer.properties");
        return new KafkaConsumer<>(props);
    }

}