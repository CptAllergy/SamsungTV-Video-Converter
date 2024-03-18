package org.example.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

public class ConfigParser {

    private int audioTrack;
    private boolean isVideoConversionEnabled;
    private boolean isSubtitleExtractionEnabled;

    /**
     * Parses the config file given by path
     * @param path
     * @throws FileNotFoundException
     */
    public ConfigParser(String path) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        while (scanner.hasNext()) {
            String line = scanner.nextLine().trim();
            if (line.contains("audio_track")) {
                // Get the audio track and subtract 1
                audioTrack = Integer.parseInt(line.split("=")[1]) - 1;
            } else if (line.contains("extract_subtitles")) {
                isSubtitleExtractionEnabled = Boolean.parseBoolean(line.split("=")[1].toLowerCase());
            } else if (line.contains("convert_video"))  {
                isVideoConversionEnabled = Boolean.parseBoolean(line.split("=")[1].toLowerCase());
            }
        }

    }

    public int getAudioTrack() {
        return audioTrack;
    }

    public boolean isVideoConversionEnabled() {
        return isVideoConversionEnabled;
    }

    public boolean isSubtitleExtractionEnabled() {
        return isSubtitleExtractionEnabled;
    }


}
