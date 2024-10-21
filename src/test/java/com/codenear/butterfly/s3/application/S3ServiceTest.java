package com.codenear.butterfly.s3.application;

import com.codenear.butterfly.s3.domain.S3Directory;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
class S3ServiceTest {

    @Autowired
    private S3Service s3Service;

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(Lifecycle.PER_CLASS)
    class S3FileOperationsTest {
        private String fileKey;

        @Test
        @Order(1)
        void S3_파일_업로드_테스트() {
            // given
            String testFileContent = "테스트 파일 내용입니다.";
            MockMultipartFile testFile = new MockMultipartFile(
                    "testFile",
                    "test.txt",
                    "text/plain",
                    testFileContent.getBytes(StandardCharsets.UTF_8)
            );

            // when
            S3Directory testDirectory = S3Directory.TEST;

            // then
            Assertions.assertDoesNotThrow(() -> {
                fileKey = s3Service.uploadFile(testFile, testDirectory);
            });
        }

//        @Test
//        @Order(2)
//        void S3_파일_삭제_테스트() {
//            boolean isSuccess = s3Service.deleteFile(fileKey);
//
//            Assertions.assertTrue(isSuccess);
//        }
    }
}