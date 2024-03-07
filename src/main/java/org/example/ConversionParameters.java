package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConversionParameters {

    private static final String DEFAULT_AUDIO_CODEC = "aac";

    private static final int DEFAULT_AUDIO_CHANNELS = 2;

    private static final String DEFAULT_VIDEO_CODEC = "h264";

    private static final String DEFAULT_FILE_FORMAT = "mp4";



    private final String audioCodec;
    private final int audioChannels;
    private final String videoCodec;

    private final String fileFormat;
    private final int audioTrack;

    private final int subtitleStreamCounter;



    Map<Integer, Map<String, String>> subtitleTags;

    public ConversionParameters(int audioTrack, int subtitleStreamCounter,  Map<Integer, Map<String, String>> subtitleTags) {
        this.audioTrack = audioTrack;
        this.subtitleStreamCounter = subtitleStreamCounter;
        this.subtitleTags = subtitleTags;

        this.fileFormat = DEFAULT_FILE_FORMAT;

        this.audioChannels = DEFAULT_AUDIO_CHANNELS;

        this.audioCodec = DEFAULT_AUDIO_CODEC;
        this.videoCodec = DEFAULT_VIDEO_CODEC;
    }


    public int getSubtitleStreamCounter() {
        return subtitleStreamCounter;
    }

    public int getAudioTrack() {
        return audioTrack;
    }

    public String getAudioCodec() {
        return audioCodec;
    }

    public int getAudioChannels() {
        return audioChannels;
    }

    public String getVideoCodec() {
        return videoCodec;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public String getSubtitleTag(int index) {
        String subtitleName;

        Map<String, String> tags = subtitleTags.get(index);
        if(tags == null) {
            // If no tags are found, just return the index
            subtitleName = index + "";
        } else if(tags.containsKey("language")) {
            subtitleName = index + "_" + tags.get("language");
        } else if(tags.containsKey("title")) {
            subtitleName = index + "_" + tags.get("title");
        } else {
            // Contains tags with an unknown value
            List<String> sortedKeys = new ArrayList<String>(tags.keySet());
            // Sort the keys to assure the same tag is used if applicable
            Collections.sort(sortedKeys);
            // Return the first key as name
            subtitleName = index + "_" + tags.get(sortedKeys.getFirst());
        }
        subtitleName = subtitleName.replace(" ", "_");
        return subtitleName.replaceAll("[^A-Za-z0-9_-]", "");
    }

}
