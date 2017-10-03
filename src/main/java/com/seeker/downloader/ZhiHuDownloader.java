package com.seeker.downloader;

import com.seeker.consumer.SpiderConsumer;
import com.seeker.producer.SpiderProducer;
import com.seeker.util.HttpUtils;
import com.seeker.util.ProxyUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class ZhiHuDownloader {

    private static Logger logger = LoggerFactory.getLogger(ZhiHuDownloader.class);

    private KafkaConsumer<String, String> consumer;
    private KafkaProducer<String, String> producer;

    private String Cookie;

    public void execute() {
        init();
        while (true) {
            ConsumerRecords<String, String> records = getUrl();
            List<String> pages = download(records);
            sendPages(pages);
        }
    }

    private void init() {
        consumer = SpiderConsumer.getInstance();
        consumer.subscribe(Arrays.asList("url-queue"));
        producer = SpiderProducer.getInstance();
    }

    private ConsumerRecords<String, String> getUrl() {
        int timeout = 1000;
        consumer.subscribe(Arrays.asList("url-queue"));
        return consumer.poll(timeout);
    }

    private List<String> download(ConsumerRecords<String, String> records) {
        List<String> pages = new ArrayList<>();
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.createDefault();
            //List<NameValuePair> valuePairs = new LinkedList<>();
            for (ConsumerRecord<String, String> record : records) {
                String key = record.key();
                String value = record.value();
                HttpGet httpGet = new HttpGet(value);
                httpGet.setHeader("Cookie" ,Cookie);
                CloseableHttpResponse response = httpClient.execute(httpGet);
                String page = EntityUtils.toString(response.getEntity());
                if (response != null) {
                    logger.debug("抓取内容为 : ");
                    logger.debug("------------------------------------");
                    logger.debug(page);
                    logger.debug("------------------------------------");
                }
                pages.add(page);
            }
            return pages;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (httpClient != null)
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private void sendPages(List<String> pages) {
        for (String page : pages) {
            producer.send(new ProducerRecord<>("page-queue", String.valueOf(System.currentTimeMillis()), page));
        }
    }

    //模拟登录
    public String login() {
        String url = "https://www.zhihu.com";
//        Map<String ,String> params = getLoginParams(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<NameValuePair> valuePairs = new LinkedList<>();
//        for (String key : params.keySet()) {
//            if (!"Cookie".equals(key)) {
//                valuePairs.add(new BasicNameValuePair(key, params.get(key)));
//            }
//        }
        valuePairs.add(new BasicNameValuePair("email" ,"sshyuxiang@gmail.com"));
        valuePairs.add(new BasicNameValuePair("password" ,"CodeIs03My18Life"));
        String loginUrl = "https://www.zhihu.com/login/email";
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs ,"UTF-8");
            HttpPost httpPost = new HttpPost(loginUrl);
            //直接从浏览器获取的cookie
            String cookie = "_zap=2a9ed946-b5ad-491c-b5ee-cd9a4f685777; d_c0=\"AHCCIFcO_QuPTknTLl-LCsu-owYFtBSWzAI=|1498740862\"; q_c1=c63a693644e64c97bf81b8c8d43286aa|1505559875000|1498740789000; q_c1=c63a693644e64c97bf81b8c8d43286aa|1505562913000|1498740789000; aliyungf_tc=AQAAAB4hUg3tBgMA9scedbJrmJyjuWAR; s-q=jsoup; s-i=3; sid=kjt59j58; capsion_ticket=\"2|1:0|10:1506097343|14:capsion_ticket|44:ODNkOTEyNWNlMWQ3NDg0Yjk3YzA1NmIyZWI2OTJhOWM=|2fa577cb37e9f875c7741eb62dc0e2a7665924f11001004e06d84cd6c8840fa2\"; l_cap_id=\"M2E5MjU1NjVhNGViNDIyZmE2ODc1MDFkOGFmNWVlZDU=|1506098371|d1c4297e62d9e5bda717957059d9759dbe51c686\"; r_cap_id=\"MjZjZTUzOGE3MGVjNDAyYzk5ZDk5M2M1MTFkYjNiNDk=|1506098370|f2cd8ec35e02c0acad7a31a386b8cc3a6d98a48e\"; cap_id=\"MjY3Y2M0MWE1NjdiNGI1NWJiOTNkYmVlMzFlMGU2Yjk=|1506098370|47d049802af52df7e2877bf71ebca6d0a3b4d5fa\"; __utma=51854390.1970790071.1505921910.1506093815.1506097621.6; __utmb=51854390.0.10.1506097621; __utmc=51854390; __utmz=51854390.1506010838.2.2.utmcsr=zhihu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=51854390.000--|3=entry_date=20170629=1; _xsrf=6b2610fb-a5cc-4b89-a9ee-8c425f1762da";
            httpPost.setHeader("Cookie" ,cookie);
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String responseHtml = EntityUtils.toString(response.getEntity());
            logger.debug(responseHtml);
            if (response.getStatusLine().getStatusCode() == 200 && responseHtml != null && responseHtml.contains("\\u767b\\u5f55\\u6210\\u529f")) {
                logger.debug(responseHtml);
                logger.info("login success!");
                Cookie = HttpUtils.getCookie(response);
                logger.info("Cookie : " + Cookie);
                return "success";
            }
            return "fail";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "fail";
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return "fail";
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
    }

    private Map<String ,String> getLoginParams(String url) {
        logger.info("First request for login : start");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        ProxyUtils.setProxy();
        Map<String ,String> params = new HashMap<>();
        try {
            logger.info("executing request " + get.getURI());
            CloseableHttpResponse response = httpClient.execute(get);
            String cookie = HttpUtils.getCookie(response);
            if (cookie != null) {
                params.put("Cookie" ,cookie);
            }
            logger.debug("Cookie : " + cookie);
            String responseHtml;
            try {
                HttpEntity entity = response.getEntity();
                responseHtml = EntityUtils.toString(entity, "UTF-8");
                if (entity != null) {
                    logger.debug("--------------------------------------");
                    logger.debug("Response content: " + responseHtml);
                    logger.debug("--------------------------------------");
                }
            } finally {
                response.close();
            }
            Document document = Jsoup.parse(responseHtml);
            String xsrfValue = document.select("input[type=hidden]").get(0).val();
            params.put("_xsrf" ,xsrfValue);
            logger.debug("_xsrf : " + xsrfValue);
            logger.info("First request for login : end");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return params;
    }

    public static void main(String[] args) {
        ZhiHuDownloader spider = new ZhiHuDownloader();
        spider.execute();
    }

}
