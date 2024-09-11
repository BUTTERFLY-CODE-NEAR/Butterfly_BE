package com.codenear.butterfly.member.util;

import java.util.List;
import java.util.Random;

public enum NicknameList {
    나비,
    하늘,
    별빛,
    꽃잎,
    바람,
    구름,
    달빛,
    강물,
    햇살;

    private static final List<NicknameList> VALUES = List.of(values());
    private static final Random RANDOM = new Random();

    public static String getRandomNickname() {
        return VALUES.get(RANDOM.nextInt(VALUES.size())).name();
    }
}