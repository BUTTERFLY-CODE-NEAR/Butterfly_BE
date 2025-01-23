package com.codenear.butterfly.s3.application;

import static java.util.UUID.randomUUID;
import static software.amazon.awssdk.services.s3.model.ObjectCannedACL.PUBLIC_READ;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.s3.domain.S3Directory;
import com.codenear.butterfly.s3.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client amazonS3Client;

    @Value("${cloud.storage.bukkit.name}")
    private String bukkit_name;

    @Value("${cloud.storage.url}")
    private String storageUrl;

    public String uploadFile(MultipartFile file, S3Directory directory) {
        try {
            String fileName = generateFileName();
            String fileKey = generateFileKey(fileName, directory);
            String contentType = file.getContentType();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bukkit_name)
                    .key(fileKey)
                    .contentType(contentType)
                    .acl(PUBLIC_READ)
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
            amazonS3Client.putObject(putObjectRequest, requestBody);

            return fileName;
        } catch (Exception e) {
            throw new S3Exception(ErrorCode.SERVER_ERROR, null);
        }
    }

    public String generateFileUrl(String fileName, S3Directory directory) {
        return storageUrl + directory.getValue() + fileName;
    }

    public void deleteFile(String fileName, S3Directory directory) {
        String fileKey = generateFileKey(fileName, directory);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bukkit_name)
                .key(fileKey)
                .build();

        amazonS3Client.deleteObject(deleteObjectRequest);
    }

    private String generateFileName() {
        String uniqueId = randomUUID().toString();
        long currentTimeMillis = System.currentTimeMillis();
        return uniqueId + currentTimeMillis;
    }

    private String generateFileKey(String fileName, S3Directory directory) {
        return directory.getValue() + fileName;
    }
}
