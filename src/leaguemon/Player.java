package leaguemon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * The <code>Player</code> class represents a player with a name, race, league, team position and a bnet profile.
 * It provides methods for accessing these properties and querying the league from the bnet profile.
 *
 * @author Inconvenius
 */
public class Player implements Comparable<Player> {

    /** Specifies the possible leagues a player can be in. */
    public enum League {
        NONE, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, MASTER, GRANDMASTER
    }

    /** Specifies the possible races a player can play as. */
    public enum Race {
        TERRAN, PROTOSS, ZERG, RANDOM
    }

    /** Specifies the possible positions a player can hold in a team. */
    public enum Position {
        NONE, TA, TL, DM
    }

    private String profile;
    private String name;
    private League league;
    private Race race;
    private Position position;

    /**
     * Constructs a player object according to a line from the roster file.
     *
     * @param player an entry in the team roster file
     */
    public Player(String player) {
        String[] split = player.split(" ");

        this.profile = split[0];
        this.league = League.valueOf(split[1]);
        this.race = Race.valueOf(split[2]);
        this.position = Position.valueOf(split[3]);

        this.name = extractName(profile);
    }

    /**
     * Extracts a player's name from its bnet profile address.
     *
     * @param profile address to extract the name from
     * @return the name of the player with the specified bnet profile address
     */
    public static String extractName(String profile) {
        String temp = profile.substring(0, profile.length() - 1);
        int lastSlash = temp.lastIndexOf('/');

        return temp.substring(lastSlash + 1);
    }

    /**
     * Retrieves the current league of this player from its bnet profile.
     */
    public void updateLeagueFromBnetProfile() {
        Document doc;

        try {
            doc = Jsoup.connect(getProfile()).get();
        } catch (IOException e) {
            System.out.println("Unable to access profile for player " + getName());
            e.printStackTrace();
            return;
        }

        String portraitFrameClass = doc.getElementById("portrait-frame").className();

        if(portraitFrameClass.isEmpty()) {
            setLeague(League.NONE);
        } else {
            int lastDash = portraitFrameClass.lastIndexOf('-');
            String league = portraitFrameClass.substring(lastDash + 1);

            setLeague(League.valueOf(league.toUpperCase()));
        }
    }

    public String getName() {
        return name;
    }

    public String getProfile() {
        return profile;
    }

    public Race getRace() {
        return race;
    }

    public Position getPosition() {
        return position;
    }

    public League getLeague() {
        return league;
    }

    private void setLeague(League league) {
        this.league = league;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != Player.class)
            return false;

        Player p = (Player) obj;

        return p.getName().equals(getName());
    }

    @Override
    public int compareTo(Player o) {
        if(getPosition().ordinal() > o.getPosition().ordinal())
            return -1;

        if(getPosition().ordinal() < o.getPosition().ordinal())
            return 1;

        if(getLeague().ordinal() > o.getLeague().ordinal())
            return -1;

        if(getLeague().ordinal() < o.getLeague().ordinal())
            return 1;

        return 0;
    }
}
