import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MusicPlayerGUI().setVisible(true);

                // Song song = new Song("src/assets/Stickerbush Symphony - David Wise.mp3");
                // System.out.println(song.getSongTitle());
                // System.out.println(song.getSongArtist());
            }
        });
    }
}
