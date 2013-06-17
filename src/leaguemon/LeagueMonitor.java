package leaguemon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Inconvenius
 */
public class LeagueMonitor {

    public static final String IMG_EXTENSION = ".png";

    public static String badgeImagesDir;
    public static String raceImagesDir;

    private List<Team> teams = new ArrayList<Team>();

    private String rosterImageDir;
    private String teamsDir;

    public LeagueMonitor() throws IOException {
        loadConfig();

        System.out.print("Reading team info from file(s)...");
        teams = loadTeamInfo();
        System.out.println(" Done");
        System.out.println();
    }

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

    private ArrayList<Player> loadPlayerInfo(File playersFile) throws IOException {
        ArrayList<Player> players = new ArrayList<Player>();

        BufferedReader br = new BufferedReader(new FileReader(playersFile));
        String line;

        while ((line = br.readLine()) != null) {
            players.add(new Player(line));
        }

        return players;
    }

    public void start(int interval) throws IOException, InterruptedException {
        while (true) {
            boolean changed = false;

            for (Team team : teams) {
                System.out.println("Checking team " + team.getName() + "...");

                for (Player player : team.getPlayers()) {
                    System.out.print("Checking " + player.getName() + " ...");
                    Player.League currentLeague = getCurrentLeague(player.getProfile());

                    if(!currentLeague.equals(player.getLeague())) {
                        System.out.print(" Updating ...");
                        player.setLeague(currentLeague);
                        changed = true;
                    }

                    System.out.println(" Done");
                }

                if(changed) {
                    team.sortRoster();
                    team.writeRosterTextFile(teamsDir);
                    team.writeRosterImageFile(rosterImageDir);
                }

                System.out.println();
            }

            if(interval <= 0)
                break;

            Thread.sleep(1000 * 60 * interval);
        }
    }

    private Player.League getCurrentLeague(String address) throws IOException {
        Document doc = Jsoup.connect(address).get();

        String portraitFrameClass = doc.getElementById("portrait-frame").className();

        if(portraitFrameClass.isEmpty()) {
            return Player.League.NONE;
        }

        int lastDash = portraitFrameClass.lastIndexOf('-');
        String league = portraitFrameClass.substring(lastDash + 1);

        return Player.League.valueOf(league.toUpperCase());
    }

    // main ///////////////////////////////////////////////////////

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: monitor <interval>");
            return;
        }

        try {
            new leaguemon.LeagueMonitor().start(Integer.parseInt(args[0]));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
