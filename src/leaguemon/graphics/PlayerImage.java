package leaguemon.graphics;

import leaguemon.LeagueMonitor;
import leaguemon.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Inconvenius
 */
public class PlayerImage extends BufferedImage {

    private final Player player;
    private BufferedImage league;
    private BufferedImage race;

    public PlayerImage(Player player) {
        super(200, 33, BufferedImage.TYPE_INT_ARGB);
        this.player = player;

        try {
            league = ImageIO.read(new File(LeagueMonitor.IMG_DIR + LeagueMonitor.LEAGUE_IMG_DIR + player.getLeague().toString().toLowerCase() + LeagueMonitor.IMG_EXTENSION));
            race = ImageIO.read(new File(LeagueMonitor.IMG_DIR + LeagueMonitor.RACE_IMG_DIR + player.getRace().toString().toLowerCase() + LeagueMonitor.IMG_EXTENSION));
        } catch (IOException e) {
            e.printStackTrace();
        }

        draw();
    }

    public void draw() {
        Graphics2D g2d = (Graphics2D) getGraphics();
        int gray = 30;
        g2d.setColor(new Color(gray,gray,gray));
        g2d.fillRect(0, 0, 200, 33);
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