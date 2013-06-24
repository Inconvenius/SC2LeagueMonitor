package leaguemon;

import leaguemon.graphics.RosterImage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The <code>Team</code> class represents a named team with a list of players. It provides methods
 * for sorting the players (according to bnet rank and position in the team) and writing the player list
 * to a text and image file.
 *
 * @author Inconvenius
 */
public class Team {

    /** List of players in this team */
    private List<Player> players = new ArrayList<Player>();

    /** Name of the team */
    private final String name;

    public Team(String name) {
        this.name = name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getName() {
        return name;
    }

    /**
     * Sorts the roster of this team according to position in the team and league.
     */
    public void sortRoster() {
        Collections.sort(players);
    }

    /**
     * Writes the roster file with the most recent info to the specified directory.
     *
     * @param dir directory to write the text file to
     */
    public void writeRosterTextFile(String dir) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(dir + getName() + ".txt"));

            for (Player player : getPlayers()) {
                out.write(player.getProfile() + " " + player.getLeague() + " "
                        + player.getRace() + " " + player.getPosition() + "\n");
            }

            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println("Error writing roster text file for team " + getName());
            e.printStackTrace();
        }
    }

    /**
     * Writes the roster image with the most recent info to the specified directory.
     *
     * @param dir directory to write the image to
     */
    public void writeRosterImageFile(String dir) {
        try {
            new RosterImage(players).writeImage(dir + getName() + LeagueMonitor.IMG_EXTENSION);
        } catch (IOException e) {
            System.out.println("Error writing roster image for team " + getName());
            e.printStackTrace();
        }
    }
}
