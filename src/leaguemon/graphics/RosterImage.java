package leaguemon.graphics;

import leaguemon.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Inconvenius
 */
public class RosterImage extends Component {

    private final List<Player> players;
    private final BufferedImage roster;

    public RosterImage(List<Player> players) {
        this.players = players;
        this.roster = new BufferedImage(200, 33*players.size(), BufferedImage.TYPE_INT_ARGB);
    }

    public void writeImage(String file) throws IOException {
        paint(roster.getGraphics());
        ImageIO.write(roster, "PNG", new File(file));
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int y = 0;
        for (Player player : players) {
            g2d.drawImage(new PlayerImage(player), 10, y, null);
            y += 33;
        }
    }
}
