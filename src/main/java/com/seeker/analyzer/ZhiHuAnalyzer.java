package com.seeker.analyzer;

import com.seeker.consumer.SpiderConsumer;
import com.seeker.producer.SpiderProducer;
import com.seeker.util.BloomFilterUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZhiHuAnalyzer {

    private static Logger logger = LoggerFactory.getLogger(ZhiHuAnalyzer.class);

    private KafkaConsumer<String, String> consumer;
    private KafkaProducer<String, String> producer;

    public void execute() {
        init();
        while (true) {
            ConsumerRecords<String, String> records = getPage();
            List<String> urls = analyze(records);
            sendUrls(urls);
        }
    }

    private void init() {
        consumer = SpiderConsumer.getInstance();
        consumer.subscribe(Arrays.asList("page-queue"));
        producer = SpiderProducer.getInstance();
    }

    private void sendUrls(List<String> urls) {
        for (String url : urls) {
            producer.send(new ProducerRecord<>("url-queue", String.valueOf(System.currentTimeMillis()), url));
        }
    }

    private List<String> analyze(ConsumerRecords<String, String> records) {
        List<String> urls = new ArrayList<>(records.count() * 20);
        for (ConsumerRecord<String, String> record : records) {
            String page = record.value();
            Document doc = Jsoup.parse(page);
            Elements elements = doc.select("#Profile-following .List-item");
            for (Element element : elements) {
                String baseUrl = element.select(".ContentItem-main .ContentItem-head .UserLink-link.").get(0).attr("href");
                if (!BloomFilterUtils.filter(baseUrl)) {
                    urls.add("https://www.zhihu.com" + baseUrl + "/following");
                }
            }
        }
        return urls;
    }

    private ConsumerRecords<String, String> getPage() {
        int timeout = 1000;
        ConsumerRecords<String, String> records = consumer.poll(timeout);
        return records;
    }

}
