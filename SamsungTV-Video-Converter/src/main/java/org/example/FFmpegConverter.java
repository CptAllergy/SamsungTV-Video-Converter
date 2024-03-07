package org.example;


import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class FFmpegConverter {

    private static final String DEFAULT_INPUT_FOLDER = "input";
    private static final String DEFAULT_OUTPUT_FOLDER = "output";
    private static final String DEFAULT_FFMPEG_PATH = "C:/Program Files/ffmpeg/bin/ffmpeg.exe";
    private static final String DEFAULT_FFPROBE_PATH = "C:/Program Files/ffmpeg/bin/ffprobe.exe";


    private String inputFolder, outputFolder, ffmpegPath, ffprobePath;
    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;

    public FFmpegConverter(String inputFolder, String outputFolder, String ffmpegPath, String ffprobePath) {
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
        this.ffmpegPath = ffmpegPath;
        this.ffprobePath = ffprobePath;

        try {
            ffmpeg = new FFmpeg(ffmpegPath);
            ffprobe = new FFprobe(ffprobePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FFmpegConverter(String ffmpegPath, String ffprobePath) {
        this.ffmpegPath = ffmpegPath;
        this.ffprobePath = ffprobePath;

        try {
            ffmpeg = new FFmpeg(ffmpegPath);
            ffprobe = new FFprobe(ffprobePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FFmpegConverter() {
        this.inputFolder = DEFAULT_INPUT_FOLDER;
        this.outputFolder = DEFAULT_OUTPUT_FOLDER;
        this.ffmpegPath = DEFAULT_FFMPEG_PATH;
        this.ffprobePath = DEFAULT_FFPROBE_PATH;

        try {
            ffmpeg = new FFmpeg(ffmpegPath);
            ffprobe = new FFprobe(ffprobePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ConversionParameters getConversionDetails(String filename) throws IOException {
        // The number of subtitle streams to extract
        int subtitleStreamCounter = 0;

        // Store the subtitle tags
        Map<Integer, Map<String, String>> subtitleTags = new HashMap<>();

        // Gets information from the desired file
        FFmpegProbeResult probeResult = ffprobe.probe("./" + inputFolder + "/" + filename);

        // Iterate over all the available streams
        for (FFmpegStream stream : probeResult.streams) {
            System.out.println("Stream Type: " + stream.codec_type + " , Index:" + stream.index + " , Tags: " + stream.tags);
            FFmpegStream.CodecType codecType = stream.codec_type;
            Map<String, String> tags = stream.tags;

            if (codecType == FFmpegStream.CodecType.SUBTITLE) {
                subtitleTags.put(subtitleStreamCounter++, tags);
            }
        }

        return new ConversionParameters(subtitleStreamCounter, subtitleTags);
    }

    /**
     * Extracts srt files from the given video
     *
     * @param filename
     * @param info
     */
    public void extractSubtitles(String filename, ConversionParameters info) {
        // Create a folder to store the subtitles and result video
        String folderName = removeFileExtension(filename);
        new File("./" + outputFolder + "/" + folderName).mkdirs();

        for (int i = 0; i < info.getSubtitleStreamCounter(); i++) {
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput("./" + inputFolder + "/" + filename)
                    .addOutput("./" + outputFolder + "/" + folderName + "/" + info.getSubtitleTag(i) + ".srt")
                    .addExtraArgs("-map", "0:s:" + i)
                    .done();

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

            // Run a one-pass encode
            executor.createJob(builder).run();
        }
    }

    public String convertVideo(String filename, ConversionParameters info) {
        // Create a folder to store the subtitles and result video
        String folderName = removeFileExtension(filename);
        new File("./" + outputFolder + "/" + folderName).mkdirs();

        String targetFile = folderName + ".mp4";

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput("./" + inputFolder + "/" + filename)
                .overrideOutputFiles(true) // Override the output if it exists

                .addOutput("./" + outputFolder +  "/" + folderName + "/"  + targetFile)   // Filename for the destination
                .setFormat("mp4")        // Format is inferred from filename, or can be set

                .disableSubtitle()       // No subtiles

//
//                .addExtraArgs("-map", "0:a:" + info.getAudioTrack() )
//                .addExtraArgs("-map", "0:v:0" )

                .setAudioChannels(2)
                .setAudioCodec("aac")
                .setAudioSampleRate(48_000)  // at 48KHz
                .setAudioBitRate(32768)      // at 32 kbit/s



                .setVideoCodec("h264")
                .setVideoFrameRate(24, 1)     // at 24 frames per second
                .setVideoResolution(640, 480) // at 640x480 resolution
                .setVideoBitRate(160000)

                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)


                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        // Run a one-pass encode
        executor.createJob(builder).run();

        // Or run a two-pass encode (which is better quality at the cost of being slower)
        executor.createTwoPassJob(builder).run();

        return targetFile;
    }

    private String removeFileExtension(String filename) {
        String newFilename;
        try {
            newFilename = filename.substring(0, filename.lastIndexOf('.'));
        } catch (IndexOutOfBoundsException e) {
            // Filename has no extension
            return filename;
        }

        return newFilename;
    }
}
