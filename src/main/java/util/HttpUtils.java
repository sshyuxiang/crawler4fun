package util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class HttpUtils {

    public static Document getDocument(String url) {
        Document document = null;
        url = "https://www.zhihu.com/people/huang-yu-xiang-6-92";
        ProxyUtils.setProxy();
        try {
            document = Jsoup.connect(url).timeout(1000).userAgent("User-Agent:Opera/9.80 (Windows NT 6.1; U; en) Presto/2.8.131 Version/11.11").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

    public static void main(String[] args) {
        Document document = HttpUtils.getDocument("");
        System.out.println(document);
        System.out.println(System.getProperties().getProperty("http.proxyHost"));
        System.out.println(System.getProperties().getProperty("http.proxyPort"));
    }

}
