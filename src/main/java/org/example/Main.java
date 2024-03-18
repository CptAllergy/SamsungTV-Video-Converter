package org.example;

import org.example.converter.ConfigParser;
import org.example.converter.ConversionParameters;
import org.example.converter.FFmpegConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        File folder = new File("./input/");
        File[] listOfFiles = folder.listFiles();

        ConfigParser config;
        try {
            config = new ConfigParser("./config.txt");
        } catch (FileNotFoundException e) {
            System.out.println("\nERROR: Missing 'config.txt' file, make sure it exists.");
            return;
        }


        // Check input folder
        if (listOfFiles == null || listOfFiles.length == 0) {
            System.out.println("Please make sure that an input folder exists and that it contains video files to process.");
            return;
        }

        // Create output folder
        new File("./" + "output").mkdirs();


        System.out.println("Starting Samsung Converter with the following settings:\n"
                + "audio_track=" + config.getAudioTrack() + "\n"
                + "extract_subtitles=" + config.isSubtitleExtractionEnabled() + "\n"
                + "convert_video=" + config.isVideoConversionEnabled() + "\n");

        try {
            FFmpegConverter converter = new FFmpegConverter();
            for (File file : listOfFiles) {
                String filename = file.getName();
                System.out.println("\nProcessing: " + filename);

                // Get info from original file
                ConversionParameters info = converter.getConversionDetails(filename, config.getAudioTrack());

                // Extract the subtitles
                if (config.isSubtitleExtractionEnabled()) {
                    converter.extractSubtitles(filename, info);
                    System.out.println("Subtitles extracted");
                }

                // Convert the file
                if (config.isVideoConversionEnabled()) {
                    System.out.println("Please wait, it's not frozen if it takes a while...");
                    String targetFile = converter.convertVideo(filename, info);
                    System.out.println("Converted: " + targetFile);
                }

            }
        } catch (Exception e) {
            System.out.println("\nERROR: Process failed with exception: " + e
                    + "\nOh não :(! Se estás a ver isto avisa o Gonçalo e tenta descrever o que aconteceu para ele tentar arranjar.");
            return;
        }


        System.out.println("\nProcess completed successfully!\nDe nada pai :)");
    }
}