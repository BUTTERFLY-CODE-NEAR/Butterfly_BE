package com.codenear.butterfly.s3.config;

import java.net.URI;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Configuration
public class S3Config {

    public static final int FULL_TIMEOUT_SECONDS = 60;
    public static final int RESTART_TIMEOUT_SECONDS = 20;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.endpoint}")
    private String endPoint;

    @Bean
    public S3Client amazonS3Client() {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        S3ClientBuilder s3ClientBuilder = S3Client.builder()
                .endpointOverride(URI.create(endPoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .overrideConfiguration(builder -> builder
                        .apiCallTimeout(Duration.ofSeconds(FULL_TIMEOUT_SECONDS))
                        .apiCallAttemptTimeout(Duration.ofSeconds(RESTART_TIMEOUT_SECONDS)));

        return s3ClientBuilder.build();
    }
}
