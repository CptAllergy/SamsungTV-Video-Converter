package org.example;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        //TODO suppress logging

        File folder = new File("./input/");
        File[] listOfFiles = folder.listFiles();

        // The track NUMBER starts at 1
        int audioTrack = 1 - 1;

        if (listOfFiles == null || listOfFiles.length == 0) {
            System.out.println("Please make sure that an input folder exists and that it contains video files to process.");
            return;
        }

        // Create output folder
        new File("./" + "output").mkdirs();

        try {
            FFmpegConverter converter = new FFmpegConverter();
            for (File file : listOfFiles) {
                String filename = file.getName();
                System.out.println("Converting: " + filename);

                // Get info from original file
                ConversionParameters info = converter.getConversionDetails(filename, audioTrack);

                // Extract the subtitles
                converter.extractSubtitles(filename, info);

                // Convert the file
                String targetFile = converter.convertVideo(filename, info);
                System.out.println("Completed: " + targetFile);
            }
        } catch (Exception e) {
            System.out.println("\nERROR: Process failed with exception: " + e
                    + "\nOh não :(! Se estás a ver isto avisa o Gonçalo e tenta descrever o que aconteceu para ele tentar arranjar.");
            return;
        }


        System.out.println("Process completed successfully!\nDe nada pai :)");
    }
}