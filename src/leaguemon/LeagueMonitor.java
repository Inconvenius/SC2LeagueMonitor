package leaguemon;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The <code>LeagueMonitor</code> class represents the league monitor that will generate rosters for players using
 * the current league information from their battle.net profiles.
 *
 * @author Inconvenius
 */
public class LeagueMonitor {

    public static final String IMG_EXTENSION = ".png";

    /** A directory containing the images for league badges */
    public static String badgeImagesDir;

    /** A directory containing the images for the races */
    public static String raceImagesDir;

    private List<Team> teams = new ArrayList<Team>();

    private String rosterImageDir;
    private String teamsDir;

    /**
     * Class constructor. Loads the configurations from config.properties file
     * and data from the player lists.
     */
    public LeagueMonitor() {
        loadConfig();

        System.out.print("Reading team info from file(s)...");
        try {
            teams = loadTeamInfo();
        } catch (IOException e) {
            System.out.print("Unable to load team info.");
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println(" Done");
        System.out.println();
    }

    /**
     * Loads configuration info from the config.properties file.
     */
    private void loadConfig() {
        Properties p = new Properties();

        try {
            p.load(new FileInputStream("config.properties"));

            rosterImageDir = p.getProperty("rosterImageDir");
            badgeImagesDir = p.getProperty("leagueBadgeImagesDir");
            raceImagesDir = p.getProperty("raceImagesDir");
            teamsDir = p.getProperty("teamsDir");

        } catch (IOException e) {
            System.out.println("Error loading configurations from config.properties file.");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Loads the team info from the directory containing the files specifying the
     * player lists. This directory is specified in the config.properties file.
     *
     * @return a list of the teams
     * @throws IOException if there is a problem reading the files
     */
    private ArrayList<Team> loadTeamInfo() throws IOException {
        File dir = new File(teamsDir);
        ArrayList<Team> teams = new ArrayList<Team>();

        for (File child : dir.listFiles()) {
            int suffixStart = child.getName().lastIndexOf('.');
            Team team = new Team(child.getName().substring(0, suffixStart));
            team.setPlayers(loadPlayerInfo(child));
            teams.add(team);
        }

        return teams;
    }

    /**
     * Loads the players' info from a single file.
     *
     * @param playersFile a file containing a list of players
     * @return a list of players that were in the specified file
     * @throws IOException if there is a problem reading the file
     */
    private ArrayList<Player> loadPlayerInfo(File playersFile) throws IOException {
        ArrayList<Player> players = new ArrayList<Player>();

        BufferedReader br = new BufferedReader(new FileReader(playersFile));
        String line;

        while ((line = br.readLine()) != null) {
            players.add(new Player(line));
        }

        return players;
    }

    /**
     * Starts the monitor with the specified interval.
     *
     * @param interval the time in minutes to wait between each check
     * @throws InterruptedException if the thread gets interrupted
     */
    public void start(int interval) throws InterruptedException {
        while (true) {
            for (Team team : teams) {
                System.out.println("Checking team " + team.getName() + "...");
                checkTeam(team);
                System.out.println();
            }

            if(interval <= 0)
                break;

            Thread.sleep(1000 * 60 * interval);
        }
    }

    /**
     * For each player in the specified team checks if that player's league has changed since
     * last time, and updates it if it has.
     *
     * @param team the team whose players to check
     */
    private void checkTeam(Team team) {
        boolean changed = false;

        for (Player player : team.getPlayers()) {
            System.out.print("Checking " + player.getName() + " ...");
            Player.League currentLeague = player.getLeague();
            player.updateLeagueFromBnetProfile();

            if(!currentLeague.equals(player.getLeague())) {
                System.out.print(" Updated ...");
                changed = true;
            }

            System.out.println(" Done");
        }

        if(changed) {
            updateRosterFiles(team);
        }
    }

    /**
     * Updates the roster image and text files with the most recent info for the specified team.
     *
     * @param team the team whose roster files to update
     */
    private void updateRosterFiles(Team team) {
        team.sortRoster();
        team.writeRosterTextFile(teamsDir);

        System.out.print("Writing roster to: " + rosterImageDir + team.getName() + LeagueMonitor.IMG_EXTENSION + " ...");
        team.writeRosterImageFile(rosterImageDir);
        System.out.println(" Done");
    }

    // main ///////////////////////////////////////////////////////

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: monitor <interval>");
            return;
        }

        try {
            new LeagueMonitor().start(Integer.parseInt(args[0]));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
