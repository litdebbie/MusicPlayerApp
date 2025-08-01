import javax.swing.*;

public class MusicPlayerGUI extends JFrame {
    public MusicPlayerGUI() {
        // set title of JFrame to "Music Player"
        super("Music Player");

        // set the width and height
        setSize(400, 600);

        // end process when app is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // launch the app at the center of the screen
        setLocationRelativeTo(null);

        // prevent the app from being resized
        setResizable(false);

        // set layout to null allows us to control the (x,y) coordinates of
        // our components and also set the height and width
        setLayout(null);
    }
}
