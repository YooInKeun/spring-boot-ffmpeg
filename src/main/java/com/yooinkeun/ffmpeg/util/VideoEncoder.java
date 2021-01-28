package com.yooinkeun.ffmpeg.util;

import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import java.awt.*;
import java.io.File;
import java.io.IOException;

@Slf4j
public class VideoEncoder {

//    @Value("${ffmpeg.path}")
//    private String ffmpegPath;

//    @Value("${ffprobe.path}")
//    private String ffprobePath;

    private final static String FFMPEG_PATH = "/usr/local/bin/ffmpeg";
    private final static String FFPROBE_PATH = "/usr/local/bin/ffprobe";

    private FFmpeg ffmpeg = new FFmpeg(FFMPEG_PATH);

    private FFprobe ffprobe = new FFprobe(FFPROBE_PATH);

    private FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

    public VideoEncoder() throws IOException { }

    /**
     * 원본 비디오 파일 용량 압축
     * @param videoFile
     */
    public void compressFile(File videoFile, String compressedFilePath) throws IOException {
        FFmpegProbeResult ffmpegProbeResult = ffprobe.probe(videoFile.getAbsolutePath());

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(videoFile.getAbsolutePath())
                .setInput(ffmpegProbeResult)
                .overrideOutputFiles(true)
                .addOutput(compressedFilePath)
                .setFormat("mp4")
                .setVideoCodec("h264")
                .disableSubtitle()
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                .done();

        executor.createJob(builder).run();
    }
    
    /**
     * 비디오 썸네일 생성, playTime 단위는 second(초)
     * @param videoFile
     * @param thumbnailFilePath
     * @param playTime
     */
    public void makeThumbnail(File videoFile, String thumbnailFilePath, Dimension dimension, long playTime) {
        int width = dimension.width % 2 == 0 ? dimension.width : dimension.width + 1;
        int height = dimension.height % 2 == 0 ? dimension.height : dimension.height + 1;

        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .setInput(videoFile.getAbsolutePath())
                .addOutput(thumbnailFilePath)
                .addExtraArgs("-t", String.valueOf(playTime))
                .setFormat("mp4")
                .setVideoCodec("libx264")
                .setVideoResolution(width, height)
                .done();

        executor.createJob(builder).run();
    }
    
    /**
     * 프레임의 파일 크기(너비, 높이) 구하기
     */
    public Dimension getDimension(File videoFile, int frameIndex) throws IOException {
        FFmpegProbeResult ffmpegProbeResult = ffprobe.probe(videoFile.getAbsolutePath());
        FFmpegStream ffmpegStream = ffmpegProbeResult.getStreams().get(frameIndex);
        return new Dimension(ffmpegStream.width, ffmpegStream.height);
    }
    
    /**
     * 동영상 재생 시간 구하기
     * @param videoFile
     * @return
     * @throws IOException
     */
    public int getPlayTime(File videoFile) throws IOException {
        FFmpegProbeResult ffmpegProbeResult = ffprobe.probe(videoFile.getAbsolutePath());
        FFmpegFormat format = ffmpegProbeResult.getFormat();
        return (int) format.duration;
    }
}
