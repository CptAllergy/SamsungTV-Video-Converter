package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        File folder = new File("./input/");
        File[] listOfFiles = folder.listFiles();

        // Default to 0
        int audioTrack = 0;

        // Parse the 'config.txt' file
        try {
            Scanner scanner = new Scanner(new File("./config.txt"));
            while (scanner.hasNext()) {
                String line = scanner.nextLine().trim();
                if (line.contains("audio_track")) {
                    // Get the audio track and subtract 1
                    audioTrack = Integer.parseInt(line.split("=")[1]) - 1;
                }
            }
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

        try {
            FFmpegConverter converter = new FFmpegConverter();
            for (File file : listOfFiles) {
                String filename = file.getName();
                System.out.println("\nConverting: " + filename);
                System.out.println("Please wait, it's not frozen if it takes a while...");

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


        System.out.println("\nProcess completed successfully!\nDe nada pai :)");
    }
}