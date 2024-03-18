package org.example.converter;


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


    private String inputFolder, outputFolder;
    private final String ffmpegPath;
    private final String ffprobePath;
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

    public ConversionParameters getConversionDetails(String filename, int audioTrack) throws IOException {
        // The number of subtitle streams to extract
        int subtitleStreamCounter = 0;
        int audioStreamCounter = 0;


        // Store the subtitle tags
        Map<Integer, Map<String, String>> subtitleTags = new HashMap<>();

        // Gets information from the desired file
        FFmpegProbeResult probeResult = ffprobe.probe("./" + inputFolder + "/" + filename);

        // Iterate over all the available streams
        for (FFmpegStream stream : probeResult.streams) {
            //System.out.println("Stream Type: " + stream.codec_type + " , Index:" + stream.index + " , Tags: " + stream.tags);
            FFmpegStream.CodecType codecType = stream.codec_type;
            Map<String, String> tags = stream.tags;

            if (codecType == FFmpegStream.CodecType.SUBTITLE) {
                subtitleTags.put(subtitleStreamCounter++, tags);
            } else if (codecType == FFmpegStream.CodecType.AUDIO) {
                audioStreamCounter++;
            }
        }

        // Check if audio stream makes sense, otherwise set it to 0 (default)
        if(audioTrack > audioStreamCounter - 1) {
            audioTrack = 0;
            System.out.println("Invalid audio_track chosen, defaulting to track number 1...Make sure that audio_track is correctly set in 'config.txt'.");
        }

        return new ConversionParameters(audioTrack, subtitleStreamCounter, subtitleTags);
    }

    /**
     * Extracts srt files from the given video
     *
     * @param filename The name of the file to extract the subtitles from (should be in input folder).
     * @param info     The ConversionParameters to obtain information from the file.
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

        String targetFile = folderName + "." + info.getFileFormat();

        FFmpegBuilder builder = new FFmpegBuilder().
                setInput("./" + inputFolder + "/" + filename)
                .overrideOutputFiles(true) // Override the output if it exists
                // Filename for the destination
                .addOutput("./" + outputFolder + "/" + folderName + "/" + targetFile)
                .setFormat(info.getFileFormat())
                // No subtitles
                .disableSubtitle()
                // Video and audio stream choices
                .addExtraArgs("-map", "0:a:" + info.getAudioTrack())
                .addExtraArgs("-map", "0:v:0")
                // Audio settings
                .setAudioChannels(info.getAudioChannels())
                .setAudioCodec(info.getAudioCodec())
                // Video settings
                .setVideoCodec(info.getVideoCodec())
                // Toggle experimental mode
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)

                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        // Run a one-pass encode
        executor.createJob(builder).run();

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
