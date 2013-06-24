package leaguemon.graphics;

import leaguemon.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The <code>RosterImage</code> class represents an image with the players' names, leagues, races and
 * positions in the team.
 *
 * @author Inconvenius
 */
public class RosterImage extends Component {

    private final List<Player> players;
    private final BufferedImage roster;

    /**
     * Class constructor specifying a list of players whose names, leagues, races
     * and team positions will be drawn onto the roster.
     *
     * @param players the list of players that will be on this roster image
     */
    public RosterImage(List<Player> players) {
        this.players = players;
        this.roster = new BufferedImage(PlayerImage.WIDTH, PlayerImage.HEIGHT*players.size(), BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Writes this roster image to the specified location.
     *
     * @param file path to write the image to
     * @throws IOException if there is a problem writing the file
     */
    public void writeImage(String file) throws IOException {
        paint(roster.getGraphics());
        ImageIO.write(roster, "PNG", new File(file));
    }

    /**
     * Draws the roster to the specified graphics object.
     *
     * @param g the graphics object to draw the roster image to
     */
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int y = 0;
        for (Player player : players) {
            g2d.drawImage(new PlayerImage(player), 10, y, null);
            y += PlayerImage.HEIGHT;
        }
    }
}
