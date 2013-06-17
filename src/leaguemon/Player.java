package leaguemon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 *
 * @author Inconvenius
 */
public class Player implements Comparable<Player> {

    public enum League {
        NONE, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, MASTER, GRANDMASTER
    }

    public enum Race {
        TERRAN, PROTOSS, ZERG, RANDOM
    }

    public enum Position {
        NONE, TA, TL, DM
    }

    private String profile;
    private String name;
    private League league;
    private Race race;
    private Position position;

    public Player(String player) {
        String[] split = player.split(" ");

        this.profile = split[0];
        this.league = League.valueOf(split[1]);
        this.race = Race.valueOf(split[2]);
        this.position = Position.valueOf(split[3]);

        this.name = extractName(profile);
    }

    public static String extractName(String profile) {
        String temp = profile.substring(0, profile.length() - 1);
        int lastSlash = temp.lastIndexOf('/');

        return temp.substring(lastSlash + 1);
    }

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
