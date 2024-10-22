package com.codenear.butterfly.s3.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.s3.domain.S3Directory;
import com.codenear.butterfly.s3.exception.S3Exception;
import java.net.URL;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final S3Client amazonS3Client;

    private static final String BUKKIT_NAME = "codenear";

    public String uploadFile(MultipartFile file, S3Directory directory) {
        try {
            String fileKey = createFileKey(directory, file.getOriginalFilename());
            String contentType = file.getContentType();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUKKIT_NAME)
                    .key(fileKey)
                    .contentType(contentType)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
            amazonS3Client.putObject(putObjectRequest, requestBody);

            return generateFileUrl(fileKey);
        } catch (Exception e) {
            throw new S3Exception(ErrorCode.SERVER_ERROR, null);
        }
    }

    public void deleteFile(String fileKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(BUKKIT_NAME)
                .key(fileKey)
                .build();

        amazonS3Client.deleteObject(deleteObjectRequest);
    }

    private String generateFileUrl(String fileKey) {
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(BUKKIT_NAME)
                .key(fileKey)
                .build();

        URL url = amazonS3Client.utilities().getUrl(getUrlRequest);
        return url.toString();
    }

    private String createFileKey(S3Directory directory, String originalFilename) {
        String uniqueId = UUID.randomUUID().toString();
        return directory.getValue() + uniqueId + originalFilename;
    }
}
