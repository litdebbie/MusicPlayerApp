import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class MusicPlayer extends PlaybackListener {
    // this will be used to update isPaused more synchronously
    private static final Object playSignal = new Object();

    // need reference so that we can update the gui in this class
    private MusicPlayerGUI musicPlayerGUI;

    // need a way to store song details
    private Song currentSong;
    public Song getCurrentSong() {
        return currentSong;
    }

    // use JLayer library to create an AdvancedPlayer obj which will handle playing the music
    private AdvancedPlayer advancedPlayer;

    // pause boolean flag used to indicate whether the player has been paused
    private boolean isPaused;

    // stores the last frame when the playback is finished (used for pausing and resuming)
    private int currentFrame;
    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }

    // track how many milliseconds has passed since playing the song (used for updating the slider)
    private int currentTimeInMilli;
    public void setCurrentTimeInMilli(int timeInMilli) {
        currentTimeInMilli = timeInMilli;
    }

    // constructor
    public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
    }

    public void loadSong(Song song) {
        currentSong = song;

        // play the current song if not null
        if(currentSong != null) playCurrentSong();
    }

    public void pauseSong() {
        if(advancedPlayer != null) {
            // update isPaused flag
            isPaused = true;

            // then we want to stop the player
            stopSong();
        }
    }

    public void stopSong() {
        if (advancedPlayer != null) {
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void playCurrentSong() {
        if(currentSong == null) return;

        try {
            // read mp3 audio data
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            // create a new advanced player
            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            // start music
            startMusicThread();
            
            // start playback slider thread
            startPlaybackSliderThread();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // create a thread that will handle playing the music
    private void startMusicThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(isPaused){
                        synchronized(playSignal) {
                            // update flag
                            isPaused = false;

                            // notify the other thread to continue (makes sure that isPaused is updated to false properly)
                            playSignal.notify();
                        }

                        // resume music from last frame
                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                    } else {
                        // play music from the beginning
                        advancedPlayer.play();
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // create a thread that will handle updating the slider
    private void startPlaybackSliderThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isPaused) {
                    try {
                        // wait till it gets notified by other thread to continue
                        // makes sure that isPausedd boolean flag updates to false before continuing
                        synchronized(playSignal) {
                            playSignal.wait();
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }

                while(!isPaused) {
                    try {
                        // increment current time in milli
                    currentTimeInMilli++;

                    // System.out.println(currentTimeInMilli * 1.83);

                    // calculate into frame value
                    int calculatedFrame = (int) ((double) currentTimeInMilli * 1.83 * currentSong.getFrameRatePerMilliseconds());

                    // update gui
                    musicPlayerGUI.setPlaybackSliderValue(calculatedFrame);

                    // mimic 1 millisecond using thread.sleep
                    Thread.sleep(1);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        // this method gets called in the beginning of the song
        System.out.println("Playback Started");
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        // this method gets called when the song finishes or if the player gets closed
        System.out.println("Playback Finished");
        // System.out.println("Actual Stop: " + evt.getFrame());

        if(isPaused){
            // calculate current frame to resume song properly
            currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
        }
    }

}
