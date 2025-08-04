import java.io.File;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import com.mpatric.mp3agic.Mp3File;

// class used to describe a song
public class Song {
    private String songTitle;
    private String songArtist;
    private String songLength;
    private String filePath;

    private Mp3File mp3File;
    private double frameRatePerMilliseconds;

    public Song(String filePath) {
        this.filePath = filePath;
        try {
            mp3File = new Mp3File(filePath);
            
            // calculate frame rate per millisecond
            // frameRatePerMilliseconds = totalFrames / songLength
            frameRatePerMilliseconds = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();
            
            // get songLength in correct format
            songLength = convertToSongLengthFormat();

            // use the jaudiotagger library to create an audiofile obj to read mp3 file's information
            AudioFile audioFile = AudioFileIO.read(new File(filePath));

            // read through the meta data of the audio file
            Tag tag = audioFile.getTag();

            if(tag != null) {
                songTitle = tag.getFirst(FieldKey.TITLE);
                songArtist = tag.getFirst(FieldKey.ARTIST);

                // check if song title or artist field is empty
                if(tag.getFirst(FieldKey.TITLE) == "") songTitle = "Unknown";
                if(tag.getFirst(FieldKey.ARTIST) == "") songArtist = "Unknown";
            } else {
                // could not read through mp3 file's meta data
                songTitle = "N/A";
                songArtist = "N/A";
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String convertToSongLengthFormat() {
        long minutes = mp3File.getLengthInSeconds() / 60;
        long seconds = mp3File.getLengthInSeconds() % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        return formattedTime;
    }

    // getters
    public String getSongTitle() {
        return songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }
    
    public String getSongLength() {
        return songLength;
    }
    
    public String getFilePath() {
        return filePath;
    }

    public Mp3File getMp3File() {
        return mp3File;
    }

    public double getFrameRatePerMilliseconds() {
        return frameRatePerMilliseconds;
    }
}
