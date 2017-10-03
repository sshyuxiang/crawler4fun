package com.seeker.util;

import com.seeker.redisbloom.bloom.BloomFilter;
import redis.clients.jedis.Jedis;

public class BloomFilterUtils {

    private static BloomFilter bloomFilter;

    static {
        Jedis jedis = RedisUtils.getJedis();
        String bitSetName = "";
        bloomFilter = new BloomFilter(0.000001, (int)(173070*1.5));
        bloomFilter.bind(jedis, bitSetName);
    }

    public static synchronized boolean filter(String url) {
        boolean isContain = false;
        if (bloomFilter.contains(url)) {
            isContain = true;
            bloomFilter.add(url);
        }
        return isContain;
    }

}
