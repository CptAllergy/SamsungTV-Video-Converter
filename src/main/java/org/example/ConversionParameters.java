package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConversionParameters {

    public String audioCodec = "aac";
    public int audioChannels = 2;
    public String videoCodec = "h264";

    private int audioTrack;

    private final int subtitleStreamCounter;

    Map<Integer, Map<String, String>> subtitleTags;

    public ConversionParameters(int subtitleStreamCounter,  Map<Integer, Map<String, String>> subtitleTags) {
        this.subtitleStreamCounter = subtitleStreamCounter;
        this.subtitleTags = subtitleTags;
    }


    public int getSubtitleStreamCounter() {
        return subtitleStreamCounter;
    }

    public int getAudioTrack() {
        return audioTrack;
    }

    public void setAudioTrack(int audioTrack) {
       this.audioTrack = audioTrack;
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
