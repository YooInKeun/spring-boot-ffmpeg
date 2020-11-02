package com.yooinkeun.ffmpeg.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class S3Config {

    @Getter
    @Value("${s3.bucket.name}")
    private String bucketName;

    @Getter
    @Value("${s3.bucket.base-path}")
    private Path basePath;

    @Value("${s3.bucket.access-key}")
    private String accessKey;

    @Value("${s3.bucket.password}")
    private String password;

    @Bean
    public AmazonS3 amazonS3Client() {
        final AWSCredentials credentials = new BasicAWSCredentials(accessKey, password);

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.fromName("ap-northeast-2"))
                .build();
    }
}
