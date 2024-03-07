//package org.example;
//
//
//import ws.schild.jave.Encoder;
//import ws.schild.jave.MultimediaObject;
//import ws.schild.jave.encode.AudioAttributes;
//import ws.schild.jave.encode.VideoAttributes;
//import ws.schild.jave.encode.EncodingAttributes;
//import ws.schild.jave.encode.enums.PresetEnum;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//
//public class ConverterJave {
//
//    public ConverterJave() {
//
//    }
//
//    public void convert() throws IOException {
//
//        try {
//            File source = new File("./input/Fullmetal Alchemist BrotherHood 01.mkv");
//            File target = new File("./output/result.mp4");
//
//            if (target.exists()) {
//                target.delete();
//            }
//
//            //Audio Attributes
//            AudioAttributes audio = new AudioAttributes();
//            audio.setCodec("aac");
//
////            audio.setBitRate(64000);
////            audio.setChannels(2);
////            audio.setSamplingRate(44100);
//
//            //Video Attributes
//            VideoAttributes video = new VideoAttributes();
//            video.setCodec("h264");
//            //video.addFilter();
//
////            video.setX264Profile(X264_PROFILE.BASELINE);
////            // Here 160 kbps video is 160000
////            video.setBitRate(160000);
////            // More the frames more quality and size, but keep it low based on devices like mobile
////            video.setFrameRate(15);
////            video.setSize(new VideoSize(400, 300));
//
//            //Encoding attributes
//            EncodingAttributes attrs = new EncodingAttributes();
//
//            attrs.setOutputFormat("mp4");
//            attrs.setAudioAttributes(audio);
//            attrs.setVideoAttributes(video);
//            HashMap<String, String > extra = new HashMap<>();
//            extra.put("-map", "0:a:1");
//            attrs.setExtraContext(extra);
//
//
//            //Encode
//            Encoder encoder = new Encoder();
//            encoder.encode(new MultimediaObject(source), target, attrs);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//
//        }
//    }
//}
