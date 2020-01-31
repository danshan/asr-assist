package com.github.danshan.asrassist.xfyun.file;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.flac.FlacFileReader;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;

public class AudioLength {
    public AudioLength() {
    }

    public static double getLength(String file) {
        try {
            int i = file.lastIndexOf(".");
            String ext = file.substring(i + 1);
            byte var4 = -1;
            switch(ext.hashCode()) {
            case 108272:
                if (ext.equals("mp3")) {
                    var4 = 0;
                }
                break;
            case 117484:
                if (ext.equals("wav")) {
                    var4 = 2;
                }
                break;
            case 3145576:
                if (ext.equals("flac")) {
                    var4 = 1;
                }
            }

            switch(var4) {
            case 0:
                return mp3File(file);
            case 1:
                return flacFile(file);
            case 2:
                return wavFile(file);
            default:
                return -1.0D;
            }
        } catch (Exception var5) {
            return -1.0D;
        }
    }

    public static double mp3File(String file) {
        try {
            File fileMP3 = new File(file);
            MP3File f = (MP3File)AudioFileIO.read(fileMP3);
            MP3AudioHeader audioHeader = (MP3AudioHeader)f.getAudioHeader();
            return (double)audioHeader.getTrackLength();
        } catch (Exception var4) {
            return -1.0D;
        }
    }

    public static double flacFile(String file) {
        try {
            File fileFLAC = new File(file);
            FlacFileReader f = new FlacFileReader();
            AudioFile af = f.read(fileFLAC);
            AudioHeader audioHeader = af.getAudioHeader();
            return (double)audioHeader.getTrackLength();
        } catch (Exception var5) {
            return -1.0D;
        }
    }

    public static double wavFile(String file) {
        File fileWav = null;
        Clip clip = null;
        AudioInputStream ais = null;

        double var5;
        try {
            fileWav = new File(file);
            clip = AudioSystem.getClip();
            ais = AudioSystem.getAudioInputStream(fileWav);
            clip.open(ais);
            double var4 = Math.rint((double)clip.getMicrosecondLength() / 1000000.0D);
            return var4;
        } catch (Exception var16) {
            var5 = -1.0D;
        } finally {
            if (clip != null) {
                clip.close();
            }

            if (ais != null) {
                try {
                    ais.close();
                } catch (IOException var15) {
                    var15.printStackTrace();
                }
            }

        }

        return var5;
    }
}
