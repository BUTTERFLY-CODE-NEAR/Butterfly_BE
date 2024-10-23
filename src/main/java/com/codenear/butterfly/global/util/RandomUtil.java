package com.codenear.butterfly.global.util;

import java.security.SecureRandom;

public class RandomUtil {

    public static int generateRandomNum(int length) {
        SecureRandom secureRandom = new SecureRandom();

        int lowerLimit = (int) Math.pow(10, length - 1);
        int upperLimit = (int) Math.pow(10, length);

        return secureRandom.nextInt(upperLimit- lowerLimit) + lowerLimit;
    }
}
