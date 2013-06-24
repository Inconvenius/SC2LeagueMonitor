package leaguemon.graphics;

import leaguemon.LeagueMonitor;
import leaguemon.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * The <code>PlayerImage</code> class represents an image with one player's
 * league, race, name and position in a team.
 *
 * @author Inconvenius
 */
public class PlayerImage extends BufferedImage {

    private final Player player;
    private BufferedImage league;
    private BufferedImage race;

    public static final int WIDTH = 200;
    public static final int HEIGHT = 33;

    /**
     * Class constructor specifying a player to draw the image for
     *
     * @param player the player whose information will be used to draw the image
     */
    public PlayerImage(Player player) {
        super(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        this.player = player;

        try {
            league = ImageIO.read(new File(LeagueMonitor.badgeImagesDir + player.getLeague().toString().toLowerCase() + LeagueMonitor.IMG_EXTENSION));
            race = ImageIO.read(new File(LeagueMonitor.raceImagesDir + player.getRace().toString().toLowerCase() + LeagueMonitor.IMG_EXTENSION));
        } catch (IOException e) {
            e.printStackTrace();
        }

        draw();
    }

    /**
     * Draws this image.
     */
    public void draw() {
        Graphics2D g2d = (Graphics2D) getGraphics();

        int gray = 30;
        g2d.setColor(new Color(gray,gray,gray));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setColor(Color.WHITE);

        g2d.drawImage(league, 0, 0, null);
        g2d.drawImage(race, 30, 0, null);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));

        if (player.getPosition().equals(Player.Position.NONE)) {
            g2d.setColor(Color.WHITE);
            g2d.drawString(player.getName(), 65, 21);
        } else if (player.getPosition().equals(Player.Position.TL)) {
            g2d.setColor(new Color(0,150,0));
            g2d.drawString(player.getName(), 65, 21);
            g2d.drawString(player.getPosition().toString(), 165, 21);
        } else if(player.getPosition().equals(Player.Position.DM)) {
            g2d.setColor(Color.cyan);
            g2d.drawString(player.getName(), 65, 21);
            g2d.drawString(player.getPosition().toString(), 165, 21);
        } else {
            g2d.setColor(new Color(0,200,0));
            g2d.drawString(player.getName(), 65, 21);
            g2d.drawString(player.getPosition().toString(), 165, 21);
        }
    }
}
