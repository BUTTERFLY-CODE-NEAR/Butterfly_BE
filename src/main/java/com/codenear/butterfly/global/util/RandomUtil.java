package com.codenear.butterfly.global.util;

import java.security.SecureRandom;

public class RandomUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static int generateRandomNum(int length) {
        int lowerLimit = (int) Math.pow(10, length - 1);
        int upperLimit = (int) Math.pow(10, length);

        return SECURE_RANDOM.nextInt(upperLimit- lowerLimit) + lowerLimit;
    }
}
