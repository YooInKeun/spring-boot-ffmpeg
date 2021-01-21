package com.yooinkeun.ffmpeg.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.yooinkeun.ffmpeg.config.S3Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class VideoService {

    private final S3Config s3Config;

    private final AmazonS3 amazonS3;

    @Transactional
    public String uploadVideo(MultipartFile file) throws IOException {
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
        String fileUrl = amazonS3.getUrl(s3Config.getBucketName(), key).toString();


//        File file = new File(multipartFile.getOriginalFilename());
//        multipartFile.transferTo(file);
//        logVideoPlayTime(Paths.get(fileUrl));
        return fileUrl;
    }

    private ObjectMetadata makeMetaData(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());
        return objectMetadata;
    }

//    private void logVideoPlayTime(Path fileUrl) {
//        try {
//            String fullUrl = fileUrl.getParent() + "/" + fileUrl.getFileName();
//
//            FFprobe ffprobe = new FFprobe("/DATA/ffmpeg/ffprobe");
//            FFmpegProbeResult probeResult = ffprobe.probe(fullUrl);
//            FFmpegFormat format = probeResult.getFormat();
//            log.info(Double.toString(format.duration)); // 초단위
//        } catch(IOException e) {
//            log.error("error", e);
//        }
//    }
}
