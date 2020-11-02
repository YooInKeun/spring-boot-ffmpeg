package com.yooinkeun.ffmpeg.controller;

import com.yooinkeun.ffmpeg.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/api/video/upload")
    public String upload(MultipartFile file) throws IOException {
        return videoService.upload(file);
    }
}
