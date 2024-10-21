package com.codenear.butterfly.s3.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.s3.domain.S3Directory;
import com.codenear.butterfly.s3.exception.S3Exception;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final S3Client amazonS3Client;

    private static final String BUKKIT_NAME = "codenear";

    public String uploadFile(MultipartFile file, S3Directory directory) {
        try {
            String fileKey = createFileKey(directory);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUKKIT_NAME)
                    .key(fileKey)
                    .build();

            Path path = getPath(file);
            byte[] bytes = get(path);

            RequestBody requestBody = RequestBody.fromBytes(bytes);
            amazonS3Client.putObject(putObjectRequest, requestBody);

            deleteTempFile(path); // 임시 파일 삭제
            return fileKey;
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

    private void deleteTempFile(Path bytes) throws IOException {
        Files.delete(bytes);
    }

    private String createFileKey(S3Directory directory) {
        UUID randomUUID = UUID.randomUUID();
        return directory.getValue() + randomUUID;
    }
    
    private byte[] get(Path tempFile) throws IOException {
        String string = tempFile.toString();
        return Files.readAllBytes(Paths.get(string));
    }

    private Path getPath(MultipartFile file) throws IOException {
        Path tempFile = Files.createTempFile("tempFile-", file.getOriginalFilename());
        file.transferTo(tempFile.toFile());
        return tempFile;
    }
}
