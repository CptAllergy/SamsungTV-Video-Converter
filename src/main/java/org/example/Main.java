package org.example;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        //TODO suppress logging

        File folder = new File("./input/");
        File[] listOfFiles = folder.listFiles();

        // The track NUMBER counting from first to last
        int audioTrack = 1 - 1;


        if(listOfFiles == null) {

            return;
        }

        try {
            FFmpegConverter converter = new FFmpegConverter();
            for (File file : listOfFiles) {
                String filename = file.getName();
                System.out.println("Converting: " + filename);
                // Get info from original file
                ConversionParameters info = converter.getConversionDetails(filename, audioTrack);
                // Convert the file


                //converter.extractSubtitles(filename, info);

                String targetFile = converter.convertVideo(filename, info);
                //System.out.println("Completed: " + targetFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\nERROR: Process failed.");
            return;
        }


        System.out.println("Funcionou.");

    }
}