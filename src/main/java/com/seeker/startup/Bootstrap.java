package com.seeker.startup;

import com.seeker.analyzer.ZhiHuAnalyzer;
import com.seeker.downloader.ZhiHuDownloader;

public class Bootstrap {

    public static void main(String[] args) {
        ZhiHuDownloader downloader = new ZhiHuDownloader();
        ZhiHuAnalyzer analyzer= new ZhiHuAnalyzer();
        downloader.execute();
        analyzer.execute();
    }

}
