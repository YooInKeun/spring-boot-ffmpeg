package com.yooinkeun.ffmpeg.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.yooinkeun.ffmpeg.config.S3Config;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class VideoService {

    private final S3Config s3Config;

    private final AmazonS3 amazonS3;

    public String upload(MultipartFile file) throws IOException {
        String uuid = UUID.randomUUID().toString();
        Path basePath = s3Config.getBasePath();
        String key = basePath.resolve(uuid).resolve(file.getOriginalFilename()).toString();

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                s3Config.getBucketName(),
                key,
                file.getInputStream(),
                makeMetaData(file))
                .withCannedAcl(CannedAccessControlList.PublicRead);

        amazonS3.putObject(putObjectRequest);
        return amazonS3.getUrl(s3Config.getBucketName(), key).toString();
    }

    private ObjectMetadata makeMetaData(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());
        return objectMetadata;
    }
}
