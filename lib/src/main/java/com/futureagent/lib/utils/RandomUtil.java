package com.futureagent.lib.utils;

import java.util.Random;

/**
 * @author skywalker on 2018/5/5.
 * Email: skywalker@thecover.co
 * Description:
 */
public class RandomUtil {
    public static int getRandomValue(int start, int end) {
        Random random = new Random();
        return random.nextInt(end) % (end - start + 1) + start;
    }
}
