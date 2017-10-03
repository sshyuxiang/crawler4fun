package com.seeker.producer;


import com.seeker.util.PropertiesUtils;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

public class SpiderProducer {

    private SpiderProducer() {

    }

    public static KafkaProducer getInstance() {
        Properties props = PropertiesUtils.getProperties("properties/kafkaProducer.properties");
        return new KafkaProducer<>(props);
    }

}
