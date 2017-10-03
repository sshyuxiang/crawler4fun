package com.seeker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

    public static Properties getProperties(String resourceName) {
        try {
            return PropertiesLoaderUtils.loadAllProperties(resourceName);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Can't load properties : " + resourceName);
            return null;
        }
    }

    public static void main(String[] args) {
        Properties props = getProperties("properties/kafkaAdmin.properties");
        logger.info("加载成功" + props.toString());
    }

}
