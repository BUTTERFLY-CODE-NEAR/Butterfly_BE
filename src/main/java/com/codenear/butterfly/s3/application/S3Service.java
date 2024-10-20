package com.codenear.butterfly.s3.application;

import com.codenear.butterfly.s3.domain.S3Directory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final S3Client amazonS3Client;

    private static final String BUKKIT_NAME = "codenear";

    public void deleteFile(S3Directory directory, String fileName) {
        String fileKey = directory.getValue() + fileName;

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(BUKKIT_NAME)
                .key(fileKey)
                .build();

        amazonS3Client.deleteObject(deleteObjectRequest);
    }
}
