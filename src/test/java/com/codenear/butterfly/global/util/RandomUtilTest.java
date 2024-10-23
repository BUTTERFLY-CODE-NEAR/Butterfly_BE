package com.codenear.butterfly.global.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RandomUtilTest {

    @Test
//  @RepeatedTest(10) // 랜덤 테스트 반복도 가능
    void 랜덤_숫자_생성_테스트() {
        // given
        int randomNumLength = 6;

        // when
        int randomNum = RandomUtil.generateRandomNum(randomNumLength);

        // then
        Assertions.assertEquals(String.valueOf(randomNum).length() , randomNumLength);
    }
}